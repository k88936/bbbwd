package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.Renderer;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.game.components.physics.JointCM;
import bbbwd.bubbleworld.game.systems.device.JointDeviceUpdateSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Blocks {

    public static Array<Block> blocks = new Array<>();

    public static ObjectMap<Block.BlockType, Array<Block>> blockTypeMap = new ObjectMap<>();

    public static Block testBlock;
    public static Block testBlock_OnlyConnectX;
    public static Block testHingeBlock;
    public static Block saw;

    public static void loadBlocks() {
        for (Block.BlockType value : Block.BlockType.values()) {
            blockTypeMap.put(value, new Array<>());
        }
        Block.defaultRenderLogic = Renderer.RenderLogic.of("test_block", Block.defaultSize);


        Gdx.app.log("Blocks", "Loading testBlock");
        testBlock = new Block() {
            @Override
            void config() {
                blockType = BlockType.basic;
                connectFilter = (newBlock, rx, ry) -> false;
                shape = ShapeBox;
            }
        };
        Gdx.app.log("Blocks", "Loading testBlock_OnlyConnectX");
        testBlock_OnlyConnectX = new Block() {
            @Override
            void config() {
                blockType = BlockType.basic;
                connectFilter = (newBlock, rx, ry) -> {
                    return (ry > ((newBlock.size + size) - (Vars.GRID_SIZE))) || (ry < ((-newBlock.size - size) + (Vars.GRID_SIZE)));
                };
            }
        };
        Gdx.app.log("Blocks", "Loading testHingeBlock");
        testHingeBlock = new HingeBlock() {
            @Override
            void config() {
                size = 0.5f;
                A = new Block() {
                    @Override
                    void config() {
                        connectFilter = (newBlock, rx, ry) -> ry > 0;
                        shape = ShapePolygon("hinge_l");
                        renderLogic = Renderer.RenderLogic.of("hinge_l", Renderer.Layer.BLOCK_LOWER, size);
                    }
                };
                B = new Block() {
                    @Override
                    void config() {
                        shape = ShapePolygon("hinge_u");
                        connectFilter = (newBlock, rx, ry) -> ry < 0;
                        renderLogic = Renderer.RenderLogic.of("hinge_u", Renderer.Layer.BLOCK_UPPER, size);
                    }
                };
            }
        };

        Gdx.app.log("Blocks", "Loading saw");
        saw = new RevoluteBlock() {
            @Override
            void config() {
                A = new Block() {
                    @Override
                    void config() {
                        connectFilter = (newBlock, rx, ry) -> true;
                        shape = ShapeCircle;
                        renderLogic = Renderer.RenderLogic.of("saw_l", Renderer.Layer.BLOCK_LOWER, size);
                    }
                };
                B = new Block() {
                    @Override
                    void config() {
                        connectFilter = (newBlock, rx, ry) -> ry > 0;
                        shape = ShapePolygon("saw_u");
                        renderLogic = Renderer.RenderLogic.of("saw_u", Renderer.Layer.BLOCK_UPPER, size);
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
