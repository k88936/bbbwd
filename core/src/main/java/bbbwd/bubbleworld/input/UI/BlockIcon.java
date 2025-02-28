package bbbwd.bubbleworld.input.UI;

import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.core.render.RenderLogic;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class BlockIcon extends BaseDrawable {

    final RenderLogic renderLogic;
    final Affine2 transform;
    final float scale;


    public BlockIcon(Block block) {
        this.renderLogic = block.renderLogic;
        transform = new Affine2();
        scale = 1 / block.size / 2;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        transform.setToTranslation(x, y);
        transform.scale(width * scale, height * scale);
        renderLogic.render(transform, batch);
    }
}
