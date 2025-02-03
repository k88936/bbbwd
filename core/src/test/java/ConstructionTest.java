import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.input.InputHandler;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;


public class ConstructionTest {
    @Test
    public void testConnectable() {
        TestUtils.init();
        TestUtils.setBlock(0, 0, 0, Blocks.testBlock);
        TestUtils.setBlock(0, 2, 0, Blocks.testBlock);
        TestUtils.setBlock(0, 1, 1, Blocks.testBlock_OnlyConnectX);
        InputHandler.seekResult seekResult = Vars.control.inputHandler.seekPlaceForBuild(new Vector2(1, 0), Blocks.testBlock_OnlyConnectX, 1);
        assert seekResult == null;
        InputHandler.seekResult seekResult1 = Vars.control.inputHandler.seekPlaceForBuild(new Vector2(1, 0), Blocks.testBlock_OnlyConnectX, 0);
        assert seekResult1.connections().size() == 2;
        InputHandler.seekResult seekResult2 = Vars.control.inputHandler.seekPlaceForBuild(new Vector2(1, 0), Blocks.testBlock, 0);
        assert seekResult2.connections().size() == 2;
    }
    @Test
    public void testOffset(){
        TestUtils.init();
        TestUtils.setBlock(0, 0, 0, Blocks.testBlock);
        InputHandler.seekResult seekResult = Vars.control.inputHandler.seekPlaceForBuild(new Vector2(1, 2*Vars.GRID_SIZE), Blocks.testBlock, 0);
        assert seekResult.transform().m12 == 2*Vars.GRID_SIZE;
    }

    @Test
    public void testConnectAnchorCompute() {
        TestUtils.init();
        TestUtils.setBlock(0, 0, 0, Blocks.testBlock);
        InputHandler.seekResult seekResult = Vars.control.inputHandler.seekPlaceForBuild(new Vector2(1, 2*Vars.GRID_SIZE), Blocks.testBlock, 0);
        assert seekResult.connections().getFirst().anchorNewBlock().epsilonEquals(-0.5f,-0.25f);
        assert seekResult.connections().getFirst().anchorOldBlock().epsilonEquals(0.5f,0.25f);
        assert MathUtils.isEqual(seekResult.connections().getFirst().relativeAngle()[0],5.81f,0.1f);
    }
}
