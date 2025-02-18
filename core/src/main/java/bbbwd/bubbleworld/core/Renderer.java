package bbbwd.bubbleworld.core;

import batchs.NormalBatch;
import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.DrawableCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.systems.physics.PhysicsSystem;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import box2dLight.RayHandlerOptions;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import shaders.LightShaderWithNormal;

import java.util.logging.Logger;

public class Renderer {
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final NormalBatch normalBatch;
    BitmapFont font;
    /** our ground box **/
    RayHandler rayHandler;
    /** our box2D world **/
    FrameBuffer normalFbo;
    ShaderProgram lightShader;
    PointLight pointLight;
    float t = 0;
    IntArray backgroundEntities = new IntArray();
    IntArray blockBottomEntities = new IntArray();
    IntArray blockLowerEntities = new IntArray();
    IntArray blockUpperEntities = new IntArray();
    IntArray blockTopEntities = new IntArray();
    IntArray effectEntities = new IntArray();
    IntArray topEntities = new IntArray();
    private SpriteBatch batch;

    public Renderer() {

        MathUtils.random.setSeed(Long.MIN_VALUE);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(10, 10, camera);

        normalBatch = new NormalBatch();
        setBatch(new SpriteBatch());
        font = new BitmapFont();
        font.setColor(Color.RED);


        /** BOX2D LIGHT STUFF BEGIN */

        lightShader = LightShaderWithNormal.createLightShader();
        RayHandlerOptions options = new RayHandlerOptions();
        options.setDiffuse(true);
        options.setGammaCorrection(true);
        rayHandler = new RayHandler(Vars.ecs.getSystem(PhysicsSystem.class).getWorldId(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), options) {
            @Override
            protected void updateLightShader() {
                lightShader.setUniformi("u_normals", 1);
//                lightShader.setUniformf("u_resolution", 320, 240);
                lightShader.setUniformf("u_resolution", viewport.getScreenWidth(), viewport.getScreenHeight());
                lightShader.setUniformf("u_world",viewport.getWorldWidth(), viewport.getWorldHeight());
//                Gdx.app.log("LightShader", "u_resolution: " + viewport.getScreenWidth() + " " + viewport.getScreenHeight());
//                Gdx.app.log("LightShader", "u_world: " + viewport.getWorldWidth() + " " + viewport.getWorldHeight());
            }

            @Override
            protected void updateLightShaderPerLight(Light light) {
                // light position must be normalized
                Vector2 project = viewport.project(light.getPosition().cpy());
                lightShader.setUniformf("u_lightpos", project.x / viewport.getScreenWidth(), project.y / viewport.getScreenHeight(), 0.5f);
                lightShader.setUniformf("u_intensity", 10);
                lightShader.setUniformf("u_falloff", 0, 0, 0.005f);
            }
        };
        rayHandler.setBlur(true);
//        rayHandler.setBlurNum(2);
        rayHandler.setCulling(true);
        rayHandler.setLightShader(lightShader);
        rayHandler.setLightMapRendering(true);
        rayHandler.setShadows(true);
        rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 0.1f);


        /** BOX2D LIGHT STUFF END */


        pointLight = new PointLight(rayHandler, 128, new Color(0.1f, 0.1f, 0.1f, 0.1f), 8, 0, 0);
//        DirectionalLight directionalLight = new DirectionalLight(rayHandler, 128, Color.WHITE, 45);
    }


    public void render() {
//        pointLight.setPosition(pointLight.getPosition().rotateDeg(0.5f));

        float cameraX = camera.position.x;
        float cameraY = camera.position.y;
        float halfWidth = getViewport().getWorldWidth() / 2.0f;
        float halfHeight = getViewport().getWorldHeight() / 2.0f;

        backgroundEntities.clear();
        blockBottomEntities.clear();
        blockLowerEntities.clear();
        blockUpperEntities.clear();
        blockTopEntities.clear();
        effectEntities.clear();
        topEntities.clear();


        PhysicsSystem physicsSystem = Vars.ecs.getSystem(PhysicsSystem.class);
        Box2dPlus.b2WorldOverlapAABBbyEntity(physicsSystem.getWorldId(), cameraX - halfWidth, cameraY - halfHeight, cameraX + halfWidth, cameraY + halfHeight, new Box2dPlus.EntityCallback() {
            @Override
            public boolean b2OverlapResultFcn_call(long entity) {
                int entityId = (int) entity;
                DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entityId);
                drawable.renderLogic.scheduleRender(Renderer.this, entityId);
                return true;
            }
        });
        getViewport().apply();

        normalBatch.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        normalFbo.begin();
        Gdx.gl.glClearColor(0.5f, 0.5f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        normalBatch.begin();
        normalBatch.enableBlending();

        renderNormal(backgroundEntities);
        renderNormal(blockBottomEntities);
        renderNormal(blockLowerEntities);
        renderNormal(blockUpperEntities);
        renderNormal(blockTopEntities);
        renderNormal(effectEntities);
        renderNormal(topEntities);


        normalBatch.end();
        normalFbo.end();

        Texture normals = normalFbo.getColorBufferTexture();
        batch.begin();
        batch.setShader(null);
        batch.setColor(Color.WHITE);
        batch.enableBlending();
//        batch.draw(bg, -3, -2, 6, 4);
//        for (int i = -20; i < 20; i += 2) {
//            for (int j = -20; j < 20; j += 2) {
//                getBatch().draw(ttttt, i, j, 2, 2);
//            }
//        }
        // render all entities
        renderEntities(backgroundEntities);
        renderEntities(blockBottomEntities);
        renderEntities(blockLowerEntities);
        renderEntities(blockUpperEntities);
        renderEntities(blockTopEntities);
        renderEntities(effectEntities);
        renderEntities(topEntities);

//        batch.draw(normals, 0, 0, // x, y
//            viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, // origx, origy
//            viewport.getWorldWidth(), viewport.getWorldHeight(), // width, height
//            1, 1, // scale x, y
//            0,// rotation
//            0, 0, normals.getWidth(), normals.getHeight(), // tex dimensions
//            false, true); // flip x, y
        batch.end();
//        lightShader.bind();
//        lightShader.setUniformi("u_normals", 1);
//        lightShader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        /** BOX2D LIGHT STUFF BEGIN */
        rayHandler.setCombinedMatrix(camera);
        rayHandler.update();
        normals.bind(1);
        rayHandler.render();
        /** BOX2D LIGHT STUFF END */

    }

    private void renderEntities(IntArray entityList) {
        for (int i = 0; i < entityList.size; i++) {
            int entity = entityList.get(i);
            TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entity);
            DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entity);
            drawable.renderLogic.render(transformCM.transform, batch);
        }
    }

    private void renderNormal(IntArray entityList) {
        for (int i = 0; i < entityList.size; i++) {
            int entity = entityList.get(i);
            TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entity);
            DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entity);
            drawable.renderLogic.renderNormal(transformCM.transform, normalBatch);
        }
    }

    public void dispose() {
        rayHandler.dispose();
        normalFbo.dispose();
    }


    public void resize(int width, int height) {
        getViewport().update(width, height);
        if (normalFbo != null) normalFbo.dispose();
        normalFbo = new FrameBuffer(Pixmap.Format.RGB565, width, height, false);
//        lightShader.setUniformf("u_resolution", width, height);
//        System.out.println(width);
//        System.out.println(height);
    }

    public Viewport getViewport() {
        return viewport;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }


    public interface RenderLogic {
        Affine2 tmpAffine2 = new Affine2();

        default void scheduleRender(Renderer renderer, int entity) {
            renderer.blockUpperEntities.add(entity);
        }

        void render(Affine2 tfm, Batch bth);

        void renderNormal(Affine2 tfm, Batch bth);
    }

    public interface BackgroundRenderLogic extends RenderLogic {
        @Override
        default void scheduleRender(Renderer renderer, int entity) {
            renderer.backgroundEntities.add(entity);
        }
    }

    public interface EffectRenderLogic extends RenderLogic {
        @Override
        default void scheduleRender(Renderer renderer, int entity) {
            renderer.effectEntities.add(entity);
        }
    }

    public interface TopRenderLogic extends RenderLogic {
        @Override
        default void scheduleRender(Renderer renderer, int entity) {
            renderer.topEntities.add(entity);
        }
    }

    public interface BlockTopRenderLogic extends RenderLogic {
        @Override
        default void scheduleRender(Renderer renderer, int entity) {
            renderer.blockTopEntities.add(entity);
        }
    }

    public interface BlockBottomRenderLogic extends RenderLogic {
        @Override
        default void scheduleRender(Renderer renderer, int entity) {
            renderer.blockBottomEntities.add(entity);
        }
    }

    public interface BlockLowerRenderLogic extends RenderLogic {
        @Override
        default void scheduleRender(Renderer renderer, int entity) {
            renderer.blockLowerEntities.add(entity);
        }
    }
}
