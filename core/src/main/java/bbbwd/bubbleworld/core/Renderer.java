package bbbwd.bubbleworld.core;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.DrawableCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Renderer {
    private final SpriteBatch batch = new SpriteBatch();
    public final Camera camera = new OrthographicCamera(10, 10);
    public final Viewport viewport = new ExtendViewport(20, 20, camera);
    private final BitmapFont font = new BitmapFont();
//    ComponentMapper<VisibleCM> visibleCMComponentMapper;

    IntArray visibleEntities = new IntArray();

    public void render() {

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        System.out.println(viewport.getWorldHeight());
//        System.out.println(viewport.getWorldWidth());

        float cameraX = camera.position.x;
        float cameraY = camera.position.y;
        float halfWidth = viewport.getWorldWidth() / 2.0f;
        float halfHeight = viewport.getWorldHeight() / 2.0f;
        visibleEntities.clear();
        Vars.ecs.getSystem(PhysicsSystem.class).collect(cameraX - halfWidth, cameraY - halfHeight, cameraX + halfWidth, cameraY + halfHeight, visibleEntities);

        viewport.apply();
        // render
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // render all entities
        int bound = visibleEntities.size;
        for (int i = 0; i < bound; i++) {
            process(visibleEntities.get(i));
        }
        batch.end();


    }

    private void process(int entity) {
        TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entity);
        DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entity);
        drawable.renderLogic.render(transformCM.transform, batch);
//        visibleCMComponentMapper.set(entity, false);
    }


    public interface RenderLogic {
        void render(Affine2 tfm, SpriteBatch bth);
    }
}
