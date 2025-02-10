package bbbwd.bubbleworld.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;

public abstract class InputHandler implements InputProcessor, Disposable {

    public abstract void update();

    public abstract void drawUI();

    public abstract void resize(int width, int height);

}
