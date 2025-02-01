package bbbwd.bubbleworld.core;
import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.content.items.Items;

public class ContentLoader {
    public  void   load() {
        Blocks.loadBlocks();
        Items.loadItems();
        // Load content
    }
}
