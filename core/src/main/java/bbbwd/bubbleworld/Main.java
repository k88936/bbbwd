package bbbwd.bubbleworld;

import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.core.*;
import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.game.systems.logic.GlobalVars;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;


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
        Control.init.run();
        Vars.logicVars = new GlobalVars();
        Vars.state = new GameState();
        Vars.control.startGame();
        Vars.renderer = new Renderer();




        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Runtime runtime = Runtime.getRuntime();
        Gdx.app.log("System ","OS: " + osBean.getName() + " " + osBean.getVersion() + " " + osBean.getArch() + " CPU: " + runtime.availableProcessors()+ "MaxMemory: " + runtime.maxMemory() / 1024 / 1024 + "MB" +
            " TotalMemory: " + runtime.totalMemory() / 1024 / 1024 + "MB" +
            " FreeMemory: " + runtime.freeMemory() / 1024 / 1024 + "MB");



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
