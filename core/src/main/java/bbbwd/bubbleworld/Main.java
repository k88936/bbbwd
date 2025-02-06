package bbbwd.bubbleworld;

import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.core.ContentLoader;
import bbbwd.bubbleworld.core.Control;
import bbbwd.bubbleworld.core.Renderer;
import bbbwd.bubbleworld.core.Resources;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends ApplicationAdapter {

    @Override
    public void create() {
        Box2d.initialize();
        Vars.resources = new Resources();
        Vars.resources.load();
        Vars.contentLoader = new ContentLoader();
        Vars.contentLoader.load();
        Vars.control = new Control();
        Vars.control.startGame();
        Vars.renderer = new Renderer();

        Vars.control.buildBlock(new Affine2().rotate(0).translate(0, 0), Blocks.testBlock);
        Vars.control.buildBlock(new Affine2().rotate(0).translate(0, 2), Blocks.testBlock);
        Vars.control.buildBlock(new Affine2().rotate(0).translate(0, 4), Blocks.testBlock);
        Vars.control.buildBlock(new Affine2().rotate(0).translate(0, 6), Blocks.testBlock);

    }


    @Override
    public void render() {
//        System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond());
        ScreenUtils.clear(1f, 1f, 1f, 1f);
        if (Vars.control.isGameRunning) {
            Vars.ecs.process();
            Vars.renderer.render();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
       Vars.renderer.resize(width, height);
    }
}
