package bbbwd.bubbleworld.game.components;

import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.content.liquid.Liquid;
import com.artemis.PooledComponent;

public class TypeCM extends PooledComponent  {
   public Block blockType;//don't overuse this, it is against the ECS pattern
    public  Liquid liquidType;
    @Override
    protected void reset() {
        blockType = null;
        liquidType = null;

    }
}
