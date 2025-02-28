package bbbwd.bubbleworld.core;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.systems.device.JointDeviceUpdateSystem;
import bbbwd.bubbleworld.game.systems.physics.PhysicsSystem;
import bbbwd.bubbleworld.game.systems.logic.LogicSystem;
import bbbwd.bubbleworld.input.InputHandler;
import bbbwd.bubbleworld.utils.Job;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;

public class Control {
    public static Job init;
    public InputHandler inputHandler;
    public boolean isGameRunning = false;

    public void startGame() {
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new PhysicsSystem())
                .with(new JointDeviceUpdateSystem())
                .with(new LogicSystem())
                .build();
        Vars.ecs = new World(config);
        isGameRunning = true;
    }


}
