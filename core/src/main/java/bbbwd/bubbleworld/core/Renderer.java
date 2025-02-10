package bbbwd.bubbleworld.core;

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
import shaders.NormalShader;

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
    ShaderProgram normalShader;
    PointLight pointLight;
    Texture ttttt;
    float t = 0;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;

    public Renderer() {
        bg = new Texture("bg-deferred.png");
        bgN = new Texture("bg-deferred-n.png");
        ttttt = new Texture("object-deferred-n.png");

        MathUtils.random.setSeed(Long.MIN_VALUE);

        camera = new OrthographicCamera();
        getCamera().update();

        viewport = new ExtendViewport(10, 10, getCamera());

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);


        /** BOX2D LIGHT STUFF BEGIN */
        normalShader = NormalShader.createNormalShader();

        lightShader = LightShaderWithNormal.createLightShader();
        RayHandlerOptions options = new RayHandlerOptions();
        options.setDiffuse(true);

        options.setGammaCorrection(true);
        rayHandler = new RayHandler(Vars.ecs.getSystem(PhysicsSystem.class).getWorldId(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), options) {
            @Override
            protected void updateLightShaderPerLight(Light light) {
                // light position must be normalized
                Vector2 project = getViewport().project(light.getPosition().cpy());
//                Logger.getGlobal().info(" xy:"+project);
//                lightShader.setUniformf("u_lightpos", project.x, project.y, 0.05f);
//                lightShader.setUniformf("u_lightpos", 0.5f, 0.5f, 0.05f);
//                lightShader.setUniformf("u_lightpos", 0f, 0f, 0.05f);
                lightShader.setUniformf("u_lightpos", project.x / getViewport().getScreenWidth(), project.y / getViewport().getScreenHeight(), 0.05f);
//                System.out.println("x: "+project.x/viewport.getScreenWidth());
//                System.out.println("y: "+project.y/viewport.getScreenHeight());
//                System.out.println("w: "+viewport.getScreenWidth());
//                System.out.println("h: "+viewport.getScreenHeight());
                lightShader.setUniformf("u_intensity", 5);
                lightShader.setUniformf("u_falloff", 0, 0, 1);
            }
        };
        rayHandler.setLightShader(lightShader);
        rayHandler.setAmbientLight(0.4f, 0.4f, 0.4f, 0.5f);
        rayHandler.setBlurNum(0);

        /** BOX2D LIGHT STUFF END */
        getCamera().position.x = 3;
        getCamera().position.y = 4;


        pointLight = new PointLight(rayHandler, 128, Color.WHITE, 4, 4, 4);
    }

    public void render() {
//        pointLight.setPosition(pointLight.getPosition().rotateDeg(0.5f));

        float cameraX = getCamera().position.x;
        float cameraY = getCamera().position.y;
        float halfWidth = getViewport().getWorldWidth() / 2.0f;
        float halfHeight = getViewport().getWorldHeight() / 2.0f;
        visibleEntities.clear();
        Vars.ecs.getSystem(PhysicsSystem.class).collect(cameraX - halfWidth, cameraY - halfHeight, cameraX + halfWidth, cameraY + halfHeight, visibleEntities);
        int bound = visibleEntities.size;
        getViewport().apply();

        getBatch().setProjectionMatrix(getCamera().combined);
        normalFbo.begin();
        Gdx.gl.glClearColor(0.5f, 0.5f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        getBatch().begin();
        getBatch().setShader(normalShader);
        normalShader.setUniformf("u_rot", 1f, 0f);
//        for (int i = -20; i < 20; i += 2) {
//            for (int j = -20; j < 20; j += 2) {
//                getBatch().draw(ttttt, i, j, 2, 2);
//            }
//        }
//        batch.draw(bgN, -3, -2, 6, 4);

        for (int i = 0; i < bound; i++) {
            int entity = visibleEntities.get(i);
            TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entity);
            DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entity);
            normalShader.setUniformf("u_rot", transformCM.transform.m00, transformCM.transform.m10);
            drawable.renderLogic.renderNormal(transformCM.transform, getBatch());
            // TODO this is baaaad, maybe modify SpriteBatch to add rotation in the attributes? Flushing after each defeats the point of batch
            getBatch().flush();
        }
        getBatch().enableBlending();


        getBatch().end();
        normalFbo.end();

        Texture normals = normalFbo.getColorBufferTexture();

        getBatch().disableBlending();
        getBatch().begin();
        getBatch().setShader(null);

        getBatch().setColor(Color.WHITE);
        getBatch().enableBlending();
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
            drawable.renderLogic.render(transformCM.transform, getBatch());
        }
//        batch.draw(normals, 0, 0, // x, y
//            viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, // origx, origy
//            viewport.getWorldWidth(), viewport.getWorldHeight(), // width, height
//            1, 1, // scale x, y
//            0,// rotation
//            0, 0, normals.getWidth(), normals.getHeight(), // tex dimensions
//            false, true); // flip x, y
        getBatch().end();
//  lightShader.bind();
//        lightShader.setUniformi("u_normals", 1);
//        lightShader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        /** BOX2D LIGHT STUFF BEGIN */
        rayHandler.setCombinedMatrix(getCamera());
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

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public interface RenderLogic {
        void render(Affine2 tfm, Batch bth);

        void renderNormal(Affine2 tfm, Batch bth);
    }
}
