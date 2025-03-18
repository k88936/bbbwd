import bbbwd.bubbleworld.Main;
import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.content.liquid.Liquids;
import bbbwd.bubbleworld.core.Control;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.game.components.physics.DynamicBodyCM;
import bbbwd.bubbleworld.game.systems.physics.PhysicsSystem;
import bbbwd.bubbleworld.input.DesktopInputHandler;
import bbbwd.bubbleworld.input.InputHandler;
import bbbwd.bubbleworld.lwjgl3.Lwjgl3Launcher;
import bbbwd.bubbleworld.lwjgl3.StartupHelper;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.box2d.structs.b2Vec2;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;

public class FloodTest {
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
                InputHandler inputHandler = Vars.control.inputHandler;
//                Control.buildBlock(new Affine2().translate(0, 0).rotate(45), Blocks.testBlock);
////
//                int y = inputHandler.buildBlock(new Affine2().translate(0, 0).rotate(0), Blocks.saw);
////                Control.buildBlock(new Affine2().translate(- 1, - 0.9f).rotate(45), Blocks.testBlock);
////                Control.buildBlock(new Affine2().translate(1, -1).rotate(-30), Blocks.testBlock);
//                inputHandler.buildBlock(new Affine2().translate(-1, 1).rotate(10), Blocks.testBlock);
//                Control.buildBlock(new Affine2().translate(- 3, 3).rotate(0), Blocks.saw);
//                b2WorldId worldId = Vars.ecs.getSystem(PhysicsSystem.class).getWorldId();
                b2Vec2 b2Vec2 = Box2dPlus.GDXTob2(new Vector2(-10, -1), new b2Vec2());
                for (float i = 2; i < 5; i += 0.125f) {
                    for (float j = 0; j < 3; j += 0.125f) {
                        int e = Liquids.testLiquid.create(new Affine2().translate(i, j));
                        DynamicBodyCM dynamicBodyCM = Vars.ecs.getMapper(DynamicBodyCM.class).get(e);
                        Box2d.b2Body_ApplyForceToCenter(dynamicBodyCM.bodyId, b2Vec2, true);
                    }
                }
                for (float i = -10; i < 10; i += 1) {
                    for (float j = -2; j <= -1; j += 1) {
                        inputHandler.buildBlock(new Affine2().translate(i, j), Blocks.testBlock);
                    }
                }
                for (float i = -5; i <= -3; i++) {
                    for (float j = 0; j < 5; j += 1) {
                        inputHandler.buildBlock(new Affine2().translate(i, j), Blocks.testBlock);
                    }

                }

//                int x = Control.buildBlock(new Affine2().translate(2, 3), Blocks.testHingeBlock);
//                JointCM jointCM = Vars.ecs.getMapper(JointCM.class).get(x);
//                Box2d.b2Joint_SetCollideConnected(jointCM.jointId, false);
//                DeviceCM deviceCM = Vars.ecs.getMapper(DeviceCM.class).get(y);
//                deviceCM.memory[1] = -0.2;

            }
        }, defaultConfiguration);
    }
}
