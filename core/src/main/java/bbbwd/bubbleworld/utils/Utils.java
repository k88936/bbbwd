package bbbwd.bubbleworld.utils;

import bbbwd.bubbleworld.Vars;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Utils {
    public static Affine2 rotateHalfPi(Affine2 affine2) {
        float m00 = affine2.m00;
        float m01 = affine2.m01;
        float m10 = affine2.m10;
        float m11 = affine2.m11;
        affine2.m00 = m10;
        affine2.m01 = -m00;
        affine2.m10 = m11;
        affine2.m11 = -m01;
        return affine2;
    }

    public static float computeRotReference(Affine2 A, Affine2 B) {
        return  (float) Math.atan2(B.m00 * A.m10 - B.m10 * A.m00, B.m00 * A.m00 + B.m10 * A.m10);
    }

    public static Vector2 Gridize(Vector2 v, float base) {


        v.x = MathUtils.floor((v.x - base) / (Vars.GRID_SIZE * 2)) * Vars.GRID_SIZE * 2 + base;
        v.y = MathUtils.floor((v.y - base) / (Vars.GRID_SIZE * 2)) * Vars.GRID_SIZE * 2 + base;
        return v;
    }
}
