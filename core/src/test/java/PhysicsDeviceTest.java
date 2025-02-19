import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

public class PhysicsDeviceTest {
    @Test
    public void testPhysicsDevice() {
        Tools.init();
        int x = Tools.setBlock(0, 0, 0, Blocks.testHingeBlock);
        DeviceCM deviceCM = Vars.ecs.getMapper(DeviceCM.class).get(x);
        deviceCM.memory[1] = 1;
        Tools.step(60);
        double v = deviceCM.memory[0];
        Logger.getGlobal().info("angle: " + v);
        assert v!=0;

    }
}
