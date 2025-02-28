package bbbwd.bubbleworld.core.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Affine2;

public class SimpleRenderLogic extends RenderLogic {

    final RenderLogic Lower;
    final RenderLogic Upper;

    public SimpleRenderLogic(RenderLogic a, RenderLogic b) {
        super(null, 0);
        if (a.layer.compareTo(b.layer) > 0) {
            Lower = b;
            Upper = a;
        } else {
            Lower = a;
            Upper = b;
        }
    }

    @Override
    public void render(Affine2 tfm, Batch bth) {
        Lower.render(tfm, bth);
        Upper.render(tfm, bth);
    }

    @Override
    public void renderNormal(Affine2 tfm, Batch bth) {
    }
}
