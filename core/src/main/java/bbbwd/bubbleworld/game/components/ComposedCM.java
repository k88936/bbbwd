package bbbwd.bubbleworld.game.components;

import com.artemis.PooledComponent;

public class ComposedCM extends PooledComponent {

    public int childA;
    public int childB;

    @Override
    protected void reset() {
        childA = childB = -1;
    }
}
