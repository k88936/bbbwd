package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.Renderer;
import bbbwd.bubbleworld.game.components.BoxCM;
import bbbwd.bubbleworld.game.components.DrawableCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;

public abstract class Block {


   public static final float defaultSize = 0.5f;
  public   static final ConnectFilter defaultConnectionFilter = (newBlock, rx, ry) -> true;
  public    static TextureRegion defaulttexture;
    public static TextureRegion defaultNormal;
  public   static final Renderer.RenderLogic defaultRenderLogic = new Renderer.RenderLogic() {

        @Override
        public void render(Affine2 tfm, Batch bth) {

            tfm.translate(-defaultSize, -defaultSize);
            bth.draw(defaulttexture, 2 * defaultSize, 2 * defaultSize, tfm);
            tfm.translate(defaultSize, defaultSize);
        }

        @Override
        public void renderNormal(Affine2 tfm, Batch bth) {

            tfm.translate(-defaultSize, -defaultSize);
            bth.draw(defaultNormal, 2 * defaultSize, 2 * defaultSize, tfm);
            tfm.translate(defaultSize, defaultSize);
        }
    };
    ////        TransformCM transformCM,
//        BoxCM boxCM
//    );
    public float size = 0.5f;
    public ConnectFilter connectFilter = defaultConnectionFilter;
   public Renderer.RenderLogic renderLogic = defaultRenderLogic;


    Block() {
        config();
    }

    abstract void config();

    public int create() {
        int entity = Vars.ecs.create();
        TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).create(entity);
        DrawableCM drawableCM = Vars.ecs.getMapper(DrawableCM.class).create(entity);
        drawableCM.renderLogic = renderLogic;
        BoxCM boxCM = Vars.ecs.getMapper(BoxCM.class).create(entity);
        assert (MathUtils.isEqual(size * 4, MathUtils.round(size * 4)));
        boxCM.size = size;
        boxCM.connectFilter = connectFilter;
//        init(
//            drawableCM,
//            boxCM
//        );
        return entity;
    }

    public interface ConnectFilter {

        /**
         * @param rx newBlock relative to oldBlock
         * @param ry newBlock relative to oldBlock
         * @return ture to pass(CANNOT connect)
         */
        boolean filterOut(Block newBlock, float rx, float ry);
    }
}

