package bbbwd.bubbleworld.core.render;

import bbbwd.bubbleworld.Vars;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

public abstract class RenderLogic {
    public final Renderer.Layer layer;
    public final float size;

    public RenderLogic(Renderer.Layer layer, float size) {
        this.layer = layer;
        this.size = size;
    }

    public abstract void render(Affine2 tfm, Batch bth);

    public abstract void renderNormal(Affine2 tfm, Batch bth);

    public static RenderLogic of(final String name, Renderer.Layer layer, float size) {
        final TextureRegion texture = Vars.resources.getTexureRegionFromPack(name);
        final TextureRegion normal = Vars.resources.getTexureRegionFromPack(name + ".normal");
        return new RenderLogic(layer, size) {
            @Override
            public void render(Affine2 tfm, Batch bth) {
                bth.draw(texture, 2 * size, 2 * size, tfm);
            }

            @Override
            public void renderNormal(Affine2 tfm, Batch bth) {
                bth.draw(normal, 2 * size, 2 * size, tfm);
            }
        };
    }

    public static RenderLogic of(final String name, float size) {
        return of(name, Renderer.Layer.BLOCK_UPPER, size);
    }

}
