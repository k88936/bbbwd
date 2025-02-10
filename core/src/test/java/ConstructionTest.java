import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.core.Control;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;


public class ConstructionTest {
    @Test
    public void testConnectable() {
        TestUtils.init();
        TestUtils.setBlock(0, 0, 0, Blocks.testBlock);
        TestUtils.setBlock(0, 2, 0, Blocks.testBlock);
        TestUtils.setBlock(0, 1, 1, Blocks.testBlock_OnlyConnectX);
        Control.SeekResult seekResult = Control.seekPlaceForBuild(new Vector2(1, 0), Blocks.testBlock_OnlyConnectX, 1);
        assert seekResult == null;
        Control.SeekResult seekResult1 = Control.seekPlaceForBuild(new Vector2(1, 0), Blocks.testBlock_OnlyConnectX, 0);
        assert seekResult1.connections().size() == 2;
        Control.SeekResult seekResult2 = Control.seekPlaceForBuild(new Vector2(1, 0), Blocks.testBlock, 0);
        assert seekResult2.connections().size() == 2;
    }

    @Test
    public void testOffset() {
        TestUtils.init();
        TestUtils.setBlock(0, 0, 0, Blocks.testBlock);
        Control.SeekResult seekResult = Control.seekPlaceForBuild(new Vector2(1, 2 * Vars.GRID_SIZE), Blocks.testBlock, 0);
        assert seekResult.transform().m12 == 2 * Vars.GRID_SIZE;
    }

    @Test
    public void testConnectAnchorCompute1() {
        TestUtils.init();
        TestUtils.setBlock(0, 0, 0, Blocks.testBlock);
        Control.SeekResult seekResult = Control.seekPlaceForBuild(new Vector2(1, 2 * Vars.GRID_SIZE), Blocks.testBlock, 0);
        assert seekResult.connections().getFirst().anchorNewBlock.epsilonEquals(-0.5f, -0.125f);
        assert seekResult.connections().getFirst().anchorOldBlock.epsilonEquals(0.5f, 0.125f);
//        assert MathUtils.isEqual(seekResult.connections().get(0).relativeAngle,5.81f,0.1f);
    }

    @Test
    public void testConnectAnchorCompute2() {
        TestUtils.init();
        TestUtils.setBlock(0, 0, 0, Blocks.testBlock);
        TestUtils.setBlock(0, 1, 0.75f, Blocks.testBlock);
        Control.SeekResult seekResult = Control.seekPlaceForBuild(new Vector2(1, 0), Blocks.testBlock, 0);
        assert seekResult.transform().m02 == 1;
        assert seekResult.transform().m12 == -0.25;

    }

    @Test
    public void testConnectAnchorCompute3() {
        TestUtils.init();
        TestUtils.setBlock(0, 0, 0, Blocks.testBlock);
        TestUtils.setBlock(0, 1, 0.75f, Blocks.testBlock);
        TestUtils.setBlock(0, 1, -0.9f, Blocks.testBlock);
        Control.SeekResult seekResult = Control.seekPlaceForBuild(new Vector2(0.8f, 0.15f), Blocks.testBlock, 0);
        Logger.getGlobal().info(seekResult.toString());
        assert seekResult.transform().m02 == 0;
        assert seekResult.transform().m12 == 1;

    }

    @Test
    public void testConnectAnchorCompute4() {
        TestUtils.init();
        TestUtils.setBlock(0, 3, 5, Blocks.testBlock);
        TestUtils.setBlock(0, 3, 3.1f, Blocks.testBlock);
        Control.SeekResult seekResult = Control.seekPlaceForBuild(new Vector2(2.9f, 4.1f), Blocks.testBlock, 0);
        assert seekResult != null;
        Control.buildAndConnect(seekResult);

//        seekResult = InputHandler.seekPlaceForBuild(new Vector2(2.9f, 4.1f), Blocks.testBlock, 0);
//        assert seekResult == null;

    }
}
