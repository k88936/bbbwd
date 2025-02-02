import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.core.ContentLoader;
import bbbwd.bubbleworld.core.Control;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.math.Affine2;

public class TestUtils {
    public static int setBlock(float angle, int x, int y, Block type) {
        return Vars.control.buildBlock(new Affine2().rotate(angle).translate(x, y), type);
    }

    public static void init() {
        Box2d.initialize();
        Vars.resources = new FakeResources();
        Vars.resources.load();
        Vars.contentLoader = new ContentLoader();
        Vars.contentLoader.load();
        Vars.control = new Control();
        Vars.control.startGame();
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
