package bbbwd.bubbleworld.core;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.content.blocks.ComposedBlock;
import bbbwd.bubbleworld.game.components.physics.JointCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.components.physics.DynamicBodyCM;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import bbbwd.bubbleworld.input.InputHandler;
import bbbwd.bubbleworld.utils.Job;
import bbbwd.bubbleworld.utils.Utils;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.box2d.structs.b2Transform;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.logging.Logger;

public class Control {
    public static Job init;
    public InputHandler inputHandler;
    public boolean isGameRunning = false;

    /**
     * @param position world position
     * @param newBlock the block to be built
     * @param rot
     * @return null if no place to build
     */
    public static SeekResult seekPlaceForBuild(Vector2 position, Block newBlock, int rot) {
        b2WorldId worldId = Vars.ecs.getSystem(PhysicsSystem.class).getWorldId();
//        Viewport viewport = Vars.renderer.viewport;

//        Vector2 world_touch = viewport.unproject(new Vector2(position));
        ArrayList<Connection> nearBy = new ArrayList<>(10);
        float extend = newBlock.size * 2 * 1.5f;


        //seek possible
        Box2dPlus.b2WorldOverlapAABBbyEntity(worldId, position.x - extend, position.y - extend, position.x + extend, position.y + extend, new Box2dPlus.EntityCallback() {
            final b2Transform cache = new b2Transform();

            @Override
            public boolean b2OverlapResultFcn_call(long entity) {
                int entityId = (int) entity;

                Logger.getGlobal().info("check entity: " + entityId);
                TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entityId);
                Affine2 inv = new Affine2(transformCM.transform).inv();
                Vector2 local = new Vector2(position);
                inv.applyTo(local);
                DynamicBodyCM dynamicBodyCM = Vars.ecs.getMapper(DynamicBodyCM.class).get(entityId);

                //ret
                Affine2 tfm = null;
                float min = Float.MAX_VALUE;
                Vector2 localGridPos = new Vector2();

                float edge = dynamicBodyCM.type.size + newBlock.size;
                for (float lx = -edge; lx <= edge; lx += Vars.GRID_SIZE * 2) {
                    for (float ly = -edge; ly <= edge; ly += Vars.GRID_SIZE * 2) {
                        if (Math.abs(lx) == edge && Math.abs(ly) == edge)
                            continue;
                        if (lx > -edge && lx < edge && ly > -edge && ly < edge)
                            continue;
                        float cur = (lx - local.x) * (lx - local.x) + (ly - local.y) * (ly - local.y);
                        if (dynamicBodyCM.type.connectFilter.filterOut(newBlock, lx, ly)) continue;
                        if (cur >= min) continue;
                        final boolean[] ok = {false};
                        Affine2 tmp = new Affine2(transformCM.transform).translate(lx, ly);
                        newBlock.shape.overlap(newBlock, tmp, entity1 -> {
                            ok[0] = true;
                            return false;
                        });
                        if (ok[0]) {
                            Logger.getGlobal().info("overlap test failed at: " + lx + ',' + ly);
                            continue;
                        }

                        localGridPos.set(lx, ly);
                        tfm = tmp;
                        min = cur;
                        Logger.getGlobal().info("renewed best: " + lx + ',' + ly);
                    }
                }

                if (min == Float.MAX_VALUE) return true;
                Connection connection = new Connection();
                connection.prefer = min;
                connection.oldBoxEntity = entityId;
                connection.size = dynamicBodyCM.type.size;
                connection.oldBlockTfm = transformCM.transform;
                connection.newPosRelativeToOld.set(localGridPos);
                connection.newBlockTfm.set(tfm);
                nearBy.add(connection);
                return true;
            }
        });

        if (nearBy.isEmpty()) {
            return null;
        }

        //determine the best candidate and compute other connections
        nearBy.sort((o1, o2) -> Float.compare(o1.prefer, o2.prefer));
        Connection first = nearBy.getFirst();
        Affine2 transform = new Affine2(first.newBlockTfm);
        for (int i = 0; i < rot % 4; i++) {
            Utils.rotateHalfPi(transform);
        }

        Affine2 inv = transform.inv();
        Vector2 newBlockPos = new Vector2(first.newBlockTfm.m02, first.newBlockTfm.m12);
        ArrayList<Connection> connections = new ArrayList<>(9);
        for (Connection connection : nearBy) {
            if (newBlockPos.dst2(connection.newBlockTfm.m02, connection.newBlockTfm.m12) > Vars.GRID_SIZE * Vars.GRID_SIZE)
                continue;
            Vector2 oldRelativeToNew = new Vector2(connection.oldBlockTfm.m02, connection.oldBlockTfm.m12);
            inv.applyTo(oldRelativeToNew);

            Utils.Gridize(oldRelativeToNew, newBlock.size + connection.size);
            if (newBlock instanceof ComposedBlock composedBlock) {
                boolean a = composedBlock.A.connectFilter.filterOut(newBlock, oldRelativeToNew.x, oldRelativeToNew.y);
                boolean b = composedBlock.B.connectFilter.filterOut(newBlock, oldRelativeToNew.x, oldRelativeToNew.y);
                if (a && b) continue;
                if (a)
                    connection.newBoxEntityMapper = ((id) -> Vars.ecs.getMapper(JointCM.class).get(id).entityB);
                else if (b)
                    connection.newBoxEntityMapper = ((id) -> Vars.ecs.getMapper(JointCM.class).get(id).entityA);

            } else {
                if (newBlock.connectFilter.filterOut(newBlock, oldRelativeToNew.x, oldRelativeToNew.y)) continue;
                connection.newBoxEntityMapper = (id -> id);
            }

            //compute the anchor and angle

            //scaled
            oldRelativeToNew.scl(newBlock.size / (connection.size + newBlock.size));
            if (Math.abs(oldRelativeToNew.x) > Math.abs(oldRelativeToNew.y)) {
                connection.anchorNewBlock.set(Math.copySign(newBlock.size, oldRelativeToNew.x), oldRelativeToNew.y);
            } else {
                connection.anchorNewBlock.set(oldRelativeToNew.x, Math.copySign(newBlock.size, oldRelativeToNew.y));
            }

            Utils.Gridize(connection.newPosRelativeToOld, newBlock.size + connection.size);
            //scaled
            connection.newPosRelativeToOld.scl(connection.size / (connection.size + newBlock.size));
            if (Math.abs(connection.newPosRelativeToOld.x) > Math.abs(connection.newPosRelativeToOld.y)) {
                connection.anchorOldBlock.set(Math.copySign(connection.size, connection.newPosRelativeToOld.x), connection.newPosRelativeToOld.y);
            } else {
                connection.anchorOldBlock.set(connection.newPosRelativeToOld.x, Math.copySign(connection.size, connection.newPosRelativeToOld.y));
            }

//            connection.relativeAngle=Utils.computeRotReference(connection.oldBlockTfm,first.newBlockTfm);
            connection.relativeAngle = 0;

            connections.add(connection);
        }
        if (connections.isEmpty()) return null;//in case of cannot connect to its host
        return new SeekResult(first.newBlockTfm, newBlock, connections);

    }

    public static int buildBlock(Affine2 transform, Block newBlock) {

        int entity = newBlock.create(transform);

        return entity;
    }

    public static void buildAndConnect(SeekResult seekResult) {
        int id = buildBlock(seekResult.transform(), seekResult.type());
        for (Connection connection : seekResult.connections()) {
            Vars.ecs.getSystem(PhysicsSystem.class)
                   .connectByWeld(connection.newBoxEntityMapper.apply(id), connection.oldBoxEntity,
                    connection.anchorNewBlock, connection.anchorOldBlock,
                    connection.relativeAngle);
            Logger.getGlobal().info(connection.toString());
            break;// or many bug

        }
    }

    public void startGame() {
        WorldConfiguration config = new WorldConfigurationBuilder()
            .with(new PhysicsSystem())
            .build();
        Vars.ecs = new World(config);
        isGameRunning = true;
    }


    public record SeekResult(Affine2 transform, Block type, ArrayList<Connection> connections) {
    }

    public static final class Connection {
        public Vector2 anchorNewBlock = new Vector2();
        public Vector2 anchorOldBlock = new Vector2();
        public float relativeAngle;
        float prefer;
        //to solve composed block connection
        Function<Integer, Integer> newBoxEntityMapper;
        int oldBoxEntity;
        float size;
        Affine2 oldBlockTfm = new Affine2();
        Affine2 newBlockTfm = new Affine2();
        Vector2 newPosRelativeToOld = new Vector2();

        @Override
        public String toString() {
            return "Connection{" +
                "prefer=" + prefer +
                ", newBoxEntityMapper=" + newBoxEntityMapper +
                ", oldBoxEntity=" + oldBoxEntity +
                ", size=" + size +
                ", oldBlockTfm=" + oldBlockTfm +
                ", newBlockTfm=" + newBlockTfm +
                ", relative=" + newPosRelativeToOld +
                ", anchorNewBlock=" + anchorNewBlock +
                ", anchorOldBlock=" + anchorOldBlock +
                ", relativeAngle=" + relativeAngle +
                '}';
        }
    }
}
