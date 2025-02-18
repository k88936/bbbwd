package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.Renderer;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.game.components.physics.JointCM;
import bbbwd.bubbleworld.game.systems.device.JointDeviceUpdateSystem;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

public class Blocks {
    public static Block testBlock;
    public static Block testBlock_OnlyConnectX;
    public static Block testHingeBlock;
    public static Block saw;

    public static void loadBlocks() {
        // Load blocks
        Block.defaultTexture = Vars.resources.getTexureRegion("test_block");
        Block.defaultNormal = Vars.resources.getTexureRegion("test_block.normal");


        testBlock = new Block() {
            @Override
            void config() {
                connectFilter = (newBlock, rx, ry) -> false;
                shape = ShapePolygon("test_block");
            }
        };
        testBlock_OnlyConnectX = new Block() {
            @Override
            void config() {
                connectFilter = (newBlock, rx, ry) -> {
                    return (ry > ((newBlock.size + size) - (Vars.GRID_SIZE))) || (ry < ((-newBlock.size - size) + (Vars.GRID_SIZE)));
                };
            }
        };

        testHingeBlock = new HingeBlock() {
            @Override
            void config() {
                size = 0.5f;
                A = new Block() {
                    @Override
                    void config() {
                        connectFilter = (newBlock, rx, ry) -> ry > 0;
                        final TextureRegion textureA = Vars.resources.getTexureRegion("hinge_l");
                        shape = ShapePolygon("hinge_l");
                        renderLogic = new Renderer.RenderLogic() {
                            @Override
                            public void render(Affine2 tfm, Batch bth) {
                                tmpAffine2.set(tfm).translate(-size, -size);
                                bth.draw(textureA, 2 * size, 2 * size, tmpAffine2);
                            }

                            @Override
                            public void renderNormal(Affine2 tfm, Batch bth) {

                            }
                        };


                    }
                };
                B = new Block() {
                    @Override
                    void config() {
                        final TextureRegion textureB = Vars.resources.getTexureRegion("hinge_u");
                        shape = ShapePolygon("hinge_u");
                        connectFilter = (newBlock, rx, ry) -> ry <0;
                        renderLogic = new Renderer.RenderLogic() {
                            @Override
                            public void render(Affine2 tfm, Batch bth) {
                                tmpAffine2.set(tfm).translate(-size, -size);
                                bth.draw(textureB, 2 * size, 2 * size, tmpAffine2);
                            }

                            @Override
                            public void renderNormal(Affine2 tfm, Batch bth) {
                                tmpAffine2.set(tfm).translate(-size, -size);
                                bth.draw(textureB, 2 * size, 2 * size, tmpAffine2);
                            }
                        };
                    }
                };
            }
        };

        saw = new RevoluteBlock() {
            @Override
            void config() {
                A=new Block() {
                    @Override
                    void config() {
                        connectFilter=(newBlock, rx, ry)->true;
                        shape= ShapeCircle;
                        TextureRegion texture = Vars.resources.getTexureRegion("saw_l");
                        renderLogic = new Renderer.RenderLogic() {
                            @Override
                            public void render(Affine2 tfm, Batch bth) {
                                tmpAffine2.set(tfm).translate(-size, -size);
                                bth.draw(texture, 2 * size, 2 * size, tmpAffine2);
                            }

                            @Override
                            public void renderNormal(Affine2 tfm, Batch bth) {

                            }
                        };
                    }
                };
                B =new Block() {
                    @Override
                    void config() {
                        connectFilter=(newBlock, rx, ry)->ry>0;
                        shape= ShapePolygon("saw_u");
                        TextureRegion texture = Vars.resources.getTexureRegion("saw_u");
                        renderLogic = new Renderer.RenderLogic() {
                            @Override
                            public void render(Affine2 tfm, Batch bth) {
                                tmpAffine2.set(tfm).translate(-size, -size);
                                bth.draw(texture, 2 * size, 2 * size, tmpAffine2);
                            }

                            @Override
                            public void renderNormal(Affine2 tfm, Batch bth) {

                            }
                        };
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
