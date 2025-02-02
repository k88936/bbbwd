package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Blocks {
    public static Block testBlock;
    public static Block testBlock_OnlyConnectX;

    public static void loadBlocks() {
        // Load blocks
        testBlock = new Block() {
            {
                final TextureRegion texReg = Vars.resources.getTexureRegion("gear");
                size = 0.5f;
                renderLogic = (tfm, bth) -> {
                    //push the offset
                    tfm.translate(-size, -size);
                    //due to the way libgdx handles texture drawing, we need to add offset to correct the center of the texture
                    bth.draw(texReg, 2 * size, 2 * size, tfm);
                    //pop the offset
                    tfm.translate(size, size);
                };
                connectFilter = (newBlock, rx, ry) -> false;
            }
        };
        testBlock_OnlyConnectX = new Block() {
            {
                final TextureRegion texReg = Vars.resources.getTexureRegion("gear");
                size = 0.5f;
                renderLogic = (tfm, bth) -> {
                    //push the offset
                    tfm.translate(-size, -size);
                    //due to the way libgdx handles texture drawing, we need to add offset to correct the center of the texture
                    bth.draw(texReg, 2 * size, 2 * size, tfm);
                    //pop the offset
                    tfm.translate(size, size);
                };
                connectFilter = (newBlock, rx, ry) -> ry > newBlock.size + size - Vars.GRID_SIZE / 2 || ry < -newBlock.size - size + Vars.GRID_SIZE / 2;
            }
        };
    }
}
