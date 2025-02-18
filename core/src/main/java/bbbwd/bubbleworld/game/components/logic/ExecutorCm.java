package bbbwd.bubbleworld.game.components.logic;

import bbbwd.bubbleworld.game.systems.logic.LAssembler;
import bbbwd.bubbleworld.game.systems.logic.LVar;
import bbbwd.bubbleworld.game.systems.logic.LogicSystem;
import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

public class ExecutorCm extends PooledComponent {

    public LogicSystem.LInstruction[] instructions = {};
    //    public LongArray graphicsBuffer = new LongArray();
    public StringBuilder textBuffer = new StringBuilder();
    public LVar counter;
    public int ipt = 30;
    private int maxDevices = 10;
    public int[] devices= new int[maxDevices];

    /** Non-constant vari
    public boolean yield;ables used for network sync */
    public Array<LVar> vars = new Array<>();

    /** Loads with a specified assembler. Resets all variables. */
    public void load(LAssembler builder) {
        reset();
        int id = 0;
        for (LVar value : builder.vars.values()) {
            if (value.constant) continue;
            vars.add(value);
            value.id = id;
            id++;
        }
        instructions = builder.instructions;
        counter = builder.getVar("@counter");

//        unit = builder.getVar("@unit");
//        thisv = builder.getVar("@this");
//        ipt = builder.putConst("@ipt", build != null ? build.ipt : 0);todo
    }

    //region utility

//    /** @return a Var from this processor. May be null if out of bounds. */
//    public LVar optionalVar(int index) {
//        return index < 0 || index >= vars.size ? null : vars.get(index);
//    }

    @Override
    protected void reset() {


        vars.clear();
        instructions = null;
        counter = null;
        Arrays.fill(devices, -1);
        textBuffer.setLength(0);
    }
}
