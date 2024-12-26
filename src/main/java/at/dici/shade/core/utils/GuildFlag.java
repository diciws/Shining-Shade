package at.dici.shade.core.utils;

public enum GuildFlag {

    // Aux
    EMPTY(0),

    // Setup
    BOT_CHANNEL_SET(1),

    // GEA-Flags
    GEA(1 << 10),
    GEA_DM(1 << 11),
    GEA_CHANNELS(1 << 12),

    // PhasmoGuessr-Flags
    PHASMO_GUESSR(1 << 13),
    PHASMO_GUESSR_PRIVATE_GAMES(1 << 14),

    // Command-Flags
    SMUDGETIMER(1 << 15),
    PROFILE(1 << 16),
    MAPDICE(1<<17),

    INVENTORY(1<<18);

    private final int flag;

    GuildFlag(int flag){
        this.flag = flag;
    }

    public int getValue(){
        return flag;
    }

}
