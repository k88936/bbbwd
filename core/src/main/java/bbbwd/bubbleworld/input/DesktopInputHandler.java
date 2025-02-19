package bbbwd.bubbleworld.input;

import bbbwd.bubbleworld.input.UI.BlockCollection;
import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.core.Control;
import bbbwd.bubbleworld.game.systems.physics.PhysicsSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DesktopInputHandler extends InputHandler {
    Control.SeekResult seekResult;
    Block type = Blocks.testBlock;
    Vector2 tmp = new Vector2();
    Vector2 touchInWorld = new Vector2();
    int rot = 0;
    private Stage stage = new Stage(new ScreenViewport());

    public DesktopInputHandler() {
        buildUI();
        Gdx.input.setInputProcessor(new InputMultiplexer(this, stage));
    }

    public void buildUI() {
        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);
        BlockCollection blockCollection = new BlockCollection();
        stage.addActor(blockCollection.fadeIn());

           // 创建一个VisWindow窗口，并添加一些内容----------------
        VisWindow window = new VisWindow("title");
        //按钮
        VisTextButton bt = new VisTextButton("button");
        //输入框
        VisTextField tf = new VisTextField("input:");
        //滑动条
        VisSlider sl = new VisSlider(0,100,1,false);
        //监视值的变化
        sl.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                VisSlider source = (VisSlider) actor;
                float value = source.getValue(); // 获取滑动条的当前值
                System.out.println("Slider value: " + value);
            }
        });
        window.add("content").padTop(5f).row();
        window.add(bt).pad(10f).row();
        window.add(tf).row();
        window.add(sl);
        // 填充窗口内容并居中显示
        window.pack();
        window.centerWindow();
        // 将窗口添加到舞台，并设置淡入效果
        stage.addActor(window.fadeIn());
    }

    @Override
    public void resize(int width, int height) {
        // 当窗口大小改变时，更新舞台的视口
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        // 清理资源
        VisUI.dispose();
        stage.dispose();
    }

    @Override
    public void update() {
        stage.act();
        if (seekResult != null) {
            Batch b = Vars.renderer.getBatch();
            b.begin();
            Affine2 translate = new Affine2(seekResult.transform()).translate(-0.5f, -0.5f);
            Blocks.testBlock.renderLogic.render(translate, b);
            b.end();
        }
//        Batch b = Vars.renderer.getBatch();
//            b.begin();
//            Blocks.testBlock.renderLogic.render(new Affine2().translate(touchInWorld), b);
//            b.end();

//        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    }

    @Override
    public void drawUI() {
        stage.draw();
    }

    @Override
    public boolean keyDown(int keycode) {
        System.out.println("key pressed: " + keycode);
        switch (keycode) {
            case Input.Keys.SPACE -> {
                if (seekResult == null) return false;
                Control.buildAndConnect(seekResult);
                seekResult = null;
            }
            case Input.Keys.E -> {
                Vars.ecs.getSystem(PhysicsSystem.class).explode(touchInWorld, 2, 1, 1);
            }
            case Input.Keys.R -> {
                rot++;
                if (rot > 3) rot = 0;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        tmp.set(Gdx.input.getX(), Gdx.input.getY());
        Vars.renderer.getViewport().unproject(tmp);
        touchInWorld.set(tmp);
        seekResult = Control.seekPlaceForBuild(touchInWorld, type, rot);


        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}