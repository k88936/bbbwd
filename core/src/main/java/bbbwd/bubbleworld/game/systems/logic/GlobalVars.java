package bbbwd.bubbleworld.game.systems.logic;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.Random;
import java.util.logging.Logger;

import static bbbwd.bubbleworld.Vars.state;


/** Stores global logic variables for logic processors. */
public class GlobalVars {
    public static final int ctrlProcessor = 1, ctrlPlayer = 2, ctrlCommand = 3;
//    public static final ContentType[] lookableContent = {ContentType.block, ContentType.unit, ContentType.item, ContentType.liquid};
    /** Global random state. */
    public static final Random rand = new Random();
    public static final Array<String> soundNames = new Array<>();
    //non-constants that depend on state
    private static LVar varTime, varTick, varSecond, varMinute, varWave, varWaveTime, varMapW, varMapH, varWait, varServer, varClient, varClientLocale, varClientUnit, varClientName, varClientTeam, varClientMobile;
    private ObjectMap<String, LVar> vars = new ObjectMap<>();
    private Array<VarEntry> varEntries = new Array<>();
    private ObjectSet<String> privilegedNames = new ObjectSet<>();
    //    private UnlockableContent[][] logicIdToContent;
    private int[][] contentIdToLogicId;

    public void init() {
        putEntryOnly("sectionProcessor");

        putEntryOnly("@this");
        putEntryOnly("@thisx");
        putEntryOnly("@thisy");
        putEntryOnly("@links");
        putEntryOnly("@ipt");

        putEntryOnly("sectionGeneral");

        put("the end", 0, false, true);
        //add default constants
        putEntry("false", 0);
        putEntry("true", 1);
        put("null", 0, false, true);

        //math
        putEntry("@pi", MathUtils.PI);
        put("π", MathUtils.PI, false, true); //for the "cool" kids
        putEntry("@e", MathUtils.E);
        putEntry("@degToRad", MathUtils.degRad);
        putEntry("@radToDeg", MathUtils.radDeg);

        putEntryOnly("sectionMap");

        //time
        varTime = putEntry("@time", 0);
        varTick = putEntry("@tick", 0);
        varSecond = putEntry("@second", 0);
        varMinute = putEntry("@minute", 0);
        varWave = putEntry("@waveNumber", 0);
        varWaveTime = putEntry("@waveTime", 0);

        varMapW = putEntry("@mapw", 0);
        varMapH = putEntry("@maph", 0);
        varWait = putEntry("@wait", 0);

        putEntryOnly("sectionNetwork");

        varServer = putEntry("@server", 0, true);
        varClient = putEntry("@client", 0, true);

        //privileged desynced client variables
        varClientLocale = putEntry("@clientLocale", 0, true);
        varClientUnit = putEntry("@clientUnit", 0, true);
        varClientName = putEntry("@clientName", 0, true);
        varClientTeam = putEntry("@clientTeam", 0, true);
        varClientMobile = putEntry("@clientMobile", 0, true);

        //special enums
        put("@ctrlProcessor", ctrlProcessor);
        put("@ctrlPlayer", ctrlPlayer);
        put("@ctrlCommand", ctrlCommand);

        //sounds
//        if(Core.assets != null){
//            for(Sound sound : Core.assets.getAll(Sound.class, new Seq<>(Sound.class))){
//                if(sound != Sounds.none && sound != Sounds.swish && sound.file != null){
//                    String name = sound.file.nameWithoutExtension();
//                    soundNames.add(name);
//                    put("@sfx-" + name, Sounds.getSoundId(sound));
//                }
//            }
//        }

        //store base content

//        for(Team team : Team.baseTeams){
//            put("@" + team.name, team);
//        }
//
//        for(Item item : Vars.content.items()){
//            put("@" + item.name, item);
//        }
//
//        for(Liquid liquid : Vars.content.liquids()){
//            put("@" + liquid.name, liquid);
//        }
//
//        for(Block block : Vars.content.blocks()){
//            //only register blocks that have no item equivalent (this skips sand)
//            if(content.item(block.name) == null & !(block instanceof LegacyBlock)){
//                put("@" + block.name, block);
//            }
//        }
//
//        for(var entry : Colors.getColors().entries()){
//            //ignore uppercase variants, they are duplicates
//            if(Character.isUpperCase(entry.key.charAt(0))) continue;
//
//            put("@color" + Strings.capitalize(entry.key), entry.value.toDoubleBits());
//        }
//
//        for(UnitType type : Vars.content.units()){
//            if(!type.internal){
//                put("@" + type.name, type);
//            }
//        }
//
//        for(Weather weather : Vars.content.weathers()){
//            put("@" + weather.name, weather);
//        }

//        //store sensor constants
//        for(LAccess sensor : LAccess.all){
//            put("@" + sensor.name(), sensor);
//        }

//        logicIdToContent = new UnlockableContent[ContentType.all.length][];
//        contentIdToLogicId = new int[ContentType.all.length][];
//
//        putEntryOnly("sectionLookup");
//
//        Fi ids = Core.files.internal("logicids.dat");
//        if(ids.exists()){
//            //read logic ID mapping data (generated in ImagePacker)
//            try(DataInputStream in = new DataInputStream(ids.readByteStream())){
//                for(ContentType ctype : lookableContent){
//                    short amount = in.readShort();
//                    logicIdToContent[ctype.ordinal()] = new UnlockableContent[amount];
//                    contentIdToLogicId[ctype.ordinal()] = new int[Vars.content.getBy(ctype).size];
//
//                    //store count constants
//                    putEntry("@" + ctype.name() + "Count", amount);
//
//                    for(int i = 0; i < amount; i++){
//                        String name = in.readUTF();
//                        UnlockableContent fetched = Vars.content.getByName(ctype, name);
//
//                        if(fetched != null){
//                            logicIdToContent[ctype.ordinal()][i] = fetched;
//                            contentIdToLogicId[ctype.ordinal()][fetched.id] = i;
//                        }
//                    }
//                }
//            }catch(IOException e){
//                //don't crash?
//                Log.err("Error reading logic ID mapping", e);
//            }
    }


    /** Updates global time and other state variables. */
    public void update() {
        //set up time; note that @time is now only updated once every invocation and directly based off of @tick.
        //having time be based off of user system time was a very bad idea.
        varTime.setNumVal(state.tick / 60.0 * 1000.0);
        varTick.setNumVal(state.tick);

        //shorthands for seconds/minutes spent in save
        varSecond.setNumVal(state.tick / 60f);
        varMinute.setNumVal(state.tick / 60f / 60f);

//        //wave state
//        varWave.numval = state.wave;
//        varWaveTime.numval = state.wavetime / 60f;
//
//        varMapW.numval = world.width();
//        varMapH.numval = world.height();

//        //network
//        varServer.numval = (net.server() || !net.active()) ? 1 : 0;
//        varClient.numval = net.client() ? 1 : 0;
//
//        //client
//        if (player != null) {
//            varClientLocale.objval = player.locale();
//            varClientUnit.objval = player.unit();
//            varClientName.objval = player.name();
//            varClientTeam.numval = player.team().id;
//            varClientMobile.numval = mobile ? 1 : 0;
//        }
    }

    public LVar waitVar() {
        return varWait;
    }

    public Array<VarEntry> getEntries() {
        return varEntries;
    }

//    /** @return a piece of content based on its logic ID. This is not equivalent to content ID. */
//    public @Nullable Content lookupContent(ContentType type, int id){
//        var arr = logicIdToContent[type.ordinal()];
//        return arr != null && id >= 0 && id < arr.length ? arr[id] : null;
//    }
//
//    /** @return the integer logic ID of content, or -1 if invalid. */
//    public int lookupLogicId(UnlockableContent content){
//        var arr = contentIdToLogicId[content.getContentType().ordinal()];
//        return arr != null && content.id >= 0 && content.id < arr.length ? arr[content.id] : -1;
//    }

    /**
     * @return a constant variable if there is a constant with this name, otherwise null.
     * Attempt to get privileged variable from non-privileged logic executor returns null constant.
     */
    public LVar get(String name) {
        return vars.get(name);
    }

    /**
     * @return a constant variable by name
     * Attempt to get privileged variable from non-privileged logic executor returns null constant.
     */
    public LVar get(String name, boolean privileged) {
        if (!privileged && privilegedNames.contains(name)) return vars.get("null");
        return vars.get(name);
    }

    /** Sets a global variable by name. */
    public void set(String name, double value) {
        get(name, true).setNumVal(value);
    }

    /** Adds a constant value by name. */
    public LVar put(String name, double value, boolean privileged) {
        return put(name, value, privileged, true);
    }

    /** Adds a constant value by name. */
    public LVar put(String name, double value, boolean privileged, boolean hidden) {
        LVar existingVar = vars.get(name);
        if (existingVar != null) { //don't overwrite existing vars (see #6910)
            Logger.getGlobal().severe("Failed to add global logic variable '@', as it already exists." + name);
            return existingVar;
        }

        LVar var = new LVar(name);
        var.constant = true;
            var.setNumVal(value);

        vars.put(name, var);
        if (privileged) privilegedNames.add(name);

        if (!hidden) {
            varEntries.add(new VarEntry(name, "", "", privileged));
        }
        return var;
    }

    public LVar put(String name, double value) {
        return put(name, value, false);
    }

    public LVar putEntry(String name, double value) {
        return put(name, value, false, false);
    }

    public LVar putEntry(String name, double value, boolean privileged) {
        return put(name, value, privileged, false);
    }

    public void putEntryOnly(String name) {
        varEntries.add(new VarEntry(name, "", "", false));
    }

    /** An entry that describes a variable for documentation purposes. This is *only* used inside UI for global variables. */
    public static class VarEntry {
        public String name, description, icon;
        public boolean privileged;

        public VarEntry(String name, String description, String icon, boolean privileged) {
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.privileged = privileged;
        }

        public VarEntry() {
        }
    }
}
