import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.core.ContentLoader;
import bbbwd.bubbleworld.core.Control;
import bbbwd.bubbleworld.core.GameState;
import bbbwd.bubbleworld.game.systems.physics.PhysicsSystem;
import bbbwd.bubbleworld.game.systems.logic.GlobalVars;
import bbbwd.bubbleworld.game.systems.logic.LogicSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.math.Affine2;

public class Tools {
    public static PhysicsSystem physicsSystem;
    public static LogicSystem logicSystem;

    public static int setBlock(float angle, float x, float y, Block type) {
        return Control.buildBlock(new Affine2().translate(x, y).rotate(angle), type);
    }

    public static int setBlock(Affine2 tfm, Block type) {
        return Control.buildBlock(tfm, type);
    }

    public static int setBlock(float c, float s, int x, int y, Block type) {
        Affine2 translate = new Affine2().translate(x, y);
        translate.m00 = c;
        translate.m01 = -s;
        translate.m10 = s;
        translate.m11 = c;
        return Control.buildBlock(translate, type);
    }

    public static void init() {
        Gdx.app=new FakeApplication();

        Box2d.initialize();
        Vars.resources = new FakeResources();
        Vars.resources.load();
        Vars.contentLoader = new ContentLoader();
        Vars.contentLoader.load();
        Vars.control = new Control();
        Vars.control.startGame();
        physicsSystem= Vars.ecs.getSystem(PhysicsSystem.class);
        logicSystem= Vars.ecs.getSystem(LogicSystem.class);

        Vars.logicVars = new GlobalVars();
        Vars.state = new GameState();

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
