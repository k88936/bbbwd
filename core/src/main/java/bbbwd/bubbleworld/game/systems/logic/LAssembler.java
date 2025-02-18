package bbbwd.bubbleworld.game.systems.logic;

import bbbwd.bubbleworld.Vars;
import bbbwd.bubbleworld.utils.Utils;
import bbbwd.bubbleworld.utils.func.Func;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;


/** "Compiles" a sequence of statements into instructions. */
public class LAssembler{
    public static ObjectMap<String, Func<String[], LStatement>> customParsers = new ObjectMap<>();

    private static final int invalidNum = Integer.MIN_VALUE;

    private boolean privileged;
    /** Maps names to variable. */
    public OrderedMap<String, LVar> vars = new OrderedMap<>();
    /** All instructions to be executed. */
    public LogicSystem.LInstruction[] instructions;

    public LAssembler(){
        //instruction counter
        putVar("@counter");
        //currently controlled unit
        putConst("@unit", 0);
        //reference to self
        putConst("@this", 0);
    }

    public static LAssembler assemble(String data){
        LAssembler asm = new LAssembler();

        Array<LStatement> st = read(data);


        Array<LogicSystem.LInstruction> insts = new Array<>();
        for (LStatement lStatement : st) {
            LogicSystem.LInstruction build = lStatement.build(asm);
            if(build==null)continue;
            insts.add(build);
        }
        asm.instructions =insts.toArray(LogicSystem.LInstruction.class);
        return asm;
    }

    public static String write(Array<LStatement> statements){
        StringBuilder out = new StringBuilder();
        for(LStatement s : statements){
            s.write(out);
            out.append("\n");
        }

        return out.toString();
    }

    /** Parses a sequence of statements from a string. */
    public static Array<LStatement> read(String text){
        //don't waste time parsing null/empty text
        if(text == null || text.isEmpty()) return new Array<>();
        return new LParser(text).parse();
    }

    /** @return a variable by name.
     * This may be a constant variable referring to a number or object. */
    public LVar var(String symbol){
        LVar constVar = Vars.logicVars.get(symbol, privileged);
        if(constVar != null) return constVar;

        symbol = symbol.trim();

        //string case
        if(!symbol.isEmpty() && symbol.charAt(0) == '\"' && symbol.charAt(symbol.length() - 1) == '\"'){
            throw new RuntimeException("Strings are not supported yet.");
//            return putConst("___" + symbol, symbol.substring(1, symbol.length() - 1).replace("\\n", "\n"));
        }

        //remove spaces for non-strings
        symbol = symbol.replace(' ', '_');

        double value = parseDouble(symbol);

        if(value == invalidNum){
            return putVar(symbol);
        }else{
            //this creates a hidden const variable with the specified value
            return putConst("___" + value, value);
        }
    }

    double parseDouble(String symbol){
        //parse hex/binary syntax
        if(symbol.startsWith("0b")) return Utils.Strings.parseLong(symbol, 2, 2, symbol.length(), invalidNum);
        if(symbol.startsWith("0x")) return Utils.Strings.parseLong(symbol, 16, 2, symbol.length(), invalidNum);
        if(symbol.startsWith("%") && (symbol.length() == 7 || symbol.length() == 9)) return parseColor(symbol);

        return Utils.Strings.parseDouble(symbol, invalidNum);
    }

    double parseColor(String symbol){
        int
        r = Utils.Strings.parseInt(symbol, 16, 0, 1, 3),
        g = Utils.Strings.parseInt(symbol, 16, 0, 3, 5),
        b = Utils.Strings.parseInt(symbol, 16, 0, 5, 7),
        a = symbol.length() == 9 ? Utils.Strings.parseInt(symbol, 16, 0, 7, 9) : 255;

        return Utils.Color.toDoubleBits(r, g, b, a);
    }

    /** Adds a constant value by name. */
    public LVar putConst(String name, double value){
        LVar var = putVar(name);
            var.setNumVal(value);
        var.constant = true;
        return var;
    }

    /** Registers a variable name mapping. */
    public LVar putVar(String name){
        if(vars.containsKey(name)){
            return vars.get(name);
        }else{
            //variables are null objects by default
            LVar var = new LVar(name);
            vars.put(name, var);
            return var;
        }
    }

    public LVar getVar(String name){
        return vars.get(name);
    }

}
