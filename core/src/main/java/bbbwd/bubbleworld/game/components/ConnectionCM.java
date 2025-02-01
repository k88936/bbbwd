package bbbwd.bubbleworld.game.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.box2d.structs.*;

public class ConnectionCM extends PooledComponent {

    public b2JointId jointId;
    public int entityA;
    public int entityB;

    @Override
    protected void reset() {

    }
}
