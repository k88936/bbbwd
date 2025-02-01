package bbbwd.bubbleworld.game.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.box2d.structs.b2BodyId;

public class BoxCM extends PooledComponent {
    public b2BodyId bodyId;
    public float size;//radius of a box


    @Override
    protected void reset() {
    }
}
