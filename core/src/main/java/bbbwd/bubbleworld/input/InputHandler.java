package bbbwd.bubbleworld.input;

import bbbwd.bubbleworld.Utils;
import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.game.components.BoxCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.box2d.structs.b2Transform;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.jnigen.runtime.closure.ClosureObject;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class InputHandler {

    /**
     * @param position world position
     * @param newBlock the block to be built
     * @param rot
     * @return null if no place to build
     */
    public seekResult seekPlaceForBuild(Vector2 position, Block newBlock, int rot) {
        b2WorldId worldId = Vars.ecs.getSystem(PhysicsSystem.class).getWorldId();
//        Viewport viewport = Vars.renderer.viewport;

//        Vector2 world_touch = viewport.unproject(new Vector2(position));
        ArrayList<candidate> nearBy = new ArrayList<>(10);
        float thisSize = newBlock.size;
        float extend = thisSize * 2 * 1.5f;


        //seek possible
        Box2dPlus.b2WorldOverlapAABBbyEntity(worldId, position.x - extend, position.y - extend, position.x + extend, position.y + extend, ClosureObject.fromClosure(new Box2dPlus.EntityCallback() {
            final b2Transform cache = new b2Transform();

            @Override
            public boolean b2OverlapResultFcn_call(long entity) {
                int entityId = (int) entity;
                TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entityId);
                Affine2 inv = new Affine2(transformCM.transform).inv();
                Vector2 local = new Vector2(position);
                inv.applyTo(local);
                BoxCM boxCM = Vars.ecs.getMapper(BoxCM.class).get(entityId);

                //ret
                Affine2 tfm = null;
                float min = Float.MAX_VALUE;
                Vector2 localGridPos = new Vector2();

                float edge = boxCM.size + thisSize;
                for (float lx = -edge; lx <= edge; lx += Vars.GRID_SIZE*2) {
                    for (float ly = -edge; ly <= edge; ly += Vars.GRID_SIZE*2) {
                        if (Math.abs(lx) == edge && Math.abs(ly) == edge)
                            continue;
                        if (lx > -edge && lx < edge && ly > -edge && ly < edge)
                            continue;
                        float cur = (lx - local.x) * (lx - local.x) + (ly - local.y) * (ly - local.y);
                        if (boxCM.connectFilter.filter(newBlock, lx, ly)) continue;
                        if (cur >= min) continue;
                        final boolean[] ok = {true};
                        Affine2 tmp = new Affine2(transformCM.transform).translate(lx, ly);
                        Box2dPlus.b2WorldOverlapSquare(worldId, thisSize * 0.95f, tmp, ClosureObject.fromClosure(new Box2dPlus.EntityCallback() {
                            @Override
                            public boolean b2OverlapResultFcn_call(long entity) {
                                ok[0] = false;
                                return false;
                            }
                        }));
//                        Vars.logger.info("lx: " + lx + " ly: " + ly + " cur: "+cur);
                        if (!ok[0]) continue;
                        localGridPos.set(lx, ly);
                        tfm = tmp;
                        min = cur;
                    }
                }

                if (min == Float.MAX_VALUE) return true;


                nearBy.add(new candidate(min, entityId, boxCM.size, new Vector2(transformCM.transform.m02, transformCM.transform.m12), tfm, localGridPos, new Vector2(), new Vector2(), new float[1]));
                return true;
            }
        }));

        if (nearBy.isEmpty()) {
            return null;
        }

        //determine the best candidate and compute other connections
        nearBy.sort((o1, o2) -> Float.compare(o1.prefer, o2.prefer));
        candidate first = nearBy.get(0);
        Affine2 transform = new Affine2(first.tfm);
        for (int i = 0; i < rot % 4; i++) {
            Utils.rotateHalfPi(transform);
        }
        Affine2 inv = transform.inv();
        Vector2 newBlockPos = new Vector2(first.tfm.m02, first.tfm.m12);
        ArrayList<candidate> connections = new ArrayList<>(9);
        for (InputHandler.candidate candidate : nearBy) {
            if (newBlockPos.dst2(candidate.tfm.m02, candidate.tfm.m12) > Vars.GRID_SIZE * Vars.GRID_SIZE)
                continue;
            Vector2 relative = new Vector2(candidate.pos);
            inv.applyTo(relative);
            Utils.Gridize(relative, newBlock.size + candidate.size);
            if (newBlock.connectFilter.filter(newBlock, relative.x, relative.y)) continue;
            //compute the anchor and angle
            if (Math.abs(relative.x) > Math.abs(relative.y)) {
                candidate.anchorNewBlock.set(Math.copySign(newBlock.size, relative.x), relative.y);
            } else {
                candidate.anchorNewBlock.set(relative.x, Math.copySign(newBlock.size, relative.y));
            }
            Utils.Gridize(candidate.relative, newBlock.size + candidate.size);
            if (Math.abs(candidate.relative.x) > Math.abs(candidate.relative.y)) {
                candidate.anchorOldBlock.set(Math.copySign(candidate.size, candidate.relative.x), candidate.relative.y);
            } else {
                candidate.anchorOldBlock.set(candidate.relative.x, Math.copySign(candidate.size, candidate.relative.y));
            }

            candidate.relativeAngle[0] = new Vector2(candidate.relative).sub(candidate.anchorOldBlock).angleDeg(candidate.anchorOldBlock)* MathUtils.degreesToRadians;

            connections.add(candidate);
        }
        if (connections.isEmpty()) return null;//in case of cannot connect to its host
        return new seekResult(first.tfm, connections);

    }


    public record seekResult(Affine2 transform, ArrayList<candidate> connections) {
    }

    public record candidate(float prefer, int entity, float size, Vector2 pos, Affine2 tfm, Vector2 relative,
                            Vector2 anchorNewBlock, Vector2 anchorOldBlock, float[] relativeAngle) {
    }
}
