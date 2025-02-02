package bbbwd.bubbleworld.core;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.content.blocks.Block;
import bbbwd.bubbleworld.content.blocks.ComposedBlock;
import bbbwd.bubbleworld.game.components.ComposedCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import bbbwd.bubbleworld.input.InputHandler;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.IntArray;

public class Control {
    public final InputHandler inputHandler = new InputHandler();
    public boolean isGameRunning = false;

    public void startGame() {
        WorldConfiguration config = new WorldConfigurationBuilder()
            .with(new PhysicsSystem())
            .build();
        Vars.ecs = new World(config);
        isGameRunning = true;
    }

    public int buildBlock(Affine2 transform, Block newBlock) {
        assert isGameRunning;
        int entity = newBlock.create();
        Vars.ecs.getMapper(TransformCM.class).get(entity).transform.set(transform);
        if (newBlock instanceof ComposedBlock) {
            IntArray children = Vars.ecs.getMapper(ComposedCM.class).get(entity).children;
            for (int i = 0; i < children.size; i++) {
                Vars.ecs.getSystem(PhysicsSystem.class).createBox(children.get(i));
            }
        } else {
            Vars.ecs.getSystem(PhysicsSystem.class).createBox(entity);
        }
        return entity;
    }

}
