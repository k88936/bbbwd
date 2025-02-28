package bbbwd.bubbleworld.core;
import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.content.items.Items;
import bbbwd.bubbleworld.content.liquid.Liquids;

public class ContentLoader {
    public  void   load() {
        Blocks.load();
        Liquids.load();
        Items.load();
        // Load content
    }
}
