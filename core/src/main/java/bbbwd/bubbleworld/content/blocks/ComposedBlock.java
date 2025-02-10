package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.ComposedCM;
import com.badlogic.gdx.math.MathUtils;

public abstract class ComposedBlock extends Block {

    static final Block defaultBlock = new Block() {
        @Override
        void config() {

        }
    };
    public Block A = defaultBlock;
    public Block B = defaultBlock;

    ComposedBlock() {
        config();
        A.size = size;
        B.size = size;
        connectFilter = (newBlock, rx, ry) -> (A.connectFilter.filterOut(newBlock, rx, ry) || B.connectFilter.filterOut(newBlock, rx, ry));

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

    public abstract void compose(int entityA, int entityB, int baseEntity);
}
