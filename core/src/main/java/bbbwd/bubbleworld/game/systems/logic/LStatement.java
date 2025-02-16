package bbbwd.bubbleworld.game.systems.logic;


import bbbwd.bubbleworld.utils.Utils;
import com.badlogic.gdx.utils.Array;

/**
 * A statement is an intermediate representation of an instruction, to be used mostly in UI.
 * Contains all relevant variable information. */
public abstract class LStatement{


    public abstract LExecutor.LInstruction build(LAssembler builder);


    public LStatement copy(){
        StringBuilder build = new StringBuilder();
        write(build);
        Array<LStatement> read = LAssembler.read(build.toString());
        return read.size == 0 ? null : read.first();
    }

    public boolean hidden(){
        return false;
    }

    //protected methods are only for internal UI layout utilities

    protected String sanitize(String value){
        if(value.isEmpty()){
            return "";
        }else if(value.length() == 1){
            if(value.charAt(0) == '"' || value.charAt(0) == ';' || value.charAt(0) == ' '){
                return "invalid";
            }
        }else{
            StringBuilder res = new StringBuilder(value.length());
            if(value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"'){
                res.append('\"');
                //strip out extra quotes
                for(int i = 1; i < value.length() - 1; i++){
                    if(value.charAt(i) == '"'){
                        res.append('\'');
                    }else{
                        res.append(value.charAt(i));
                    }
                }
                res.append('\"');
            }else{
                //otherwise, strip out semicolons, spaces and quotes
                for(int i = 0; i < value.length(); i++){
                    char c = value.charAt(i);
                    res.append(switch(c){
                        case ';' -> 's';
                        case '"' -> '\'';
                        case ' ' -> '_';
                        default -> c;
                    });
                }
            }

            return res.toString();
        }

        return value;
    }







    public void afterRead(){}

    public void write(StringBuilder builder){
        LogicIO.write(this, builder);
    }



    public String name(){
        return Utils.Strings.insertSpaces(getClass().getSimpleName().replace("Statement", ""));
    }

}
