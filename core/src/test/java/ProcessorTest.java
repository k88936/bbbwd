import bbbwd.bubbleworld.game.systems.logic.LAssembler;
import bbbwd.bubbleworld.game.systems.logic.LExecutor;
import org.junit.jupiter.api.Test;

public class ProcessorTest {
    @Test
    public void test() {
        TestUtils.init();
        LAssembler assemble = LAssembler.assemble("set x 10\n" +
            "set y 20\n" +
            "op add xy x y\n");
        LExecutor executor = new LExecutor();
        executor.load(assemble);
        executor.runOnce();
        executor.runOnce();
        executor.runOnce();
//        Logger.getGlobal().info("Result: " + Arrays.toString(executor.vars));
        assert executor.optionalVar(0).numVal == 3;
        assert executor.optionalVar(1).numVal == 10;
        assert executor.optionalVar(2).numVal == 20;
        assert executor.optionalVar(3).numVal == 30;
    }
}
