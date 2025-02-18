package bbbwd.bubbleworld.content.blocks;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.core.Renderer;
import bbbwd.bubbleworld.game.components.DrawableCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import bbbwd.bubbleworld.game.components.physics.DynamicBodyCM;
import bbbwd.bubbleworld.game.systems.physics.PhysicsSystem;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.box2d.structs.b2BodyId;
import com.badlogic.gdx.box2d.structs.b2Hull;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;

public abstract class Block {


    public static final float defaultSize = 0.5f;
    public static final ConnectFilter defaultConnectionFilter = (newBlock, rx, ry) -> true;
    public static final Shape ShapeBox = new Shape() {
        @Override
        public b2BodyId buildShape(Affine2 transform, Block block) {
            PhysicsSystem physicsSystem = Vars.ecs.getSystem(PhysicsSystem.class);
            return Box2dPlus.b2CreateBlock(physicsSystem.getWorldId(), transform, block.size);

        }

        @Override
        public void overlap(Block block, float tolerance, Affine2 transform, Box2dPlus.EntityCallback callback) {
            PhysicsSystem physicsSystem = Vars.ecs.getSystem(PhysicsSystem.class);
            Box2dPlus.b2WorldOverlapSquare(physicsSystem.getWorldId(), block.size * tolerance, transform, callback);

        }
    };
    public static final Shape ShapeCircle = new Shape() {
        @Override
        public b2BodyId buildShape(Affine2 transform, Block block) {
            PhysicsSystem physicsSystem = Vars.ecs.getSystem(PhysicsSystem.class);
            return Box2dPlus.b2CreateCircle(physicsSystem.getWorldId(), transform, block.size);
        }

        @Override
        public void overlap(Block block, float tolerance, Affine2 transform, Box2dPlus.EntityCallback callback) {
            PhysicsSystem physicsSystem = Vars.ecs.getSystem(PhysicsSystem.class);
            Box2dPlus.b2WorldOverlapCircle(physicsSystem.getWorldId(), block.size * tolerance, transform, callback);
        }
    };
    public static TextureRegion defaultTexture;
    public static TextureRegion defaultNormal;
    public static final Renderer.RenderLogic defaultRenderLogic = new Renderer.RenderLogic() {

        @Override
        public void render(Affine2 tfm, Batch bth) {

            tfm.translate(-defaultSize, -defaultSize);
            bth.draw(defaultTexture, 2 * defaultSize, 2 * defaultSize, tfm);
            tfm.translate(defaultSize, defaultSize);
        }

        @Override
        public void renderNormal(Affine2 tfm, Batch bth) {

            tfm.translate(-defaultSize, -defaultSize);
            bth.draw(defaultNormal, 2 * defaultSize, 2 * defaultSize, tfm);
            tfm.translate(defaultSize, defaultSize);
        }
    };
    ////        TransformCM transformCM,
//        DynamicBodyCM boxCM
//    );
    public float size = 0.5f;
    public Shape shape = ShapeBox;
    public ConnectFilter connectFilter = defaultConnectionFilter;
    public Renderer.RenderLogic renderLogic = defaultRenderLogic;
    public b2Hull hull;
    Block() {
        init();
    }

    public Shape ShapePolygon(String id) {
        hull = Vars.resources.getHull(id);
        return new Shape() {
            @Override
            public b2BodyId buildShape(Affine2 transform, Block block) {
                PhysicsSystem physicsSystem = Vars.ecs.getSystem(PhysicsSystem.class);
                return Box2dPlus.b2CreatePolygon(physicsSystem.getWorldId(), transform, block.hull);
            }

            @Override
            public void overlap(Block block, float tolerance, Affine2 transform, Box2dPlus.EntityCallback callback) {
                PhysicsSystem physicsSystem = Vars.ecs.getSystem(PhysicsSystem.class);
                Box2dPlus.b2WorldOverlapPolygon(physicsSystem.getWorldId(), block.hull, transform, callback);

            }
        };
    }

    void init() {
        config();
    }

    abstract void config();

    public int create(Affine2 transform) {
        int entity = Vars.ecs.create();
        TransformCM transformCM = Vars.ecs.getMapper(TransformCM.class).create(entity);
        DrawableCM drawableCM = Vars.ecs.getMapper(DrawableCM.class).create(entity);
        drawableCM.renderLogic = renderLogic;
        DynamicBodyCM dynamicBodyCM = Vars.ecs.getMapper(DynamicBodyCM.class).create(entity);
        dynamicBodyCM.type = this;
        assert (MathUtils.isEqual(size * 4, MathUtils.round(size * 4)));
        Vars.ecs.getMapper(TransformCM.class).get(entity).transform.set(transform);
        dynamicBodyCM.bodyId = shape.buildShape(transform, this);
        Box2dPlus.b2BodySetRawUserData(dynamicBodyCM.bodyId, entity);
        return entity;
    }

    public interface ConnectFilter {
        /**
         * @param rx newBlock relative to oldBlock
         * @param ry newBlock relative to oldBlock
         * @return ture to pass(CANNOT connect)
         */
        boolean filterOut(Block newBlock, float rx, float ry);
    }

    public interface Shape {
        b2BodyId buildShape(Affine2 transform, Block block);

        default void overlap(Block block, Affine2 transform, Box2dPlus.EntityCallback callback) {
            overlap(block, 0.999f, transform, callback);
        }

        void overlap(Block block, float tolerance, Affine2 transform, Box2dPlus.EntityCallback callback);
    }

}

