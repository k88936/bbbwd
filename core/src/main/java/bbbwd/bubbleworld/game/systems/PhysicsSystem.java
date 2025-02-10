package bbbwd.bubbleworld.game.systems;

import bbbwd.bubbleworld.game.components.BoxCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.box2d.structs.*;
import com.badlogic.gdx.box2d.utils.Box2dWorldTaskSystem;
import com.badlogic.gdx.jnigen.runtime.closure.ClosureObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;

import java.util.Arrays;

@All({TransformCM.class, BoxCM.class})
public class PhysicsSystem extends BaseEntitySystem implements Disposable {
    private static final int workerCount = Runtime.getRuntime().availableProcessors();
    private final b2WorldId worldId;
    private final Box2dWorldTaskSystem taskSystem;
    public ComponentMapper<TransformCM> transformMapper;
    public ComponentMapper<BoxCM> boxMapper;
//    public ComponentMapper<VisibleCM> visibleMapper;

    public PhysicsSystem() {

        b2WorldDef worldDef = Box2d.b2DefaultWorldDef();
        worldDef.gravity().y(0);
        worldDef.enableSleep(true);
        taskSystem = Box2dWorldTaskSystem.createForWorld(worldDef, workerCount, Thread.currentThread()::interrupt);
        worldId = Box2d.b2CreateWorld(worldDef.asPointer());
    }

    @Override
    protected void processSystem() {

        Box2d.b2World_Step(getWorldId(), 1 / 60f, 4);
        taskSystem.afterStep();
        ThreadLocal<b2Transform> cache = ThreadLocal.withInitial(b2Transform::new);
        Arrays.stream(subscription.getEntities().getData(), 0, subscription.getEntities().size()).parallel().forEach(entityId -> {
            BoxCM physics = boxMapper.get(entityId);
            if (physics.isStatic) return;
            Box2dPlus.b2Body_GetTransform(physics.bodyId, cache.get());
            TransformCM transformCM = transformMapper.get(entityId);
            Box2dPlus.b2ToGDX(cache.get(), transformCM.transform);
        });
    }

    public void collect(float lx, float ly, float ux, float uy, IntArray entities) {
        Box2dPlus.b2WorldOverlapAABBbyEntity(getWorldId(), lx, ly, ux, uy, ClosureObject.fromClosure(new Box2dPlus.EntityCallback() {
//            final b2Transform cache = new b2Transform();

            @Override
            public boolean b2OverlapResultFcn_call(long entity) {
                int entityId = (int) entity;
//                BoxCM physics = boxMapper.get(entityId);
//                Box2dPlus.b2Body_GetTransform(physics.bodyId, cache);
//                TransformCM transformCM = transformMapper.get(entityId);
//                Box2dPlus.b2ToGDX(cache, transformCM.transform);
                entities.add(entityId);
//                System.out.println(transformCM.transform);
                return true;
            }
        }));
    }

    public b2BodyId createBox(int entity) {
        //todo can inline to a jnicall
        TransformCM transformCM = transformMapper.get(entity);
        BoxCM boxCM = boxMapper.get(entity);
        boxCM.bodyId = Box2dPlus.b2CreateBlock(worldId, transformCM.transform, boxCM.size);
        Box2dPlus.b2BodySetRawUserData(boxCM.bodyId, entity);
        return boxCM.bodyId;
    }

    public b2JointId connectByWeld(int entityA, int entityB, Vector2 localAnchorA, Vector2 localAnchorB, float referenceAngle) {
        BoxCM boxA = boxMapper.get(entityA);
        BoxCM boxB = boxMapper.get(entityB);
        b2JointId id = Box2dPlus.b2ConnectBlockByWeldJoint(worldId, boxA.bodyId, boxB.bodyId, localAnchorA, localAnchorB, referenceAngle);
        return id;
    }
    public b2JointId connectByRevolute(int entityA, int entityB, Vector2 localAnchorA, Vector2 localAnchorB, float limitLower, float limitUpper,float maxTorch) {
        BoxCM boxA = boxMapper.get(entityA);
        BoxCM boxB = boxMapper.get(entityB);
        b2JointId id = Box2dPlus.b2ConnectBlockByRevoluteJoint(worldId, boxA.bodyId, boxB.bodyId, localAnchorA, localAnchorB, limitLower, limitUpper, maxTorch);
        Box2d.b2RevoluteJoint_SetMotorSpeed(id,0.5f);
        return id;
    }
//    public b2JointId connectByRevolute(int entityA, int entityB, Vector2 center, float limitLower, float limitUpper,float maxTorch) {
//        BoxCM boxA = boxMapper.get(entityA);
//        BoxCM boxB = boxMapper.get(entityB);
//        return Box2dPlus.b2ConnectBlockByRevoluteJoint(worldId, boxA.bodyId, boxB.bodyId, center,center,limitLower,limitUpper,maxTorch);
//    }
//    public b2JointId connectByRevolute(int entityA, int entityB, Vector2 center, float limit,float maxTorch) {
//        BoxCM boxA = boxMapper.get(entityA);
//        BoxCM boxB = boxMapper.get(entityB);
//        return Box2dPlus.b2ConnectBlockByRevoluteJoint(worldId, boxA.bodyId, boxB.bodyId, center,center,limit,limit,maxTorch);
//    }


//    public void createBody()


    @Override
    public void dispose() {
        Box2d.b2DestroyWorld(getWorldId());
        taskSystem.dispose();
    }

    public void explode(Vector2 position, float radius, float falloff, float power) {
        Box2dPlus.b2WorldExplode(worldId, position, radius, falloff, power);
    }

    public b2WorldId getWorldId() {
        return worldId;
    }
}
