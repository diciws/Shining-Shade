package at.dici.shade.core.handler;

import at.dici.shade.core.cache.SteamVerifyCache;
import at.dici.shade.core.mysql.MySqlClass;

import java.util.function.Consumer;

public class SteamVerifyHandler {

    public static boolean setdiscordid( String discordid,String code, Consumer<Boolean> consumer ) {

        MySqlClass.getInstance().updateAsync("UPDATE steam_verify SET discordid = ? WHERE verify_code = ?;",
                success -> {
                    if (success)
                        SteamVerifyCache.autoreload();
                    consumer.accept( success );
                }, discordid, code);

        return true;
    }

}
