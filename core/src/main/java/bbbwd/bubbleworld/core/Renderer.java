package bbbwd.bubbleworld.core;

import batchs.NormalBatch;
import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.DrawableCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import box2dLight.RayHandlerOptions;
import com.badlogic.gdx.Gdx;
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

public class Renderer {
    IntArray visibleEntities = new IntArray();
    BitmapFont font;
    /** our ground box **/
    RayHandler rayHandler;
    Texture bg, bgN;
    TextureRegion objectReg, objectRegN;
    /** our box2D world **/
    FrameBuffer normalFbo;
    ShaderProgram lightShader;
    PointLight pointLight;
    Texture ttttt;
    float t = 0;
    private OrthographicCamera camera;
    private Viewport viewport;
    private NormalBatch normalBatch;
    private SpriteBatch batch;

    public Renderer() {
        bg = new Texture("bg-deferred.png");
        bgN = new Texture("bg-deferred-n.png");
        ttttt = new Texture("object-deferred-n.png");

        MathUtils.random.setSeed(Long.MIN_VALUE);

        camera = new OrthographicCamera();
        camera.update();

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
            protected void updateLightShaderPerLight(Light light) {
                // light position must be normalized
                Vector2 project = getViewport().project(light.getPosition().cpy());
                lightShader.setUniformf("u_lightpos", project.x / getViewport().getScreenWidth(), project.y / getViewport().getScreenHeight(), 0.05f);
                lightShader.setUniformf("u_intensity", 5);
                lightShader.setUniformf("u_falloff", 0, 0, 1);
            }
        };
        rayHandler.setLightShader(lightShader);
        rayHandler.setAmbientLight(0.4f, 0.4f, 0.4f, 0.5f);
        rayHandler.setBlurNum(0);

        /** BOX2D LIGHT STUFF END */
        camera.position.x = 3;
        camera.position.y = 4;


        pointLight = new PointLight(rayHandler, 128, Color.WHITE, 4, 4, 4);
    }

    public void render() {
//        pointLight.setPosition(pointLight.getPosition().rotateDeg(0.5f));

        float cameraX = camera.position.x;
        float cameraY = camera.position.y;
        float halfWidth = getViewport().getWorldWidth() / 2.0f;
        float halfHeight = getViewport().getWorldHeight() / 2.0f;
        visibleEntities.clear();
        Vars.ecs.getSystem(PhysicsSystem.class).collect(cameraX - halfWidth, cameraY - halfHeight, cameraX + halfWidth, cameraY + halfHeight, visibleEntities);
        int bound = visibleEntities.size;
        getViewport().apply();

        normalBatch.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        normalFbo.begin();
        Gdx.gl.glClearColor(0.5f, 0.5f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        normalBatch.begin();
        for (int i = 0; i < bound; i++) {
            int entity = visibleEntities.get(i);
            TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entity);
            DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entity);
            drawable.renderLogic.renderNormal(transformCM.transform, normalBatch);
            // TODO this is baaaad, maybe modify SpriteBatch to add rotation in the attributes? Flushing after each defeats the point of batch
        }
        normalBatch.enableBlending();


        normalBatch.end();
        normalFbo.end();

        Texture normals = normalFbo.getColorBufferTexture();

        batch.disableBlending();
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
        for (int i = 0; i < bound; i++) {
            int entity = visibleEntities.get(i);
            TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entity);
            DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entity);
            drawable.renderLogic.render(transformCM.transform, batch);
        }
//        batch.draw(normals, 0, 0, // x, y
//            viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, // origx, origy
//            viewport.getWorldWidth(), viewport.getWorldHeight(), // width, height
//            1, 1, // scale x, y
//            0,// rotation
//            0, 0, normals.getWidth(), normals.getHeight(), // tex dimensions
//            false, true); // flip x, y
        batch.end();
//  lightShader.bind();
//        lightShader.setUniformi("u_normals", 1);
//        lightShader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        /** BOX2D LIGHT STUFF BEGIN */
        rayHandler.setCombinedMatrix(camera);
        rayHandler.update();
        normals.bind(1);
        rayHandler.render();
        /** BOX2D LIGHT STUFF END */

    }

    public void dispose() {
        rayHandler.dispose();

        objectReg.getTexture().dispose();
        objectRegN.getTexture().dispose();

        normalFbo.dispose();
    }


    public void resize(int width, int height) {
        getViewport().update(width, height);
        if (normalFbo != null) normalFbo.dispose();
        normalFbo = new FrameBuffer(Pixmap.Format.RGB565, width, height, false);
//        lightShader.bind();
        lightShader.setUniformi("u_normals", 1);
        lightShader.setUniformf("u_resolution", width, height);
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
        void render(Affine2 tfm, Batch bth);

        void renderNormal(Affine2 tfm, Batch bth);
    }
}
