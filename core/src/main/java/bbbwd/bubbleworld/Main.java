package bbbwd.bubbleworld;

import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.core.ContentLoader;
import bbbwd.bubbleworld.core.Control;
import bbbwd.bubbleworld.core.Renderer;
import bbbwd.bubbleworld.core.Resources;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.math.Affine2;
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
        tfm.translate(1f, 1);
        Vars.control.buildBlock(tfm, Blocks.testBlock);
        tfm.translate(1f, 1);
        Vars.control.buildBlock(tfm, Blocks.testBlock);




    }


    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        if (Vars.control.isGameRunning) {
            Vars.ecs.process();
            Vars.renderer.render();
        }
    }

}
