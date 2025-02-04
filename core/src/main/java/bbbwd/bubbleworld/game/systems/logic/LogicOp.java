package bbbwd.bubbleworld.game.systems.logic;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

public enum LogicOp {
    add("+", (a, b) -> a + b),
    sub("-", (a, b) -> a - b),
    mul("*", (a, b) -> a * b),
    div("/", (a, b) -> a / b),
    idiv("//", (a, b) -> Math.floor(a / b)),
    mod("%", (a, b) -> a % b),
    pow("^", Math::pow),

    equal("==", (a, b) -> Math.abs(a - b) < 0.000001 ? 1 : 0, (a, b) -> Objects.equals(a, b) ? 1 : 0),
    notEqual("not", (a, b) -> Math.abs(a - b) < 0.000001 ? 0 : 1, (a, b) -> !Objects.equals(a, b) ? 1 : 0),
    land("and", (a, b) -> a != 0 && b != 0 ? 1 : 0),
    lessThan("<", (a, b) -> a < b ? 1 : 0),
    lessThanEq("<=", (a, b) -> a <= b ? 1 : 0),
    greaterThan(">", (a, b) -> a > b ? 1 : 0),
    greaterThanEq(">=", (a, b) -> a >= b ? 1 : 0),
    strictEqual("===", (a, b) -> 0), //this lambda is not actually used

    shl("<<", (a, b) -> (long) a << (long) b),
    shr(">>", (a, b) -> (long) a >> (long) b),
    or("or", (a, b) -> (long) a | (long) b),
    and("b-and", (a, b) -> (long) a & (long) b),
    xor("xor", (a, b) -> (long) a ^ (long) b),
    not("flip", a -> ~(long) (a)),

    max("max", true, Math::max),
    min("min", true, Math::min),
//    angle("angle", true, (x, y) -> MathUtils.atan2Deg((float) y, (float) x)),
//        angleDiff("anglediff", true, (x, y) -> Angles.angleDist((float)x, (float)y)),
    len("len", true, (x, y) -> Vector2.len((float) x, (float) y)),
    //    noise("noise", true, (x, y) -> Simplex.raw2d(0, x, y)),
    abs("abs", Math::abs), //not a method reference because it fails to compile for some reason
    log("log", Math::log),
    log10("log10", Math::log10),
    floor("floor", Math::floor),
    ceil("ceil", Math::ceil),
    sqrt("sqrt", Math::sqrt),
//    rand("rand", d -> GlobalVars.rand.nextDouble() * d),

    sin("sin", d -> MathUtils.sinDeg((float) d)),
    cos("cos", d -> MathUtils.cosDeg((float) d)),
    tan("tan", d -> MathUtils.tanDeg((float) d)),

    asin("asin", d -> MathUtils.asinDeg((float) d)),
    acos("acos", d -> MathUtils.acosDeg((float) d)),
    atan("atan", d -> MathUtils.atanDeg((float) d)),
    ;

    public static final LogicOp[] all = values();

    public final OpObjLambda2 objFunction2;
    public final OpLambda2 function2;
    public final OpLambda1 function1;
    public final boolean unary, func;
    public final String symbol;

    LogicOp(String symbol, OpLambda2 function) {
        this(symbol, function, null);
    }

    LogicOp(String symbol, boolean func, OpLambda2 function) {
        this.symbol = symbol;
        this.function2 = function;
        this.function1 = null;
        this.unary = false;
        this.objFunction2 = null;
        this.func = func;
    }

    LogicOp(String symbol, OpLambda2 function, OpObjLambda2 objFunction) {
        this.symbol = symbol;
        this.function2 = function;
        this.function1 = null;
        this.unary = false;
        this.objFunction2 = objFunction;
        this.func = false;
    }

    LogicOp(String symbol, OpLambda1 function) {
        this.symbol = symbol;
        this.function1 = function;
        this.function2 = null;
        this.unary = true;
        this.objFunction2 = null;
        this.func = false;
    }

    @Override
    public String toString() {
        return symbol;
    }

    interface OpObjLambda2 {
        double get(Object a, Object b);
    }

    interface OpLambda2 {
        double get(double a, double b);
    }

    interface OpLambda1 {
        double get(double a);
    }
}
