package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.Renderer;
import bbbwd.bubbleworld.game.components.BoxCM;
import bbbwd.bubbleworld.game.components.DrawableCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import com.badlogic.gdx.math.MathUtils;

public abstract class Block {


//    abstract void init(
//        DrawableCM drawableCM,
    ////        TransformCM transformCM,
//        BoxCM boxCM
//    );
    public float size;
    public Renderer.RenderLogic renderLogic;
    public ConnectFilter connectFilter;

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
        boolean filter(Block newBlock, float rx, float ry);
    }
}

