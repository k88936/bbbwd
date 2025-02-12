package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.Renderer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

public class Blocks {
    public static Block testBlock;
    public static Block testBlock_OnlyConnectX;
    public static Block testHingeBlock;

    public static void loadBlocks() {
        // Load blocks
        Block.defaultTexture = Vars.resources.getTexureRegion("test_block");
        Block.defaultNormal = Vars.resources.getTexureRegion("test_block.normal");

        testBlock = new Block() {
            @Override
            void config() {
                connectFilter = (newBlock, rx, ry) -> false;
                hull = Vars.resources.getHull("test_block");
                shape = ShapePolygon;
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
                        renderLogic = new Renderer.RenderLogic() {
                            @Override
                            public void render(Affine2 tfm, Batch bth) {
                                tfm.translate(-size, -size);
                                bth.draw(textureA, 2 * size, 2 * size, tfm);
                                tfm.translate(size, size);
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
                        connectFilter = (newBlock, rx, ry) -> ry < 0;
                        renderLogic = new Renderer.RenderLogic() {
                            @Override
                            public void render(Affine2 tfm, Batch bth) {
                                tfm.translate(-size, -size);
                                bth.draw(textureB, 2 * size, 2 * size, tfm);
                                tfm.translate(size, size);
                            }

                            @Override
                            public void renderNormal(Affine2 tfm, Batch bth) {

                            }
                        };
                    }
                };
            }
        };
    }
}
