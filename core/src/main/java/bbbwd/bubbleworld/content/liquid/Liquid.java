package bbbwd.bubbleworld.content.liquid;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.render.RenderLogic;
import bbbwd.bubbleworld.core.render.Renderer;
import bbbwd.bubbleworld.game.components.DrawableCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.components.TypeCM;
import bbbwd.bubbleworld.game.components.physics.DynamicBodyCM;
import bbbwd.bubbleworld.game.systems.physics.PhysicsSystem;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;

public abstract class Liquid {
    static final float defaultSize = 0.125f;
    static final float defaultDensity = 0.5f;
//    static final float defaultViscosity = 0.5f;

    public static final Box2dPlus.ContactFilter defaultContactFilter = new Box2dPlus.ContactFilter(0, PhysicsSystem.CollisionType.LIQUID.get(),
            PhysicsSystem.CollisionType.SOLID.get() | PhysicsSystem.CollisionType.BLOCK.get() | PhysicsSystem.CollisionType.LIQUID.get());

    public float size;
    public LiquidRenderLogic renderLogic;

    Liquid() {
        setupDefault();
        otherSetting();
    }

    void setupDefault() {
        size = defaultSize;
    }

    void otherSetting() {
        config();
    }

    public abstract void config();

    public int create(Affine2 transform) {
        int entity = Vars.ecs.create();
        TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).create(entity);
        DrawableCM drawableCM = Vars.ecs.getMapper(DrawableCM.class).create(entity);
        drawableCM.renderLogic = renderLogic;
        DynamicBodyCM dynamicBodyCM = Vars.ecs.getMapper(DynamicBodyCM.class).create(entity);
        TypeCM typeCM = Vars.ecs.getMapper(TypeCM.class).create(entity);
        typeCM.liquidType = this;
        assert (MathUtils.isEqual(size * 4, MathUtils.round(size * 4)));
        Vars.ecs.getMapper(TransformCM.class).get(entity).transform.set(transform);
        dynamicBodyCM.bodyId = Box2dPlus.b2CreateCircle(Vars.ecs.getSystem(PhysicsSystem.class).getWorldId(), transform, size,defaultContactFilter);
        Box2dPlus.b2BodySetRawUserData(dynamicBodyCM.bodyId, entity);
        return entity;

    }

    public static class LiquidRenderLogic extends RenderLogic {
        private final TextureRegion texture;

        LiquidRenderLogic( TextureRegion texture,float size) {
            super(Renderer.Layer.LIQUID, size);
            this.texture = texture;
        }
        public static LiquidRenderLogic of(final String name, float size) {
            final TextureRegion texture = Vars.resources.getTexureRegionFromPack(name);
            return new LiquidRenderLogic(texture, size) {
            };
        }

        @Override
        public void render(Affine2 tfm, Batch bth) {
            bth.draw(texture, 2 * size, 2 * size, tfm);
        }

        @Override
        public void renderNormal(Affine2 tfm, Batch bth) {
        }
    }
}
