package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.Renderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

public class Blocks {
    public static Block testBlock;
    public static Block testBlock_OnlyConnectX;

    public static void loadBlocks() {
        // Load blocks
        final TextureRegion texture = Vars.resources.getTexureRegion("hinge_l");
        final TextureRegion normal = Vars.resources.getTexureRegion("hinge_l.normal");
        testBlock = new Block() {
            @Override
            void config() {

                size = 0.5f;
                renderLogic = new Renderer.RenderLogic() {
                    @Override
                    public void render(Affine2 tfm, SpriteBatch bth) {
                        //push the offset
                        tfm.translate(-size, -size);
                        //due to the way libgdx handles texture drawing, we need to add offset to correct the center of the texture
                        bth.draw(texture, 2 * size, 2 * size, tfm);
                        //pop the offset
                        tfm.translate(size, size);
                    }

                    @Override
                    public void renderNormal(Affine2 tfm, SpriteBatch bth) {
                        tfm.translate(-size, -size);
                        //due to the way libgdx handles texture drawing, we need to add offset to correct the center of the texture
                        bth.draw(normal, 2 * size, 2 * size, tfm);
                        //pop the offset
                        tfm.translate(size, size);
                    }
                };
                connectFilter = (newBlock, rx, ry) -> false;
            }
        };
        testBlock_OnlyConnectX = new Block() {
            @Override
            void config() {
                size = 0.5f;
                renderLogic = new Renderer.RenderLogic() {
                    @Override
                    public void render(Affine2 tfm, SpriteBatch bth) {
                        //push the offset
                        tfm.translate(-size, -size);
                        //due to the way libgdx handles texture drawing, we need to add offset to correct the center of the texture
                        bth.draw(texture, 2 * size, 2 * size, tfm);
                        //pop the offset
                        tfm.translate(size, size);
                    }

                    @Override
                    public void renderNormal(Affine2 tfm, SpriteBatch bth) {

                        render(tfm, bth);
                    }
                };
                connectFilter = (newBlock, rx, ry) -> {
                    return (ry > ((newBlock.size + size) - (Vars.GRID_SIZE))) || (ry < ((-newBlock.size - size) + (Vars.GRID_SIZE)));
                };
            }
        };
    }
}
