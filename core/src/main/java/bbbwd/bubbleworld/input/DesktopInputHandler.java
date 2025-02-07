package bbbwd.bubbleworld.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DesktopInputHandler extends InputHandler implements Disposable {
    private Stage stage;
    private VisTable table_lu;
    private VisTable table_rd;
    private VisTable table_label;

    public DesktopInputHandler() {
        buildUI();
        Gdx.input.setInputProcessor(stage);
    }
    public void buildUI() {
        // 启用VisUI并加载皮肤，跳过Gdx版本检查

        // 创建一个舞台，用于放置和管理所有的演员（actors）
        stage = new Stage(new ScreenViewport());
        // 设置输入处理器为舞台，以便舞台可以处理输入事件

        // 创建一个VisTable作为根布局容器，并设置为填充整个舞台
        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        // 创建一个VisTable作为左上角的组件容器，并设置特定的大小和位置
        table_lu = new VisTable();
        table_lu.setSize(250, 100);
        root.addActor(table_lu);
        // 创建一个imagebutton列表（5个imgButton）作为左上角table_lu中的第一行组件
        // 先创建textbutton ，后面再改为image button
        // 创建第一行的5个按钮
        VisTable table_lu_row1 = new VisTable();
        table_lu_row1.setSize(250, 50);
        table_lu.add(table_lu_row1);
        table_lu.row();
        VisTextButton visTextButtons_lu_row1[] = new VisTextButton[5];
        for (int i = 0; i < 5; i++) {
            visTextButtons_lu_row1[i] = new VisTextButton("" + (i + 1));
            //visTextButtons_lu_row1[i].setSize(50,50);//这行似乎不需要
            table_lu_row1.add(visTextButtons_lu_row1[i]).width(50).height(50);
        }
        // 创建第二行的两个按钮
        VisTable table_lu_row2 = new VisTable();
        table_lu_row2.setSize(250, 50);
        table_lu.add(table_lu_row2);
        VisTextButton visTextButtons_lu_row2[] = new VisTextButton[2];
        visTextButtons_lu_row2[0] = new VisTextButton("1");
        visTextButtons_lu_row2[1] = new VisTextButton("2");
        table_lu_row2.add(visTextButtons_lu_row2[0]).width(220).height(50);
        table_lu_row2.add(visTextButtons_lu_row2[1]).width(30).height(50);


        // 创建一个VisTable作为右下角的组件容器，并设置特定的大小和位置-----------------------
        table_rd = new VisTable();
        table_rd.setSize(100, 150);
        root.addActor(table_rd);

        VisTable table_rd_row1 = new VisTable();
        VisTable table_rd_row2 = new VisTable();
        table_rd.add(table_rd_row1);
        table_rd.row();
        table_rd.add(table_rd_row2);

        VisTextButton button_check = new VisTextButton("materials");
        VisTextButton button_yes = new VisTextButton("Yes");
        VisTextButton button_no = new VisTextButton("No");
        button_yes.setDisabled(true);
        button_no.setDisabled(true);

        button_check.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                button_yes.setDisabled(false);
                button_no.setDisabled(false);
                button_check.setDisabled(true);

                table_label = new VisTable();
                table_label.setSize(100, 100);
                table_label.setPosition(Gdx.graphics.getWidth() - table_rd.getWidth(), 150);
                VisLabel label = new VisLabel("show");
                table_label.add(label);
                stage.addActor(table_label);
            }
        });
        button_yes.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                button_yes.setDisabled(true);
                button_no.setDisabled(true);
                button_check.setDisabled(false);
                table_label.remove();
            }
        });
        button_no.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                button_yes.setDisabled(true);
                button_no.setDisabled(true);
                button_check.setDisabled(false);
                table_label.remove();
            }
        });
        table_rd_row1.add(button_check).width(100).height(100);
        table_rd_row2.add(button_yes).width(50).height(50);
        table_rd_row2.add(button_no).width(50).height(50);

        // 创建一个VisWindow窗口，并添加一些内容----------------
        VisWindow window = new VisWindow("title");
        //按钮
        VisTextButton bt = new VisTextButton("button");
        //输入框
        VisTextField tf = new VisTextField("input:");
        //滑动条
        VisSlider sl = new VisSlider(0, 100, 1, false);
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
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        // 当窗口大小改变时，更新舞台的视口
        stage.getViewport().update(width, height, true);
        table_lu.setPosition(0, height - table_lu.getHeight());
        table_rd.setPosition(width - table_rd.getWidth(), 0);

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
//        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    }

    @Override
    public void drawUI() {
        stage.draw();
    }

    @Override
    public boolean keyDown(int keycode) {
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
