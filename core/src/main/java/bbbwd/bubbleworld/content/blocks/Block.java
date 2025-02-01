package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.BoxCM;
import bbbwd.bubbleworld.game.components.DrawableCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.core.Renderer;
import com.badlogic.gdx.math.MathUtils;

public abstract class Block {


//    abstract void init(
//        DrawableCM drawableCM,
    ////        TransformCM transformCM,
//        BoxCM boxCM
//    );
    public float size;
    public Renderer.RenderLogic renderLogic;


    public final int create() {
        int entity = Vars.ecs.create();
        TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).create(entity);
        DrawableCM drawableCM = Vars.ecs.getMapper(DrawableCM.class).create(entity);
        drawableCM.renderLogic = renderLogic;
        BoxCM boxCM = Vars.ecs.getMapper(BoxCM.class).create(entity);
        assert (MathUtils.isEqual(size * 4, MathUtils.round(size * 4)));
        boxCM.size = size;
//        init(
//            drawableCM,
//            boxCM
//        );
        return entity;
    }

}
