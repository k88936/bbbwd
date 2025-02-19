package bbbwd.bubbleworld.input.UI;

import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.content.blocks.Blocks;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

public class BlockCollection extends VisWindow {

    public BlockCollection() {
        super("tabbed pane");
        TableUtils.setSpacingDefaults(this);
        final VisTable container = new VisTable();
        TabbedPane.TabbedPaneStyle style = VisUI.getSkin().get("vertical", TabbedPane.TabbedPaneStyle.class);
        TabbedPane tabbedPane = new TabbedPane(style);
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                container.clearChildren();
                container.add(tab.getContentTable()).expand().fill();
            }
        });
        add(container).expand().fill();
        add(tabbedPane.getTable()).growY();
        for (Block.BlockType value : Block.BlockType.values()) {
            Category category = new Category(value);
            tabbedPane.add(category);
        }
        tabbedPane.switchTab(0);
        setResizable(true);
        setSize(400, 380);
    }
    private static class Category extends Tab {
        private final String title;
        private final Table content = new VisTable();

        public Category(Block.BlockType type) {
            super(false, false);
            this.title = type.name();
            Array<Block> blocks = Blocks.blockTypeMap.get(type);
            for (int i = 0; i < 16; i++) {
                if (i < blocks.size) {
                    Block block = blocks.get(i);
                    Image image = new Image(new BlockIcon(block));
                    content.add(image).size(64).pad(8);
                    image.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            System.out.println("clicked");
                        }
                    });
                } else {
                    content.add().size(64).pad(8);
                }
                if (i % 4 == 3) content.row();
            }
        }
        @Override
        public String getTabTitle() {
            return title;
        }
        @Override
        public Table getContentTable() {
            return content;
        }
    }
}
