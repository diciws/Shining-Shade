package at.dici.shade.core.cache;

import at.dici.shade.core.utils.GuildUtil;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Getter
public class GuildCache {
    @Getter
    private static GuildCache instance;

    private final AsyncMySqlCache<Long, GuildUtil> guildCache = new AsyncMySqlCache<Long, GuildUtil>() {
        @Override
        protected String query() {
            return "SELECT * FROM guilds";
        }

        @Override
        protected Function<ResultSet, Map<Long, GuildUtil>> function() {
            return input -> {
                Map<Long, GuildUtil> map = Maps.newLinkedHashMap();

                try {
                    assert input != null;
                    while ( input.next() ) {
                        long id = input.getLong("id");
                        String date = input.getString("date");
                        int privilege = input.getInt("privilegelevel");
                        long phasmoGuessrChannelId = input.getLong("phasmoguessrchannelid");
                        long botchannelid = input.getLong("botchannelid");
                        int guildflag = input.getInt("guildflag");
                        map.put(id,new GuildUtil(id, date , privilege , phasmoGuessrChannelId , botchannelid , guildflag));
                    }
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }

                return map;
            };
        }
    };

    public GuildCache(){
        GuildCache.instance = this;
    }

    public boolean isGuild( long id ) {
        GuildUtil guild = this.getGuildCache().getIfPresent(id);
        return  guild != null;
    }

    public void removeGuild( long id ) {
        this.getGuildCache().invalidate( id );
    }

    public void cacheGuild(GuildUtil id) {
        this.getGuildCache().put( id.getId(), id);
    }

    /*
        Methods for easy data reception
    */

    public static String getJoinDate(long guild ) {
        return instance.getGuildCache().get( guild ).getDate();
    }

    public static int getPrivilegeLevel(long guild ) {
        return instance.getGuildCache().get( guild ).getPrivilegeLevel();
    }

    public static long getPhasmoGuessrChannelId(long guild ){
        return instance.getGuildCache().get( guild ).getPhasmoGuessrChannelId();
    }

    public static long getBotChannelId(long guild ){
        return instance.getGuildCache().get( guild ).getBotchannelid();
    }

    public static int getFlags(long guild){
        return instance.getGuildCache().get(guild).getGuildflag();
    }

}
