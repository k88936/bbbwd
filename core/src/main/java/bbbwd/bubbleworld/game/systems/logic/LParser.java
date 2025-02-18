package bbbwd.bubbleworld.game.systems.logic;


import bbbwd.bubbleworld.utils.Utils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;

import java.util.Map;

public class LParser {
    private static final String[] tokens = new String[16];
    private static final int maxJumps = 500;
    private static final Map<String, String> opNameChanges = Map.of(
        "atan2", "angle",
        "dst", "len"
    );

    private static final Array<JumpIndex> jumps = new Array<>();
    private static final ObjectIntMap<String> jumpLocations = new ObjectIntMap<>();

    Array<LStatement> statements = new Array<>();
    char[] chars;
    int pos, line, tok;

    LParser(String text) {
        this.chars = text.toCharArray();
    }

    void comment() {
        //read until \n or eof
        while (pos < chars.length && chars[pos++] != '\n') ;
    }

    void error(String message) {
        throw new RuntimeException("Invalid code. " + message);
    }

    String string() {
        int from = pos;

        while (++pos < chars.length) {
            var c = chars[pos];
            if (c == '\n') {
                error("Missing closing quote \" before end of line.");
            } else if (c == '"') {
                break;
            }
        }

        if (pos >= chars.length || chars[pos] != '"') error("Missing closing quote \" before end of file.");

        return new String(chars, from, ++pos - from);
    }

    String token() {
        int from = pos;

        while (pos < chars.length) {
            char c = chars[pos];
            if (c == '\n' || c == ' ' || c == '#' || c == '\t' || c == ';') break;
            pos++;
        }

        return new String(chars, from, pos - from);
    }

    /** Apply changes after reading a list of tokens. */
    void checkRead() {
        if (tokens[0].equals("op")) {
            //legacy name change
            tokens[1] = opNameChanges.getOrDefault(tokens[1], tokens[1]);
        }
    }

    /** Reads the next statement until EOL/EOF. */
    void statement() {
        boolean expectNext = false;
        tok = 0;

        while (pos < chars.length) {
            char c = chars[pos];
            if (tok >= tokens.length) error("Line too long; may only contain " + tokens.length + " tokens");

            //reached end of line, bail out.
            if (c == '\n' || c == ';') break;

            if (expectNext && c != ' ' && c != '#' && c != '\t') {
                error("Expected space after string/token.");
            }

            expectNext = false;

            if (c == '#') {
                comment();
                break;
            } else if (c == '"') {
                tokens[tok++] = string();
                expectNext = true;
            } else if (c != ' ' && c != '\t') {
                tokens[tok++] = token();
                expectNext = true;
            } else {
                pos++;
            }
        }

        //only process lines with at least 1 token
        if (tok > 0) {
            checkRead();

            //store jump location, always ends with colon
            if (tok == 1 && tokens[0].charAt(tokens[0].length() - 1) == ':') {
                if (jumpLocations.size >= maxJumps) {
                    error("Too many jump locations. Max jumps: " + maxJumps);
                }
                jumpLocations.put(tokens[0].substring(0, tokens[0].length() - 1), line);
            } else {
                boolean wasJump;
                String jumpLoc = null;
                //clean up jump position before parsing
                wasJump = (tokens[0].equals("jump") && tok > 1 && !Utils.Strings.canParseInt(tokens[1]));
                if (wasJump) {
                    jumpLoc = tokens[1];
                    tokens[1] = "-1";
                }

                for (int i = 1; i < tok; i++) {
                    if (tokens[i].equals("@configure")) tokens[i] = "@config";
                    if (tokens[i].equals("configure")) tokens[i] = "config";
                }

                LStatement st;

                try {
                    st = LogicIO.read(tokens, tok);
                } catch (Exception e) {
                    //replace invalid statements
                    st = new LStatements.InvalidStatement();
                }


                //store jumps that use labels
                if (st instanceof LStatements.JumpStatement jump && wasJump) {
                    jumps.add(new JumpIndex(jump, jumpLoc));
                }

                if (st != null) {
                    statements.add(st);
                } else {
                    //attempt parsing using custom parser if a match is found; this is for mods
                    if (LAssembler.customParsers.containsKey(tokens[0])) {
                        statements.add(LAssembler.customParsers.get(tokens[0]).get(tokens));
                    } else {
                        //unparseable statement
                        statements.add(new LStatements.InvalidStatement());
                    }
                }
                line++;
            }
        }
    }

    Array<LStatement> parse() {
        jumps.clear();
        jumpLocations.clear();

        while (pos < chars.length && line < LogicSystem.maxInstructions) {
            switch (chars[pos]) {
                case '\n', ';', ' ' -> pos++; //skip newlines and spaces
                case '\r' -> pos += 2; //skip the newline after the \r
                default -> statement();
            }
        }

        //load destination indices
        for (var i : jumps) {
            if (!jumpLocations.containsKey(i.location)) {
                error("Undefined jump location: \"" + i.location + "\". Make sure the jump label exists and is typed correctly.");
            }
            i.jump.destIndex = jumpLocations.get(i.location, -1);
        }

        return statements;
    }

    static class JumpIndex {
        LStatements.JumpStatement jump;
        String location;

        public JumpIndex(LStatements.JumpStatement jump, String location) {
            this.jump = jump;
            this.location = location;
        }
    }

}
