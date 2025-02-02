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
        Vars.renderer = new Renderer();
        Vars.control.startGame();

        //for test
        Affine2 tfm = new Affine2();
        Vars.control.buildBlock(tfm, Blocks.testBlock);
        tfm.translate(1f, 1);
        Vars.control.buildBlock(tfm, Blocks.testBlock_OnlyConnectX);
        System.out.println(Vars.control.inputHandler.seekPlaceForBuild(new Vector2(0, 1), Blocks.testBlock_OnlyConnectX).connections());


    }


    @Override
    public void render() {
//        System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond());
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        if (Vars.control.isGameRunning) {
            Vars.ecs.process();
            Vars.renderer.render();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
