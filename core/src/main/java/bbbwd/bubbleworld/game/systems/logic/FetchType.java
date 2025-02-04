package bbbwd.bubbleworld.game.systems.logic;

public enum FetchType{
    unit,
    unitCount,
    player,
    playerCount,
    core,
    coreCount,
    build,
    buildCount;

    public static final FetchType[] all = values();
}
