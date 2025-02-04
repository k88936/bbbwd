package bbbwd.bubbleworld.game.systems.logic;

import com.badlogic.gdx.graphics.Color;

public class LStatements {

    //TODO broken
    //@RegisterStatement("#")
    public static class CommentStatement extends LStatement {
        public String comment = "";


        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return null;
        }
    }

    //    @RegisterStatement("noop")
    public static class InvalidStatement extends LStatement {


        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.NoopI();
        }
    }

    //    @RegisterStatement("read")
    public static class ReadStatement extends LStatement {
        public String output = "result", target = "cell1", address = "0";


        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.ReadI(builder.var(target), builder.var(address), builder.var(output));
        }


    }

    //    @RegisterStatement("write")
    public static class WriteStatement extends LStatement {
        public String input = "result", target = "cell1", address = "0";

        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.WriteI(builder.var(target), builder.var(address), builder.var(input));
        }


    }
//
//    //    @RegisterStatement("draw")
//    public static class DrawStatement extends LStatement {
//        static final String[] aligns = {"center", "top", "bottom", "left", "right", "topLeft", "topRight", "bottomLeft", "bottomRight"};
//        //yes, boxing Integer is gross but this is easier to construct and Integers <128 don't allocate anyway
//        static final ObjectMap<String, Integer> nameToAlign = Map.of(
//                "center", Align.center,
//                "top", Align.top,
//                "bottom", Align.bottom,
//                "left", Align.left,
//                "right", Align.right,
//                "topLeft", Align.topLeft,
//                "topRight", Align.topRight,
//                "bottomLeft", Align.bottomLeft,
//                "bottomRight", Align.bottomRight
//        );
//
//        //        public GraphicsType type = GraphicsType.clear;
//        public String x = "0", y = "0", p1 = "0", p2 = "0", p3 = "0", p4 = "0";
//
//
//        @Override
//        public void afterRead() {
//            //0 constant alpha for colors is not allowed

    ////            if(type == GraphicsType.color && p2.equals("0")){
    ////                p2 = "255";
    ////            }
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new DrawI((byte) type.ordinal(), builder.var(x), builder.var(y),
//                    type == GraphicsType.print ? new LVar(p1, nameToAlign.get(p1, Align.bottomLeft), true) : builder.var(p1), builder.var(p2), builder.var(p3), builder.var(p4));
//            return null;
//        }
//
//
//    }

    //    @RegisterStatement("print")
    public static class PrintStatement extends LStatement {
        public String value = "\"frog\"";


        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.PrintI(builder.var(value));
        }


    }

    //    @RegisterStatement("format")
    public static class FormatStatement extends LStatement {
        public String value = "\"frog\"";


        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.FormatI(builder.var(value));
        }


    }
//
//        //    @RegisterStatement("drawflush")
//        public static class DrawFlushStatement extends LStatement {
//            public String target = "display1";
//
//
//            @Override
//            public LInstruction build(LAssembler builder) {
//
//                return new DrawFlushI(builder.var(target));
//            }
//
//
//        }
//
//        //    @RegisterStatement("printflush")
//        public static class PrintFlushStatement extends LStatement {
//            public String target = "message1";
//
//
//            @Override
//            public LExecutor.LInstruction build(LAssembler builder) {
//                return new LExecutor.PrintFlushI(builder.var(target));
//            }
//
//        }
//
//        //        @RegisterStatement("getlink")
//        public static class GetLinkStatement extends LStatement {
//            public String output = "result", address = "0";
//
//
//            @Override
//            public LExecutor.LInstruction build(LAssembler builder) {
//                return new GetLinkI(builder.var(output), builder.var(address));
//            }
//
//
//        }

//        //        @RegisterStatement("control")
//        public static class ControlStatement extends LStatement {
//            public LAccess type = LAccess.enabled;
//            public String target = "block1", p1 = "0", p2 = "0", p3 = "0", p4 = "0";
//
//
//            @Override
//            public LExecutor.LInstruction build(LAssembler builder) {
//                return new ControlI(type, builder.var(target), builder.var(p1), builder.var(p2), builder.var(p3), builder.var(p4));
//            }
//
//
//        }
//
//        //        @RegisterStatement("radar")
//        public static class RadarStatement extends LStatement {
//            public RadarTarget target1 = RadarTarget.enemy, target2 = RadarTarget.any, target3 = RadarTarget.any;
//            public RadarSort sort = RadarSort.distance;
//            public String radar = "turret1", sortOrder = "1", output = "result";
//
//
//            @Override
//            public LExecutor.LInstruction build(LAssembler builder) {
//                return new RadarI(target1, target2, target3, sort, builder.var(radar), builder.var(sortOrder), builder.var(output));
//            }
//        }
//
//    }

//    //        @RegisterStatement("sensor")
//    public static class SensorStatement extends LStatement {
//        public String to = "result";
//        public String from = "block1", type = "@copper";
//
//        private transient int selected = 0;
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SenseI(builder.var(from), builder.var(to), builder.var(type));
//        }
//
//    }

    //    @RegisterStatement("set")
    public static class SetStatement extends LStatement {
        public String to = "result";
        public String from = "0";

        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.SetI(builder.var(from), builder.var(to));
        }


    }

    //    @RegisterStatement("op")
    public static class OperationStatement extends LStatement {
        public LogicOp op = LogicOp.add;
        public String dest = "result", a = "a", b = "b";


        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.OpI(op, builder.var(a), builder.var(b), builder.var(dest));
        }


    }

    //    @RegisterStatement("wait")
    public static class WaitStatement extends LStatement {
        public String value = "0.5";


        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.WaitI(builder.var(value));
        }


    }

    //    @RegisterStatement("stop")
    public static class StopStatement extends LStatement {


        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.StopI();
        }


    }

//    //    @RegisterStatement("lookup")
//    public static class LookupStatement extends LStatement {
//        //        public ContentType type = ContentType.item;
//        public String result = "result", id = "0";
//
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new LookupI(builder.var(result), builder.var(id), type);
//        }
//
//
//    }

//    //    @RegisterStatement("packcolor")
//    public static class PackColorStatement extends LStatement {
//        public String result = "result", r = "1", g = "0", b = "0", a = "1";
//
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new PackColorI(builder.var(result), builder.var(r), builder.var(g), builder.var(b), builder.var(a));
//        }
//
//    }

    //    @RegisterStatement("end")
    public static class EndStatement extends LStatement {


        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.EndI();
        }


    }

    //    @RegisterStatement("jump")
    public static class JumpStatement extends LStatement {
        private static Color last = new Color();


        public int destIndex;

        public ConditionOp op = ConditionOp.notEqual;
        public String value = "x", compare = "false";


        @Override
        public LExecutor.LInstruction build(LAssembler builder) {
            return new LExecutor.JumpI(op, builder.var(value), builder.var(compare), destIndex);
        }

    }

//    //    @RegisterStatement("ubind")
//    public static class UnitBindStatement extends LStatement {
//        public String type = "@poly";
//
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new UnitBindI(builder.var(type));
//        }
//
//
//    }
//
//    //    @RegisterStatement("ucontrol")
//    public static class UnitControlStatement extends LStatement {
//        public LUnitControl type = LUnitControl.move;
//        public String p1 = "0", p2 = "0", p3 = "0", p4 = "0", p5 = "0";
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new UnitControlI(type, builder.var(p1), builder.var(p2), builder.var(p3), builder.var(p4), builder.var(p5));
//        }
//
//
//    }
//
//    //    @RegisterStatement("uradar")
//    public static class UnitRadarStatement extends FormatStatement.RadarStatement {
//
//        public UnitRadarStatement() {
//            radar = "0";
//        }
//
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new RadarI(target1, target2, target3, sort, builder.var("@unit"), builder.var(sortOrder), builder.var(output));
//        }
//
//    }
//
//    //    @RegisterStatement("ulocate")
//    public static class UnitLocateStatement extends LStatement {
//        public LLocate locate = LLocate.building;
//        //        public BlockFlag flag = BlockFlag.core;
//        public String enemy = "true", ore = "@copper";
//        public String outX = "outx", outY = "outy", outFound = "found", outBuild = "building";
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new UnitLocateI(locate, flag, builder.var(enemy), builder.var(ore), builder.var(outX), builder.var(outY), builder.var(outFound), builder.var(outBuild));
//        }
//
//    }
//
//    //    @RegisterStatement("getblock")
//    public static class GetBlockStatement extends LStatement {
//        //        public TileLayer layer = TileLayer.block;
//        public String result = "result", x = "0", y = "0";
//
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new GetBlockI(builder.var(x), builder.var(y), builder.var(result), layer);
//        }
//
//    }
//
//    //    @RegisterStatement("setblock")
//    public static class SetBlockStatement extends LStatement {
//        //        public TileLayer layer = TileLayer.block;
//        public String block = "@air", x = "0", y = "0", team = "@derelict", rotation = "0";
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SetBlockI(builder.var(x), builder.var(y), builder.var(block), builder.var(team), builder.var(rotation), layer);
//
//        }
//    }
//
//    //    @RegisterStatement("spawn")
//    public static class SpawnUnitStatement extends LStatement {
//        public String type = "@dagger", x = "10", y = "10", rotation = "90", team = "@sharded", result = "result";
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SpawnUnitI(builder.var(type), builder.var(x), builder.var(y), builder.var(rotation), builder.var(team), builder.var(result));
//        }
//
//    }
//
//    //    @RegisterStatement("status")
//    public static class ApplyStatusStatement extends LStatement {
//        private static @Nullable String[] statusNames;
//        public boolean clear;
//        public String effect = "wet", unit = "unit", duration = "10";
//
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new ApplyEffectI(clear, effect, builder.var(unit), builder.var(duration));
//        }
//
//    }
//
//    //    @RegisterStatement("weathersense")
//    public static class WeatherSenseStatement extends LStatement {
//        public String to = "result";
//        public String weather = "@rain";
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SenseWeatherI(builder.var(weather), builder.var(to));
//        }
//
//
//    }

//    //    @RegisterStatement("weatherset")
//    public static class WeatherSetStatement extends LStatement {
//        public String weather = "@rain", state = "true";
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SetWeatherI(builder.var(weather), builder.var(state));
//        }
//
//    }

//    //    @RegisterStatement("spawnwave")
//    public static class SpawnWaveStatement extends LStatement {
//        public String x = "10", y = "10", natural = "false";
//
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SpawnWaveI(builder.var(natural), builder.var(x), builder.var(y));
//        }
//
//    }

//    //    @RegisterStatement("setrule")
//    public static class SetRuleStatement extends LStatement {
//        public LogicRule rule = LogicRule.waveSpacing;
//        public String value = "10", p1 = "0", p2 = "0", p3 = "100", p4 = "100";
//
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SetRuleI(rule, builder.var(value), builder.var(p1), builder.var(p2), builder.var(p3), builder.var(p4));
//        }
//
//
//    }

//    //    @RegisterStatement("message")
//    public static class FlushMessageStatement extends LStatement {
//        public MessageType type = MessageType.announce;
//        public String duration = "3", outSuccess = "@wait";
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new FlushMessageI(type, builder.var(duration), builder.var(outSuccess));
//        }
//
//
//    }
//
//    //    @RegisterStatement("cutscene")
//    public static class CutsceneStatement extends LStatement {
//        public CutsceneAction action = CutsceneAction.pan;
//        public String p1 = "100", p2 = "100", p3 = "0.06", p4 = "0";
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LInstruction build(LAssembler builder) {
//            return new CutsceneI(action, builder.var(p1), builder.var(p2), builder.var(p3), builder.var(p4));
//        }
//
//    }
//
//    //    @RegisterStatement("effect")
//    public static class EffectStatement extends LStatement {
//        public String type = "warn", x = "0", y = "0", sizerot = "2", color = "%ffaaff", data = "";
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler b) {
//            return new EffectI(LogicFx.get(type), b.var(x), b.var(y), b.var(sizerot), b.var(color), b.var(data));
//        }
//
//    }
//
//    //    @RegisterStatement("explosion")
//    public static class ExplosionStatement extends LStatement {
//        public String team = "@crux", x = "0", y = "0", radius = "5", damage = "50", air = "true", ground = "true", pierce = "false", effect = "true";
//
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler b) {
//            return new ExplosionI(b.var(team), b.var(x), b.var(y), b.var(radius), b.var(damage), b.var(air), b.var(ground), b.var(pierce), b.var(effect));
//        }
//
//
//    }
//
//    //    @RegisterStatement("setrate")
//    public static class SetRateStatement extends LStatement {
//        public String amount = "10";
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SetRateI(builder.var(amount));
//        }
//
//    }

//    //    @RegisterStatement("fetch")
//    public static class FetchStatement extends LStatement {
//        public FetchType type = FetchType.unit;
//        public String result = "result", team = "@sharded", index = "0", extra = "@conveyor";
//
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new FetchI(type, builder.var(result), builder.var(team), builder.var(extra), builder.var(index));
//        }
//
//
//    }
//
//    //TODO: test this first
////    @RegisterStatement("sync")
//    public static class SyncStatement extends LStatement {
//        public String variable = "var";
//
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SyncI(builder.var(variable));
//        }
//
//
//    }

////    @RegisterStatement("clientdata")
//    public static class ClientDataStatement extends LStatement {
//        public String channel = "\"frog\"", value = "\"bar\"", reliable = "0";
//
//
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            if (!state.rules.allowLogicData) return null;
//            return new ClientDataI(builder.var(channel), builder.var(value), builder.var(reliable));
//        }
//
//
//    }
//
////    @RegisterStatement("getflag")
//    public static class GetFlagStatement extends LStatement {
//        public String result = "result", flag = "\"flag\"";
//
//
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new GetFlagI(builder.var(result), builder.var(flag));
//        }
//
//    }
//
//    @RegisterStatement("setflag")
//    public static class SetFlagStatement extends LStatement {
//        public String flag = "\"flag\"", value = "true";

//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SetFlagI(builder.var(flag), builder.var(value));
//        }
//
//    }

////    @RegisterStatement("setprop")
//    public static class SetPropStatement extends LStatement {
//        public String type = "@copper", of = "block1", value = "0";
//
//        private transient int selected = 0;
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SetPropI(builder.var(type), builder.var(of), builder.var(value));
//        }
//
//    }

////    @RegisterStatement("playsound")
//    public static class PlaySoundStatement extends LStatement {
//        public boolean positional;
//        public String id = "@sfx-pew", volume = "1", pitch = "1", pan = "0", x = "@thisx", y = "@thisy", limit = "true";
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new PlaySoundI(positional, builder.var(id), builder.var(volume), builder.var(pitch), builder.var(pan), builder.var(x), builder.var(y), builder.var(limit));
//        }
//
//    }
//
////    @RegisterStatement("setmarker")
//    public static class SetMarkerStatement extends LStatement {
//        public LMarkerControl type = LMarkerControl.pos;
//        public String id = "0", p1 = "0", p2 = "0", p3 = "0";
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new SetMarkerI(type, builder.var(id), builder.var(p1), builder.var(p2), builder.var(p3));
//        }
//
//    }
//
////    @RegisterStatement("makemarker")
//    public static class MakeMarkerStatement extends LStatement {
//        public String type = "shape", id = "0", x = "0", y = "0", replace = "true";
//
//
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new MakeMarkerI(type, builder.var(id), builder.var(x), builder.var(y), builder.var(replace));
//        }
//
//    }

////    @RegisterStatement("localeprint")
//    public static class LocalePrintStatement extends LStatement {
//        public String value = "\"name\"";
//
//        @Override
//        public boolean privileged() {
//            return true;
//        }
//
//        @Override
//        public LExecutor.LInstruction build(LAssembler builder) {
//            return new LocalePrintI(builder.var(value));
//        }
//
//
//    }

}
