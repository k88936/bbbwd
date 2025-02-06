package bbbwd.bubbleworld.core;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.game.components.DrawableCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.systems.PhysicsSystem;
import box2dLight.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.box2d.structs.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import shaders.LightShaderWithNormal;
import shaders.NormalShader;

import java.util.ArrayList;

public class Renderer {
    IntArray visibleEntities = new IntArray();

    OrthographicCamera camera;
    Viewport viewport;
    SpriteBatch batch;
    BitmapFont font;
    /** our box2D world **/
    /** our ground box **/
    RayHandler rayHandler;
    Texture bg, bgN;
    TextureRegion objectReg, objectRegN;
    FrameBuffer normalFbo;
    ShaderProgram lightShader;
    ShaderProgram normalShader;

    public Renderer() {
        bg = new Texture("bg-deferred.png");
        bgN = new Texture("bg-deferred-n.png");

        MathUtils.random.setSeed(Long.MIN_VALUE);

        camera = new OrthographicCamera();
        camera.update();

        viewport = new ExtendViewport(15,15,camera);

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
                float x = (light.getX()) / viewport.getWorldWidth();
                float y = (light.getY()) / viewport.getWorldHeight();
                lightShader.setUniformf("u_lightpos", x, y, 0.05f);
                lightShader.setUniformf("u_intensity", 5);
            }
        };
        rayHandler.setLightShader(lightShader);
        rayHandler.setAmbientLight(0.5f, 0.5f, 0.5f, 0.5f);
        rayHandler.setBlurNum(0);

        normalFbo = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        /** BOX2D LIGHT STUFF END */




        new PointLight(rayHandler, 100, Color.WHITE, 20f, 5f, 3f);
    }

    public void render() {
        float cameraX = camera.position.x;
        float cameraY = camera.position.y;
        float halfWidth = viewport.getWorldWidth() / 2.0f;
        float halfHeight = viewport.getWorldHeight() / 2.0f;
        visibleEntities.clear();
        Vars.ecs.getSystem(PhysicsSystem.class).collect(cameraX - halfWidth, cameraY - halfHeight, cameraX + halfWidth, cameraY + halfHeight, visibleEntities);
        int bound = visibleEntities.size;
        viewport.apply();

        batch.setProjectionMatrix(camera.combined);
        normalFbo.begin();
        batch.begin();
        batch.setShader(normalShader);
        normalShader.setUniformf("u_rot", 1f,0f);
        batch.draw(bgN, 0, 0,6,4);

        for (int i = 0; i < bound; i++) {
            int entity = visibleEntities.get(i);
            TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entity);
            DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entity);
            normalShader.setUniformf("u_rot", transformCM.transform.m00, transformCM.transform.m10);
            drawable.renderLogic.renderNormal(transformCM.transform, batch);
            // TODO this is baaaad, maybe modify SpriteBatch to add rotation in the attributes? Flushing after each defeats the point of batch
            batch.flush();
        }
        batch.enableBlending();


        batch.end();
        normalFbo.end();

        Texture normals = normalFbo.getColorBufferTexture();

        batch.disableBlending();
        batch.begin();
        batch.setShader(null);

        batch.setColor(Color.WHITE);
        batch.enableBlending();
        batch.draw(bg, 0, 0,6,4);

        // render all entities
        for (int i = 0; i < bound; i++) {
            int entity = visibleEntities.get(i);
            TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).get(entity);
            DrawableCM drawable = Vars.ecs.getMapper(DrawableCM.class).get(entity);
            drawable.renderLogic.render(transformCM.transform, batch);
        }
        batch.end();

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
        viewport.update(width, height);
        if (normalFbo != null) normalFbo.dispose();
        normalFbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
    }

    public interface RenderLogic {
        void render(Affine2 tfm, SpriteBatch bth);

        void renderNormal(Affine2 tfm, SpriteBatch bth);
    }
}
