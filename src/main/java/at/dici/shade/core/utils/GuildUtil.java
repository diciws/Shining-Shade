package at.dici.shade.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class GuildUtil {
    private long id;
    private String date;
    private int privilegeLevel; // 0 = Guild cant do shit, 100 = random Public Guild, 200 = Guild with bonus access, 300 = Feature-Testing Guild, 400 = Our Guild, 500 = Debug-Guild
    // makes it easier to test on command like if(privilege >= SPECIAL_GUILD) instead of using String

    private long phasmoGuessrChannelId;
    private long botchannelid;
    private int guildflag;

    // New Levels can be added at any time
    public static class PrivLevel{
        public final static int NONE = 0;
        public final static int DEFAULT = 100;
        public final static int BONUS = 200;
        public final static int TESTERS = 300;
        public final static int PHASMOLEGENDS = 400;
        public final static int DEBUG = 500;
    }

}
