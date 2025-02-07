package bbbwd.bubbleworld.core;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import bbbwd.bubbleworld.input.DesktopInputHandler;
import bbbwd.bubbleworld.input.InputHandler;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;

public class Control {
    public final InputHandler inputHandler = new DesktopInputHandler();
    public boolean isGameRunning = false;

    public void startGame() {
        WorldConfiguration config = new WorldConfigurationBuilder()
            .with(new PhysicsSystem())
            .build();
        Vars.ecs = new World(config);
        isGameRunning = true;
    }


}
