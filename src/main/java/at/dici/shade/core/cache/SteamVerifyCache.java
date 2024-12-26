package at.dici.shade.core.cache;

import at.dici.shade.core.utils.SteamVerifyUtil;
import at.dici.shade.core.utils.UserUtil;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Getter
public class SteamVerifyCache {
    @Getter
    private static SteamVerifyCache instance;

    private final AsyncMySqlCache<String, SteamVerifyUtil> steamverifyCache = new AsyncMySqlCache<String, SteamVerifyUtil>() {
        @Override
        protected String query( ) {
            return "SELECT * FROM steam_verify;";
        }

        @Override
        protected Function<ResultSet, Map<String, SteamVerifyUtil>> function( ) {
            return input -> {
                Map<String, SteamVerifyUtil> map = Maps.newLinkedHashMap();

                try {
                    assert input != null;
                    while ( input.next() ) {
                        int id = input.getInt("id" );
                        String steamname = input.getString("steamname" );
                        String steamid = input.getString("steamid" );
                        String realsteamname = input.getString("realsteamname" );
                        String phasmo_game = input.getString("phasmo_game" );
                        int phasmo_hours_min = input.getInt("phasmo_hours_min" );
                        String discordid = input.getString("discordid" );
                        String verify_code = input.getString("verify_code" );

                        map.put( discordid, new SteamVerifyUtil( id, steamname, steamid, realsteamname, phasmo_game, phasmo_hours_min, discordid, verify_code) );
                    }
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
                return map;
            };
        }
    };
    public SteamVerifyCache( ) {
        SteamVerifyCache.instance = this;
    }

    public long CacheSize( ){
        return this.getSteamverifyCache().size();
    }

    public static void autoreload(){
        Logger.log(LogLevel.WARNING, "Accessing Steam-Verify-Cache: "+instance.steamverifyCache.size());
        instance.steamverifyCache.invalidateAll();
        new SteamVerifyCache();
        Logger.log(LogLevel.INFORMATION, "SteamVerifyCache reloaded.");
    }

    public boolean isExisting( String id ) {
        SteamVerifyUtil user = this.getSteamverifyCache().getIfPresent( id );
        return  user != null;
    }

    public static String getPhasmoGame(String id){
        return instance.getSteamverifyCache().get(id).getPhasmo_game();
    }

    public static int getPhasmoGameMin(String id){
        return instance.getSteamverifyCache().get(id).getPhasmo_hours_min();
    }

}
