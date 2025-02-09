import bbbwd.bubbleworld.content.blocks.Blocks;
import bbbwd.bubbleworld.game.components.BoxCM;
import bbbwd.bubbleworld.game.components.TransformCM;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.box2d.enums.b2BodyType;
import com.badlogic.gdx.box2d.structs.*;
import com.badlogic.gdx.jnigen.runtime.closure.ClosureObject;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.badlogic.gdx.math.Affine2;
import org.junit.jupiter.api.Test;

public class PhysicsTest {
    @Test
    public void TestSquareOverlap() {
        TestUtils.init();
        Affine2 tmp = new Affine2().translate(3.0f, 5.0f);
        TestUtils.setBlock(tmp, Blocks.testBlock);


        final boolean[] ok = {false};
        Box2dPlus.b2WorldOverlapSquare(TestUtils.worldId, 0.4f, tmp, ClosureObject.fromClosure(entity1 -> {
            ok[0] = true;
            return false;
        }));
//        assert ok[0];
    }

    @Test
    public void TestSquareOverlap2() {
        TestUtils.init();
//        Affine2 tmp = new Affine2().translate(3.0f, 5.0f);
//        TestUtils.setBlock(tmp, Blocks.testBlock);

        float x=0.0f;
        float y=5.0f;
        float boxSize=0.5f;
        float testSize=0.4f;
        b2Transform tfm = new b2Transform();
        tfm.p().x(x);
        tfm.p().y(y);
        tfm.q().c(1f);
        tfm.q().s(0f);

        b2BodyDef def = Box2d.b2DefaultBodyDef();
        def.position().x(x);
        def.position().y(y);
        def.rotation().c(1.0f);
        def.rotation().s(0f);
        def.type(b2BodyType.b2_dynamicBody);
        b2Polygon b2Polygon = Box2d.b2MakeSquare(boxSize);
        b2ShapeDef shape = Box2d.b2DefaultShapeDef();
        b2BodyId bodyId = Box2d.b2CreateBody(TestUtils.worldId, def.asPointer());
        Box2d.b2CreatePolygonShape(bodyId, shape.asPointer(), b2Polygon.asPointer());

        b2Polygon test_polygon = Box2d.b2MakeSquare(testSize);
        final boolean[] ok = {false};
        Box2d.b2World_OverlapPolygon(TestUtils.worldId, test_polygon.asPointer(), tfm, Box2d.b2DefaultQueryFilter(), ClosureObject.fromClosure((new Box2d.b2OverlapResultFcn() {
            @Override
            public boolean b2OverlapResultFcn_call(b2ShapeId shapeId, VoidPointer context) {
                ok[0] = true;
                return false;
            }
        })), new VoidPointer(1));
//        assert ok[0];

    }
}
