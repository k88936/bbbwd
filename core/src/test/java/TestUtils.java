import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.core.ContentLoader;
import bbbwd.bubbleworld.core.Control;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import bbbwd.bubbleworld.input.InputHandler;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.math.Affine2;

public class TestUtils {
    public static b2WorldId worldId;

    public static int setBlock(float angle, float x, float y, Block type) {
        return InputHandler.buildBlock(new Affine2().translate(x, y).rotate(angle), type);
    }

    public static int setBlock(Affine2 tfm, Block type) {
        return InputHandler.buildBlock(tfm, type);
    }

    public static int setBlock(float c, float s, int x, int y, Block type) {
        Affine2 translate = new Affine2().translate(x, y);
        translate.m00 = c;
        translate.m01 = -s;
        translate.m10 = s;
        translate.m11 = c;
        return InputHandler.buildBlock(translate, type);
    }

    public static void init() {
        Box2d.initialize();
        Vars.resources = new FakeResources();
        Vars.resources.load();
        Vars.contentLoader = new ContentLoader();
        Vars.contentLoader.load();
        Vars.control = new Control();
        Vars.control.startGame();
        worldId = Vars.ecs.getSystem(PhysicsSystem.class).getWorldId();

    }

    public static void step() {
        if (Vars.control.isGameRunning) {
            Vars.ecs.process();
        }
    }

    public static void step(int times) {
        for (int i = 0; i < times; i++) {
            step();
        }
    }
}
