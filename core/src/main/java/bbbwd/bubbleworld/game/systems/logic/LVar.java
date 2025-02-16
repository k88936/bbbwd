package bbbwd.bubbleworld.game.systems.logic;


public class LVar {
    public final String name;
    public int id;

    public boolean isObj, constant;

    public Object objVal;
    public double numVal;

    //ms timestamp for when this was last synced; used in the sync instruction
    public long syncTime;

    public LVar(String name) {
        this(name, -1);
    }

    public LVar(String name, int id) {
        this(name, id, false);
    }

    public LVar(String name, int id, boolean constant) {
        this.name = name;
        this.id = id;
        this.constant = constant;
    }

//    public @Nullable Building building(){
//        return isobj && objval instanceof Building building ? building : null;
//    }

    public static boolean invalid(double d) {
        return Double.isNaN(d) || Double.isInfinite(d);
    }

//    public @Nullable Team team(){
//        if(isobj){
//            return objval instanceof Team t ? t : null;
//        }else{
//            int t = (int)numval;
//            if(t < 0 || t >= Team.all.length) return null;
//            return Team.all[t];
//        }
//    }

    public Object obj() {
        return isObj ? objVal : null;
    }

    public boolean bool() {
        return isObj ? objVal != null : Math.abs(numVal) >= 0.00001;
    }

    public double num() {
        return isObj ? objVal != null ? 1 : 0 : invalid(numVal) ? 0 : numVal;
    }

    /** Get num value from variable, convert null to NaN to handle it differently in some instructions */
    public double numOrNan() {
        return isObj ? objVal != null ? 1 : Double.NaN : invalid(numVal) ? 0 : numVal;
    }

    public float numf() {
        return isObj ? objVal != null ? 1 : 0 : invalid(numVal) ? 0 : (float) numVal;
    }

    /** Get float value from variable, convert null to NaN to handle it differently in some instructions */
    public float numfOrNan() {
        return isObj ? objVal != null ? 1 : Float.NaN : invalid(numVal) ? 0 : (float) numVal;
    }

    public int numi() {
        return (int) num();
    }

    public void setBool(boolean value) {
        setNum(value ? 1 : 0);
    }

    public void setNum(double value) {
        if (constant) return;
        if (invalid(value)) {
            objVal = null;
            isObj = true;
        } else {
            numVal = value;
            objVal = null;
            isObj = false;
        }
    }

    public void setObj(Object value) {
        if (constant) return;
        objVal = value;
        isObj = true;
    }

    public void setConst(Object value) {
        objVal = value;
        isObj = true;
    }

    @Override
    public String toString() {
        return name + (isObj ? " = " + objVal : " = " + numVal);
    }
}
