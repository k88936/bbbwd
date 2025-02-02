package bbbwd.bubbleworld.game.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.IntArray;

public class ComposedCM extends PooledComponent {
    public IntArray children = new IntArray(2);

    @Override
    protected void reset() {
        children.clear();
    }
}
