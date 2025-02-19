package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.physics.JointCM;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.math.MathUtils;

public abstract class HingeBlock extends RevoluteBlock {
    HingeBlock() {
        super();
    }

    float limitLower = -MathUtils.HALF_PI;
    float limitUpper = MathUtils.HALF_PI;

    @Override
    public void compose(int entityA, int entityB, int baseEntity) {
        super.compose(entityA, entityB, baseEntity);
        JointCM jointCM = Vars.ecs.getMapper(JointCM.class).get(baseEntity);
        Box2d.b2RevoluteJoint_EnableLimit(jointCM.jointId, true);
        Box2d.b2RevoluteJoint_SetLimits(jointCM.jointId, limitLower, limitUpper);
    }
}
