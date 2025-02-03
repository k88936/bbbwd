package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.ComposedCM;
import com.badlogic.gdx.math.MathUtils;

public abstract class ComposedBlock extends Block {

    Block A;
    Block B;

    ComposedBlock() {
        config();
        A.size = size;
        B.size = size;
        //only used when this is created
        connectFilter = (newBlock, rx, ry) -> (A.connectFilter.filter(newBlock, rx, ry) || B.connectFilter.filter(newBlock, rx, ry));

    }

    @Override
    public int create() {
        assert (MathUtils.isEqual(size * 4, MathUtils.round(size * 4)));
        int A = this.A.create();
        int B = this.B.create();
        int entity = Vars.ecs.create();
        ComposedCM composedCM = Vars.ecs.getMapper(ComposedCM.class).create(entity);
        composedCM.childA = A;
        composedCM.childB = B;
        return entity;
    }

    public abstract void compose(int entityA, int entityB);
}
