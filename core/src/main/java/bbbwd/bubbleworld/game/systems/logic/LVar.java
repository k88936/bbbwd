package bbbwd.bubbleworld.game.systems.logic;


public class LVar {
    public final String name;
    public int id;

    public boolean constant;

    //ms timestamp for when this was last synced; used in the sync instruction
    public long syncTime;
    private double numVal;

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


    public boolean bool() {
        return Math.abs(getNumVal()) >= 0.00001;
    }

    public double num() {
        return invalid(getNumVal()) ? 0 : numVal;
    }


    public float numf() {
        return invalid(getNumVal()) ? 0 : (float) numVal;
    }

    /** Get float value from variable, convert null to NaN to handle it differently in some instructions */
    public float numfOrNan() {
        return invalid(getNumVal()) ? 0 : (float) numVal;
    }

    public int numi() {
        return (int) num();
    }

    public void setBool(boolean value) {
        double value1 = value ? 1 : 0;
        if (constant) return;
        this.numVal = value1;
    }

    public void setNum(double value) {
        if (constant) return;
        this.numVal = value;
    }


//    public void setConst(Object value) {
//        objVal = value;
//        isObj = true;
//    }


    public void reset() {
        setNumVal(0);
    }

    public double getNumVal() {
        return numVal;
    }

    public double setNumVal(double numVal) {
        this.numVal = numVal;
        return numVal;
    }

    public void increment() {
        numVal++;
    }

    public void increment(double delta) {
        numVal += delta;
    }
}
