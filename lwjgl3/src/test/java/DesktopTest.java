import bbbwd.bubbleworld.Main;
import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.core.Control;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.input.DesktopInputHandler;
import bbbwd.bubbleworld.lwjgl3.Lwjgl3Launcher;
import bbbwd.bubbleworld.lwjgl3.StartupHelper;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.math.Affine2;

public class DesktopTest {
    public static void main(String[] args) {

        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        Control.init = () -> {
            Vars.control.inputHandler = new DesktopInputHandler();
        };
        new Lwjgl3Application(new Main() {
            @Override
            public void create() {
                super.create();


//                Control.buildBlock(new Affine2().translate(0, 0).rotate(45), Blocks.testBlock);

                Control.buildBlock(new Affine2().translate(- 1, - 0.9f).rotate(45), Blocks.testBlock);
                Control.buildBlock(new Affine2().translate(1, -1).rotate(-30), Blocks.testBlock);
                Control.buildBlock(new Affine2().translate(- 1, 1).rotate(10), Blocks.testBlock);
                int x = Control.buildBlock(new Affine2().translate(1, 2), Blocks.testHingeBlock);
                DeviceCM deviceCM = Vars.ecs.getMapper(bbbwd.bubbleworld.game.components.logic.DeviceCM.class).get(x);
                deviceCM.memory[1] = -0.2;

            }
        }, Lwjgl3Launcher.getDefaultConfiguration());
    }
}
