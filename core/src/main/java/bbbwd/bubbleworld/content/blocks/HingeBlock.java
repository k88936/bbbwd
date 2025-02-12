package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.physics.JointCM;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import com.badlogic.gdx.box2d.structs.b2JointId;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class HingeBlock extends ComposedBlock {

    Vector2 connectionPoint=new Vector2(0,0);
    float limitLower=-MathUtils.HALF_PI;
    float limitUpper=MathUtils.HALF_PI;
    float maxTorch=1;

    @Override
    public void compose(int entityA, int entityB, int baseEntity) {
        b2JointId b2JointId = Vars.ecs.getSystem(PhysicsSystem.class).connectByRevolute(entityA, entityB, connectionPoint, connectionPoint, limitLower, limitUpper, maxTorch);
        JointCM jointCM = Vars.ecs.getMapper(JointCM.class).create(baseEntity);
        jointCM.jointId = b2JointId;
        jointCM.entityA = entityA;
        jointCM.entityB = entityB;

    }
}
