package bbbwd.bubbleworld.game.systems;

import bbbwd.bubbleworld.game.components.BoxCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.components.VisibleCM;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.box2d.enums.b2BodyType;
import com.badlogic.gdx.box2d.structs.*;
import com.badlogic.gdx.box2d.utils.Box2dWorldTaskSystem;
import com.badlogic.gdx.jnigen.runtime.closure.ClosureObject;
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
        });
    }

    public void markVisible(float lx, float ly, float ux, float uy, IntArray entities) {
        Box2dPlus.b2WorldOverlapAABB(getWorldId(),  lx, ly,ux,uy, ClosureObject.fromClosure(new Box2dPlus.EntityCallback() {
            final b2Transform cache = new b2Transform();

            @Override
            public boolean b2OverlapResultFcn_call(long entity) {
                int entityId = (int) entity;
                BoxCM physics = boxMapper.get(entityId);
                Box2dPlus.b2Body_GetTransform(physics.bodyId, cache);
                TransformCM transformCM = transformMapper.get(entityId);
                Box2dPlus.b2ToGDX(cache, transformCM.transform);
                entities.add(entityId);
//                System.out.println(transformCM.transform);
                return true;
            }
        }));
    }

   public void createBody(int entity) {
        //todo can inline to a jnicall
        TransformCM transformCM = transformMapper.get(entity);
        BoxCM boxCM = boxMapper.get(entity);
        b2BodyDef def = Box2d.b2DefaultBodyDef();
        Box2dPlus.GDXTob2(transformCM.transform, def.position());
        Box2dPlus.GDXTob2(transformCM.transform, def.rotation());
        def.type(b2BodyType.b2_dynamicBody);
        b2Polygon b2Polygon = Box2d.b2MakeSquare(boxCM.size);
        b2ShapeDef shape = Box2d.b2DefaultShapeDef();
        boxCM.bodyId = Box2d.b2CreateBody(worldId, def.asPointer());
        Box2dPlus.b2Body_SetRawUserData(boxCM.bodyId, entity);
        Box2d.b2CreatePolygonShape(boxCM.bodyId, shape.asPointer(), b2Polygon.asPointer());
    }


    @Override
    public void dispose() {
        Box2d.b2DestroyWorld(getWorldId());
        taskSystem.dispose();
    }

    public b2WorldId getWorldId() {
        return worldId;
    }
}
