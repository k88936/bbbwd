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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import shaders.LightShaderWithNormal;

import java.util.EnumMap;
import java.util.Map;

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
    private SpriteBatch batch;

    Map<Layer, IntArray> drawables = new EnumMap<>(Layer.class);

    public Renderer() {

        for (Layer value : Layer.values()) {
            drawables.put(value, new IntArray());
        }

        MathUtils.random.setSeed(Long.MIN_VALUE);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(10, 10, camera);

        normalBatch = new NormalBatch();
        setBatch(new SpriteBatch());
        font = new BitmapFont();
        font.setColor(Color.RED);


        /* BOX2D LIGHT STUFF BEGIN */
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
                lightShader.setUniformf("u_world", viewport.getWorldWidth(), viewport.getWorldHeight());
//                Gdx.app.log("LightShader", "u_resolution: " + viewport.getScreenWidth() + " " + viewport.getScreenHeight());
//                Gdx.app.log("LightShader", "u_world: " + viewport.getWorldWidth() + " " + viewport.getWorldHeight());
            }

            @Override
            protected void updateLightShaderPerLight(Light light) {
                // light position must be normalized
                Vector2 project = viewport.project(light.getPosition().cpy());
                lightShader.setUniformf("u_lightpos", project.x / viewport.getScreenWidth(), project.y / viewport.getScreenHeight(), 0.5f);
                lightShader.setUniformf("u_intensity", 10);
                lightShader.setUniformf("u_falloff", 0, 0, 0.01f);
            }
        };
        rayHandler.setBlur(true);
        rayHandler.setBlurNum(2);
        rayHandler.setCulling(true);
        rayHandler.setLightShader(lightShader);
        rayHandler.setLightMapRendering(true);
        rayHandler.setShadows(true);
        rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 0.1f);
        /* BOX2D LIGHT STUFF END */


        pointLight = new PointLight(rayHandler, 128, new Color(0.1f, 0.1f, 0.1f, 0.1f), 8, 0, 0);
//        DirectionalLight directionalLight = new DirectionalLight(rayHandler, 128, Color.WHITE, 45);
    }


    public void render() {
        float cameraX = camera.position.x;
        float cameraY = camera.position.y;
        float halfWidth = getViewport().getWorldWidth() / 2.0f;
        float halfHeight = getViewport().getWorldHeight() / 2.0f;
        drawables.values().forEach(IntArray::clear);
        PhysicsSystem physicsSystem = Vars.ecs.getSystem(PhysicsSystem.class);
        Box2dPlus.b2WorldOverlapAABBbyEntity(physicsSystem.getWorldId(), cameraX - halfWidth, cameraY - halfHeight, cameraX + halfWidth, cameraY + halfHeight, new Box2dPlus.EntityCallback() {
            @Override
            public boolean b2OverlapResultFcn_call(long entity) {
                int entityId = (int) entity;
                DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entityId);
                drawables.get(drawable.renderLogic.layer).add(entityId);
                return true;
            }
        });
        getViewport().apply();

//        if(true) return;
        normalBatch.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        normalFbo.begin();
        Gdx.gl.glClearColor(0.5f, 0.5f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        normalBatch.begin();

        drawables.values().forEach(this::renderNormal);


        normalBatch.end();
        normalFbo.end();

        Texture normals = normalFbo.getColorBufferTexture();
        batch.begin();
        batch.setShader(null);
        batch.setColor(Color.WHITE);
        batch.enableBlending();
        // render all entities
        drawables.values().forEach(this::renderEntities);

        batch.end();
        /** BOX2D LIGHT STUFF BEGIN */
        rayHandler.setCombinedMatrix(camera);
        rayHandler.update();
        normals.bind(1);
        rayHandler.render();
        /** BOX2D LIGHT STUFF END */

    }

    private final Affine2 tmp = new Affine2();

    private void renderEntities(IntArray entityList) {
        for (int i = 0; i < entityList.size; i++) {
            int entity = entityList.get(i);
            TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entity);
            DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entity);
            tmp.set(transformCM.transform).translate(-drawable.renderLogic.size, -drawable.renderLogic.size);
            drawable.renderLogic.render(tmp, batch);
        }
    }

    private void renderNormal(IntArray entityList) {
        for (int i = 0; i < entityList.size; i++) {
            int entity = entityList.get(i);
            TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entity);
            DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entity);
            tmp.set(transformCM.transform).translate(-drawable.renderLogic.size, -drawable.renderLogic.size);
            drawable.renderLogic.renderNormal(tmp, normalBatch);
        }
    }

    public void dispose() {
        rayHandler.dispose();
        normalFbo.dispose();
    }


    public void resize(int width, int height) {
        if (width * height == 0) return;
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

    public enum Layer {
        BACKGROUND,
        BLOCK_BOTTOM,
        BLOCK_LOWER,
        BLOCK_UPPER,
        BLOCK_TOP,
        EFFECT,
        TOP
    }

    public abstract static class RenderLogic {
        public final Layer layer;
        public final float size;

        RenderLogic(Layer layer, float size) {
            this.layer = layer;
            this.size = size;
        }

        public abstract void render(Affine2 tfm, Batch bth);

        abstract void renderNormal(Affine2 tfm, Batch bth);

        public static Renderer.RenderLogic of(final String name, Renderer.Layer layer, float size) {
            final TextureRegion texture = Vars.resources.getTexureRegion(name);
            final TextureRegion normal = Vars.resources.getTexureRegion(name + ".normal");
            return new Renderer.RenderLogic(layer, size) {
                @Override
                public void render(Affine2 tfm, Batch bth) {
                    bth.draw(texture, 2 * size, 2 * size, tfm);
                }
                @Override
                public void renderNormal(Affine2 tfm, Batch bth) {
                    bth.draw(normal, 2 * size, 2 * size, tfm);
                }
            };
        }

        public static Renderer.RenderLogic of(final String name, float size) {
            return of(name, Layer.BLOCK_UPPER, size);
        }

    }

    public static class SimpleRenderLogic extends RenderLogic {

        final RenderLogic Lower;
        final RenderLogic Upper;

        public SimpleRenderLogic(RenderLogic a, RenderLogic b) {
            super(null, 0);
            if (a.layer.compareTo(b.layer) > 0) {
                Lower = b;
                Upper = a;
            } else {
                Lower = a;
                Upper = b;
            }
        }

        @Override
        public void render(Affine2 tfm, Batch bth) {
            Lower.render(tfm, bth);
            Upper.render(tfm, bth);
        }

        @Override
        public void renderNormal(Affine2 tfm, Batch bth) {
        }
    }


}
