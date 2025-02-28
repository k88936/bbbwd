package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.core.render.RenderLogic;
import bbbwd.bubbleworld.core.render.Renderer;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.game.components.physics.JointCM;
import bbbwd.bubbleworld.game.systems.device.JointDeviceUpdateSystem;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Blocks {

    public static Array<Block> blocks = new Array<>();

    public static ObjectMap<Block.BlockType, Array<Block>> blockTypeMap = new ObjectMap<>();

    public static Block testBlock, testBlock_OnlyConnectX, testHingeBlock, saw;

    public static void load() {
        for (Block.BlockType value : Block.BlockType.values()) {
            blockTypeMap.put(value, new Array<>());
        }
        Block.defaultRenderLogic = RenderLogic.of("test_block", Block.defaultSize);


//        Gdx.app.log("Blocks", "Loading testBlock");
        testBlock = new Block() {
            @Override
            void config() {
                blockType = BlockType.basic;
                connectFilter = ConnectFilter.always;
                shape = ShapeBox;
            }
        };
//        Gdx.app.log("Blocks", "Loading testBlock_OnlyConnectX");
        testBlock_OnlyConnectX = new Block() {
            @Override
            void config() {
                blockType = BlockType.basic;
                connectFilter = ConnectFilter.allow(true, true, false, false);
                shape = ShapeBox;
            }
        };
//        Gdx.app.log("Blocks", "Loading testHingeBlock");
        testHingeBlock = new HingeBlock() {
            @Override
            void config() {
                size = 0.5f;
                A = new Block() {
                    @Override
                    void config() {
                        connectFilter = ConnectFilter.allow(false, false, false, true);
                        shape = ShapePolygon("hinge_l");
                        renderLogic = RenderLogic.of("hinge_l", Renderer.Layer.BLOCK_LOWER, size);
                    }
                };
                B = new Block() {
                    @Override
                    void config() {
                        shape = ShapePolygon("hinge_u");
                        connectFilter = ConnectFilter.allow(false, false, true, false);
                        renderLogic = RenderLogic.of("hinge_u", Renderer.Layer.BLOCK_UPPER, size);
                    }
                };
            }
        };

//        Gdx.app.log("Blocks", "Loading saw");
        saw = new RevoluteBlock() {
            @Override
            void config() {
                A = new Block() {
                    @Override
                    void config() {
                        connectFilter = ConnectFilter.never;
                        shape = ShapeCircle;
                        renderLogic = RenderLogic.of("saw_l", Renderer.Layer.BLOCK_LOWER, size);
                    }
                };
                B = new Block() {
                    @Override
                    void config() {
                        connectFilter = ConnectFilter.allow(false, false, false, true);
                        shape = ShapePolygon("saw_u");
                        renderLogic = RenderLogic.of("saw_u", Renderer.Layer.BLOCK_UPPER, size);
                    }
                };

                physicsUpdateLogic = new JointDeviceUpdateSystem.PhysicsUpdateLogic() {
                    @Override
                    public void update(DeviceCM deviceCM, JointCM jointCM) {
                        deviceCM.memory[0] = Box2d.b2RevoluteJoint_GetAngle(jointCM.jointId);
                        Box2d.b2RevoluteJoint_SetMotorSpeed(jointCM.jointId, 1);
                    }
                };
            }
        };
    }
}
