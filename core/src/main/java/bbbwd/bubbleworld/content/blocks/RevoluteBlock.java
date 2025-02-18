package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.game.components.physics.JointCM;
import bbbwd.bubbleworld.game.systems.device.JointDeviceUpdateSystem;
import bbbwd.bubbleworld.game.systems.physics.PhysicsSystem;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.structs.b2JointId;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class RevoluteBlock extends ComposedBlock {


    Vector2 connectionPoint = new Vector2(0, 0);
    float maxTorch = 1;

    RevoluteBlock() {
        init();
    }

    @Override
    void init() {
        physicsUpdateLogic = new JointDeviceUpdateSystem.PhysicsUpdateLogic() {
            @Override
            public void update(DeviceCM deviceCM, JointCM jointCM) {
                deviceCM.memory[0] = Box2d.b2RevoluteJoint_GetAngle(jointCM.jointId);
                Box2d.b2RevoluteJoint_SetMotorSpeed(jointCM.jointId, (float) deviceCM.memory[1]);
            }
        };
        super.init();
    }

    @Override
    public void compose(int entityA, int entityB, int baseEntity) {
        b2JointId b2JointId = Vars.ecs.getSystem(PhysicsSystem.class).connectByRevolute(entityA, entityB, connectionPoint, connectionPoint);

        Box2d.b2RevoluteJoint_SetMaxMotorTorque(b2JointId, maxTorch);
        JointCM jointCM = Vars.ecs.getMapper(JointCM.class).create(baseEntity);
        jointCM.jointId = b2JointId;
        jointCM.entityA = entityA;
        jointCM.entityB = entityB;

    }
}
