package bbbwd.bubbleworld.core.render;

import batchs.NormalBatch;
import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.render.liquid.LiquidBlurShader;
import bbbwd.bubbleworld.core.render.liquid.LiquidNormalComputeShader;
import bbbwd.bubbleworld.core.render.liquid.LiquidSpriteShader;
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
import com.badlogic.gdx.graphics.g2d.CpuSpriteBatch;
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

import static bbbwd.bubbleworld.game.systems.physics.PhysicsSystem.ALL;

public class Renderer {
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final NormalBatch normalBatch;
    private final Batch batch;
    private final ScreenDrawer screenDrawer;
    private final Affine2 tmp = new Affine2();
    BitmapFont font;
    /** our ground box **/
    RayHandler rayHandler;
    /** our box2D world **/
    FrameBuffer normalFbo;
    FrameBuffer liquidFbo;
    FrameBuffer liquidViceFbo;
    FrameBuffer liquidNormalFbo;
    ShaderProgram lightShader;
    ShaderProgram liquidShader;
    PointLight pointLight;
    Map<Layer, IntArray> drawables = new EnumMap<>(Layer.class);
    private final ShaderProgram liquidNormalShader;
    private final ShaderProgram liquidSpriteShader;


    public Renderer() {

        for (Layer value : Layer.values()) {
            drawables.put(value, new IntArray());
        }

        MathUtils.random.setSeed(Long.MIN_VALUE);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(10, 10, camera);

        normalBatch = new NormalBatch();
        batch = new CpuSpriteBatch();
        screenDrawer = new ScreenDrawer();
        font = new BitmapFont();
        font.setColor(Color.RED);


        /* BOX2D LIGHT STUFF BEGIN */
        lightShader = LightShaderWithNormal.createLightShader();
        liquidShader = LiquidBlurShader.createLiquidBlurShader();
        int kernel_size = 9;
        float[] kernel = createGaussianKernel(kernel_size, 8);
        liquidShader.bind();
        liquidShader.setUniformi("u_range", kernel_size);
        liquidShader.setUniform1fv("u_kernel", kernel, 0, kernel.length);
        liquidNormalShader = LiquidNormalComputeShader.createLiquidNormalComputeShader();
        liquidSpriteShader = LiquidSpriteShader.createLiquidSpriteShader();
        RayHandlerOptions options = new RayHandlerOptions();
        options.setDiffuse(true);
        options.setGammaCorrection(true);
        rayHandler = new RayHandler(Vars.ecs.getSystem(PhysicsSystem.class).getWorldId(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), options) {
            @Override
            protected void updateLightShader() {
                lightShader.setUniformi("u_normals", 1);
                lightShader.setUniformf("u_resolution", viewport.getScreenWidth(), viewport.getScreenHeight());
                lightShader.setUniformf("u_world", viewport.getWorldWidth(), viewport.getWorldHeight());
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

    public static float[] createGaussianKernel(int radius, float sigma) {
        int size = 2 * radius + 1;
        float[] kernel = new float[size];
        float sum = 0;
        int index = 0;
        for (int i = -radius; i <= radius; i++) {
            kernel[index] = (float) (Math.exp(-i * i / (2 * sigma * sigma)) / (Math.sqrt(2 * Math.PI) * sigma));
            sum += kernel[index];
            index++;
        }
        // 归一化核
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }

        return kernel;
    }

    public void render() {
        float cameraX = camera.position.x;
        float cameraY = camera.position.y;
        float halfWidth = getViewport().getWorldWidth() / 2.0f;
        float halfHeight = getViewport().getWorldHeight() / 2.0f;
        drawables.values().forEach(IntArray::clear);
        PhysicsSystem physicsSystem = Vars.ecs.getSystem(PhysicsSystem.class);
        Box2dPlus.b2WorldOverlapAABBbyEntity(physicsSystem.getWorldId(), cameraX - halfWidth, cameraY - halfHeight, cameraX + halfWidth, cameraY + halfHeight, ALL, entity -> {
            int entityId = (int) entity;
            DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entityId);
            drawables.get(drawable.renderLogic.layer).add(entityId);
            return true;
        });
        getViewport().apply();


        batch.setProjectionMatrix(camera.combined);
        batch.setShader(null);
        liquidFbo.begin();
        Gdx.gl20.glClearColor(0, 0, 0, 0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
        batch.setColor(Color.ORANGE);
        renderSprite(drawables.get(Layer.LIQUID));
        batch.end();
        liquidFbo.end();
        Texture liquid = liquidFbo.getColorBufferTexture();
//

        liquidShader.bind();
        liquidShader.setUniformf("u_resolution", viewport.getScreenWidth(), viewport.getScreenHeight());
        liquidShader.setUniformi("u_step", 0);
        screenDrawer.setShader(liquidShader);
        screenDrawer.disableBlending();
        liquidViceFbo.begin();
        Gdx.gl20.glClearColor(0, 0, 0, 0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        screenDrawer.begin();
        screenDrawer.drawScreen(liquid);
        screenDrawer.end();
        liquidViceFbo.end();
        liquid = liquidViceFbo.getColorBufferTexture();

        liquidShader.setUniformi("u_step", 1);
        screenDrawer.setShader(liquidShader);
        liquidFbo.begin();
        Gdx.gl20.glClearColor(0, 0, 0, 0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        screenDrawer.begin();
        screenDrawer.drawScreen(liquid);
        screenDrawer.end();
        liquidFbo.end();
        Texture liquidProcessedTexture = liquidFbo.getColorBufferTexture();


        normalFbo.begin();
        Gdx.gl.glClearColor(0.5f, 0.5f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        normalBatch.setProjectionMatrix(camera.combined);
        normalBatch.begin();
        renderNormal(drawables.get(Layer.BACKGROUND));
        renderNormal(drawables.get(Layer.BLOCK_BOTTOM));
        renderNormal(drawables.get(Layer.BLOCK_LOWER));
        renderNormal(drawables.get(Layer.BLOCK_UPPER));
        normalBatch.end();

        liquidNormalShader.bind();
        liquidNormalShader.setUniformf("u_resolution", viewport.getScreenWidth(), viewport.getScreenHeight());
        liquidNormalShader.setUniformf("u_world", viewport.getWorldWidth(), viewport.getWorldHeight());
        screenDrawer.enableBlending();
        screenDrawer.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        screenDrawer.setShader(liquidNormalShader);
//        liquidBatch.setShader(null);
        screenDrawer.begin();
        screenDrawer.drawScreen(liquidProcessedTexture);
        screenDrawer.end();

        normalBatch.begin();
        renderNormal(drawables.get(Layer.BLOCK_TOP));
        renderNormal(drawables.get(Layer.EFFECT));
        renderNormal(drawables.get(Layer.TOP));
        normalBatch.end();
        normalFbo.end();


//        if(true) return;

        Texture normals = normalFbo.getColorBufferTexture();


        batch.setProjectionMatrix(camera.combined);
        batch.setShader(null);
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(Color.WHITE);
        batch.begin();
        renderSprite(drawables.get(Layer.BACKGROUND));
        renderSprite(drawables.get(Layer.BLOCK_BOTTOM));
        renderSprite(drawables.get(Layer.BLOCK_LOWER));
        renderSprite(drawables.get(Layer.BLOCK_UPPER));
        batch.end();

        screenDrawer.enableBlending();
        screenDrawer.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        screenDrawer.setShader(liquidSpriteShader);
        screenDrawer.begin();
        screenDrawer.drawScreen(liquidProcessedTexture);
        screenDrawer.end();

        batch.begin();
        renderSprite(drawables.get(Layer.BLOCK_TOP));
        renderSprite(drawables.get(Layer.EFFECT));
        renderSprite(drawables.get(Layer.TOP));
        batch.end();

        rayHandler.setCombinedMatrix(camera);
        rayHandler.update();
        normals.bind(1);
        rayHandler.render();
    }

    private void renderSprite(IntArray entityList) {
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
        liquidFbo.dispose();
        batch.dispose();
        normalBatch.dispose();
        screenDrawer.dispose();
        font.dispose();

    }


    public void resize(int width, int height) {
        if (width * height == 0) return;
        getViewport().update(width, height);
        if (normalFbo != null) normalFbo.dispose();
        if (liquidFbo != null) liquidFbo.dispose();
        if (liquidNormalFbo != null) liquidNormalFbo.dispose();
        if (liquidViceFbo != null) liquidViceFbo.dispose();
        normalFbo = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        liquidFbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        liquidViceFbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        liquidNormalFbo = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Batch getBatch() {
        return batch;
    }

    public enum Layer {
        BACKGROUND,
        BLOCK_BOTTOM,
        BLOCK_LOWER,
        BLOCK_UPPER, LIQUID,
        BLOCK_TOP,
        EFFECT,
        TOP
    }


}
