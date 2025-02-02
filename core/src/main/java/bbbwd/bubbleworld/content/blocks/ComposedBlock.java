package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.ComposedCM;
import com.badlogic.gdx.math.MathUtils;

import javax.swing.text.html.parser.Entity;

public abstract class ComposedBlock extends Block {

    Block A;
    Block B;

    ComposedBlock(){
        config();
        A.size=size;
        B.size=size;
        connectFilter=(newBlock, rx, ry) -> (A.connectFilter.filter(newBlock, rx, ry) || B.connectFilter.filter(newBlock, rx, ry));
    }
    @Override
    public int create() {
        assert A.size == size;
        assert B.size == size;
        assert (MathUtils.isEqual(size * 4, MathUtils.round(size * 4)));
        int A = this.A.create();
        int B = this.B.create();
        int entity = Vars.ecs.create();
        ComposedCM composedCM = Vars.ecs.getMapper(ComposedCM.class).create(entity);
        composedCM.children.add(A, B);
        return entity;
    }

    abstract void compose(int entityA, int entityB);
}
