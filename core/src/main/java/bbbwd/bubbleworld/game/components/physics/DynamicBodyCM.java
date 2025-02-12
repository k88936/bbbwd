package bbbwd.bubbleworld.game.components.physics;

import bbbwd.bubbleworld.content.blocks.Block;
import com.artemis.PooledComponent;
import com.badlogic.gdx.box2d.structs.b2BodyId;

public class DynamicBodyCM extends PooledComponent {
    public b2BodyId bodyId;
    public Block type;//don't overuse this, it is against the ECS pattern


    @Override
    protected void reset() {
    }
}
