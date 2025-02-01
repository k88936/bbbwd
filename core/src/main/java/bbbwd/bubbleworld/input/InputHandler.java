package bbbwd.bubbleworld.input;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.BoxCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.box2d.structs.b2Transform;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.jnigen.runtime.closure.ClosureObject;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class InputHandler {

    protected seekResult seekPlaceForBuild(Vector2 position, Block newBlock) {
        b2WorldId worldId = Vars.ecs.getSystem(PhysicsSystem.class).getWorldId();
        Viewport viewport = Vars.renderer.viewport;

        Vector2 world_touch = viewport.unproject(new Vector2(position));
        ArrayList<candidate> nearBy = new ArrayList<>(10);
        float thisSize = newBlock.size;
        float extend = thisSize * 2 * 1.5f;
        final float gridSize = 0.25f;


        Box2dPlus.b2WorldOverlapAABB(worldId, world_touch.x - extend, world_touch.y - extend, world_touch.x + extend, world_touch.y + extend, ClosureObject.fromClosure(new Box2dPlus.EntityCallback() {
            final b2Transform cache = new b2Transform();

            @Override
            public boolean b2OverlapResultFcn_call(long entity) {
                int entityId = (int) entity;
                TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entityId);
                Affine2 inv = new Affine2(transformCM.transform).inv();
                Vector2 local = new Vector2(world_touch);
                inv.applyTo(local);
                BoxCM boxCM = Vars.ecs.getMapper(BoxCM.class).get(entityId);

                //ret
                Affine2 tfm = null;
                float min = Float.MAX_VALUE;
                Vector2 localGridPos = new Vector2();

                float edge = boxCM.size + thisSize;
                for (float lx = -edge; lx <= edge; lx += gridSize) {
                    for (float ly = -edge; ly <= edge; ly += gridSize) {
                        if (Math.abs(lx) == edge && Math.abs(ly) == edge)
                            continue;
                        if (lx > -edge && lx < edge && ly > -edge && ly < edge)
                            continue;
                        float cur = (lx - local.x) * (lx - local.x) + (ly - local.y) * (ly - local.y);
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
                        if (!ok[0]) continue;
                        localGridPos.set(lx, ly);
                        tfm = tmp;
                        min = cur;
                    }
                }

                if (min == Float.MAX_VALUE) return true;

                Vector2 worldGridPos = new Vector2(localGridPos);
                transformCM.transform.applyTo(worldGridPos);

                nearBy.add(new candidate(min, entityId, worldGridPos, tfm));
                return true;
            }
        }));

        if (nearBy.isEmpty()) {
            return null;
        }
IntArray connections = new IntArray(16);
        nearBy.sort((o1, o2) -> Float.compare(o1.prefer, o2.prefer));

        candidate first = nearBy.getFirst();
        System.out.println(nearBy.size());
        for (InputHandler.candidate candidate : nearBy) {

            float v = candidate.worldGridPos.dst2(first.worldGridPos);
            System.out.println(v);
            if (v < gridSize * gridSize * 0.25) {
                connections.add(candidate.entity);
            }


        }
        return new seekResult(first.tfm, connections);

    }



    protected record seekResult(Affine2 transform, IntArray connections) {
    }

    public record candidate(float prefer, int entity, Vector2 worldGridPos, Affine2 tfm) {
    }
}
