package bbbwd.bubbleworld.game.components;

import bbbwd.bubbleworld.content.blocks.Block;
import com.artemis.PooledComponent;
import com.badlogic.gdx.box2d.structs.b2BodyId;

public class BoxCM extends PooledComponent {
    public b2BodyId bodyId;
    public boolean isStatic=false;
    public float size;//radius of a box
    public Block.ConnectFilter connectFilter;//don't overuse this, it is against the ECS pattern


    @Override
    protected void reset() {
    }
}
