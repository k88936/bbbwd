import bbbwd.bubbleworld.Main;
import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.core.Control;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.game.components.physics.JointCM;
import bbbwd.bubbleworld.input.DesktopInputHandler;
import bbbwd.bubbleworld.lwjgl3.Lwjgl3Launcher;
import bbbwd.bubbleworld.lwjgl3.StartupHelper;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.math.Affine2;

public class DesktopTest {
    public static void main(String[] args) {

        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        Control.init = () -> {
            Vars.control.inputHandler = new DesktopInputHandler();
        };
        Lwjgl3ApplicationConfiguration defaultConfiguration = Lwjgl3Launcher.getDefaultConfiguration();
        defaultConfiguration.setTitle("test");
        new Lwjgl3Application(new Main() {
            @Override
            public void create() {
                super.create();


//                Control.buildBlock(new Affine2().translate(0, 0).rotate(45), Blocks.testBlock);
//
                Control.buildBlock(new Affine2().translate(0, 0).rotate(0), Blocks.testBlock);
//                Control.buildBlock(new Affine2().translate(- 1, - 0.9f).rotate(45), Blocks.testBlock);
//                Control.buildBlock(new Affine2().translate(1, -1).rotate(-30), Blocks.testBlock);
//                Control.buildBlock(new Affine2().translate(- 1, 1).rotate(10), Blocks.testBlock);
                Control.buildBlock(new Affine2().translate(- 3, 3).rotate(0), Blocks.saw);

                int x = Control.buildBlock(new Affine2().translate(2, 3), Blocks.testHingeBlock);
//                JointCM jointCM = Vars.ecs.getMapper(JointCM.class).get(x);
//                Box2d.b2Joint_SetCollideConnected(jointCM.jointId, false);


                DeviceCM deviceCM = Vars.ecs.getMapper(bbbwd.bubbleworld.game.components.logic.DeviceCM.class).get(x);
                deviceCM.memory[1] = -0.2;

            }
        }, defaultConfiguration);
    }
}
