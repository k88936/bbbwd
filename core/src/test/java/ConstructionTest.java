import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Blocks;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;


public class ConstructionTest {
    @Test
    public void testSeekPlaceForConstruction() {
        TestUtils.init();
        TestUtils.setBlock(0, 0, 0, Blocks.testBlock);
        TestUtils.setBlock(0, 2, 0, Blocks.testBlock);
        TestUtils.setBlock(0, 1, 1, Blocks.testBlock);
        assert Vars.control.inputHandler.seekPlaceForBuild(new Vector2(1, 0), Blocks.testBlock_OnlyConnectX).connections().size == 2;
        assert Vars.control.inputHandler.seekPlaceForBuild(new Vector2(1, 0), Blocks.testBlock).connections().size == 3;

    }
}
