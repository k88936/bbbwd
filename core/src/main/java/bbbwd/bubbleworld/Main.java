package bbbwd.bubbleworld;

import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.core.ContentLoader;
import bbbwd.bubbleworld.core.Control;
import bbbwd.bubbleworld.core.Renderer;
import bbbwd.bubbleworld.core.Resources;
import bbbwd.bubbleworld.input.InputHandler;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends ApplicationAdapter {

    enum Platform {
        Desktop,
        Android,
        IOS,
        HTML,
        Headless
    }
    @Override
    public void create() {
        Box2d.initialize();
        Vars.resources = new Resources();
        Vars.resources.load();
        VisUI.load(VisUI.SkinScale.X1);
        Vars.contentLoader = new ContentLoader();
        Vars.contentLoader.load();
        Vars.control = new Control();
        Control.init.apply(null);
        Vars.control.startGame();
        Vars.renderer = new Renderer();

        float xb = 4;
        float yb = 4;
        InputHandler.buildBlock(new Affine2().rotate(0).translate(xb - 1, yb - 0.9f), Blocks.testBlock);
        InputHandler.buildBlock(new Affine2().rotate(0).translate(xb - 1, yb + 1), Blocks.testBlock);
        InputHandler.buildBlock(new Affine2().rotate(0).translate(xb + 1, yb - 1), Blocks.testBlock);
        InputHandler.buildBlock(new Affine2().rotate(0).translate(xb + 1, yb + 1), Blocks.testBlock);

    }


    @Override
    public void render() {
//        System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond());
        ScreenUtils.clear(1f, 1f, 1f, 1f);
        if (Vars.control.isGameRunning) {
            Vars.control.inputHandler.update();
            Vars.ecs.process();
            Vars.renderer.render();
        }
        Vars.control.inputHandler.drawUI();
    }

    @Override
    public void dispose() {
        Vars.renderer.dispose();
        Vars.control.inputHandler.dispose();
        VisUI.dispose();
        super.dispose();

    }

    @Override
    public void resize(int width, int height) {
        Vars.control.inputHandler.resize(width, height);
        Vars.renderer.resize(width, height);
    }
}
