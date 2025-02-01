package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.Renderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

public class Blocks {
    public  static Block testBlock;

    public static void loadBlocks() {
        // Load blocks
        testBlock = new Block() {
            {
                final TextureRegion texReg = Vars.resources.atlas.findRegion("gear");
                size = 0.5f;
                renderLogic = new Renderer.RenderLogic() {

                    @Override
                    public void render(Affine2 tfm, SpriteBatch bth) {
                        //push the offset
                        tfm.translate(-size, -size);
                        //due to the way libgdx handles texture drawing, we need to add offset to correct the center of the texture
                        bth.draw(texReg, 2 * size, 2 * size, tfm);
                        //pop the offset
                        tfm.translate(size, size);
                    }
                };
            }
        };
    }
}
