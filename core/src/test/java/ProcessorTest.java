import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.game.components.logic.ExecutorCm;
import bbbwd.bubbleworld.game.systems.logic.LAssembler;
import org.junit.jupiter.api.Test;

public class ProcessorTest {
    @Test
    public void test() {
        Tools.init();
        int entity = Vars.ecs.create();

        ExecutorCm logic = Vars.ecs.getMapper(ExecutorCm.class).create(entity);

        logic.load(LAssembler.assemble("read x 1 2"));
        Tools.step();

        assert logic.instructions.length == 1;
        assert logic.counter.getNumVal() == 1;


        DeviceCM device = Vars.ecs.getMapper(DeviceCM.class).create(entity);
        device.memory[2] = 1;

        logic.devices[1] = entity;

        Tools.step();
        assert logic.vars.get(1).getNumVal() == 1;

    }

//    @Test

}
