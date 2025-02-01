package bbbwd.bubbleworld.core;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import bbbwd.bubbleworld.input.InputHandler;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.math.Affine2;

public class Control {
    public final InputHandler inputHandler = new InputHandler();

    public void startGame() {
        WorldConfiguration config = new WorldConfigurationBuilder()
            .with(new PhysicsSystem())
            .build();
        Vars.ecs = new World(config);
        isGameRunning = true;
    }
    public boolean isGameRunning = false;

    public int  buildBlock(Affine2 transform, Block newBlock) {
        assert isGameRunning;
        int entity = newBlock.create();
        Vars.ecs.getMapper(TransformCM.class).get(entity).transform.set(transform);
        Vars.ecs.getSystem(PhysicsSystem.class).createBody(entity);
        return entity;
    }
}
