package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.Renderer;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.game.components.physics.JointCM;
import bbbwd.bubbleworld.game.systems.device.JointDeviceUpdateSystem;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;

public abstract class ComposedBlock extends Block {

    static final Block defaultBlock = new Block() {
        @Override
        void config() {

        }
    };
    static final JointDeviceUpdateSystem.PhysicsUpdateLogic DEFAULT_PHYSICS_UPDATE_LOGIC = new JointDeviceUpdateSystem.PhysicsUpdateLogic() {
        @Override
        public void update(DeviceCM deviceCM, JointCM jointCM) {

        }
    };
    public Block A;
    public Block B;


    public JointDeviceUpdateSystem.PhysicsUpdateLogic physicsUpdateLogic;

    @Override
    void setupDefault() {
        super.setupDefault();
        A = defaultBlock;
        B = defaultBlock;
        physicsUpdateLogic = DEFAULT_PHYSICS_UPDATE_LOGIC;
    }

    @Override
    void interOtherSetting() {

        super.interOtherSetting();
        A.size = size;
        B.size = size;
        renderLogic = new Renderer.SimpleRenderLogic(A.renderLogic, B.renderLogic);
    }

    @Override
    public int create(Affine2 transform) {
        assert (MathUtils.isEqual(size * 4, MathUtils.round(size * 4)));
        int A = this.A.create(transform);
        int B = this.B.create(transform);
        int entity = Vars.ecs.create();
        Vars.ecs.getMapper(DeviceCM.class).create(entity).logic = physicsUpdateLogic;
        compose(A, B, entity);
        return entity;
    }

    public abstract void compose(int entityA, int entityB, int baseEntity);
}
