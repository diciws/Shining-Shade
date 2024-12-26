package at.dici.shade.core.handler;

import at.dici.shade.core.cache.GuildCache;
import at.dici.shade.core.mysql.MySqlClass;
import at.dici.shade.core.utils.GuildUtil;

import java.util.function.Consumer;

public class GuildHandler {
    public static boolean guildAsync( long id, Consumer<Boolean> consumer ) {
        if ( GuildCache.getInstance().isGuild( id ) )
            return false;

            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datum = sdf.format(dt);

            int privilegelevel = 100;
            long phasmoguessrchannelid = 0;
            long phasmoquizchannelid = 0;
            long botchannelid = 0;

            MySqlClass.getInstance().updateAsync("INSERT INTO guilds (id, date, privilegelevel, phasmoguessrchannelid, phasmoquizchannelid, botchannelid, guildflag) VALUES (?, ?, ?, ?, ?, ?, ?);",
                    success -> {
                        if (success)
                            GuildCache.getInstance().cacheGuild(new GuildUtil(id, datum, privilegelevel, phasmoguessrchannelid, botchannelid, 0));
                        consumer.accept(success);
                    }, id, datum, privilegelevel, phasmoguessrchannelid, phasmoquizchannelid, botchannelid, 0);

            return true;

    }

    public static boolean setFlags( long guildid, int guildflags, Consumer<Boolean> consumer ) {

        MySqlClass.getInstance().updateAsync("UPDATE guilds SET guildflag = ? WHERE id = ?;",
                success -> {
                    if (success)
                        GuildCache.getInstance().cacheGuild( new GuildUtil(guildid, GuildCache.getJoinDate(guildid),
                                GuildCache.getPrivilegeLevel(guildid), GuildCache.getPhasmoGuessrChannelId(guildid), GuildCache.getBotChannelId(guildid), guildflags) );
                    consumer.accept( success );
                }, guildid, guildflags);

        return true;
    }

    public static boolean setbotchannel( long guildid, long botchannelid, Consumer<Boolean> consumer ) {

        MySqlClass.getInstance().updateAsync("UPDATE guilds SET botchannelid = ? WHERE id = ?;",
                success -> {
                    if (success)
                        GuildCache.getInstance().cacheGuild( new GuildUtil(guildid, GuildCache.getJoinDate(guildid),
                                GuildCache.getPrivilegeLevel(guildid), GuildCache.getPhasmoGuessrChannelId(guildid), botchannelid,  GuildCache.getFlags(guildid)) );
                    consumer.accept( success );
                }, guildid, botchannelid);

        return true;
    }

    public static boolean setphasmoguessrchannel( long guildid, long phasmoguessrchannel, Consumer<Boolean> consumer ) {

        MySqlClass.getInstance().updateAsync("UPDATE guilds SET phasmoguessrchannelid = ? WHERE id = ?;",
                success -> {
                    if (success)
                        GuildCache.getInstance().cacheGuild( new GuildUtil(guildid, GuildCache.getJoinDate(guildid),
                                GuildCache.getPrivilegeLevel(guildid), phasmoguessrchannel, GuildCache.getBotChannelId(guildid),  GuildCache.getFlags(guildid)) );
                    consumer.accept( success );
                }, phasmoguessrchannel, guildid);

        return true;
    }

    public static boolean setphasmoquizchannel( long guildid, long setphasmoquizchannel, Consumer<Boolean> consumer ) {

        MySqlClass.getInstance().updateAsync("UPDATE guilds SET phasmoquizchannelid = ? WHERE id = ?;",
                success -> {
                    if (success)
                        GuildCache.getInstance().cacheGuild( new GuildUtil(guildid, GuildCache.getJoinDate(guildid),
                                GuildCache.getPrivilegeLevel(guildid), GuildCache.getPhasmoGuessrChannelId(guildid), setphasmoquizchannel,  GuildCache.getFlags(guildid)) );
                    consumer.accept( success );
                }, setphasmoquizchannel, guildid);

        return true;
    }

    public static boolean removeGuild( long guildid, Consumer<Boolean> consumer ) {

        MySqlClass.getInstance().updateAsync("DELETE from guilds WHERE id = ?;",
                success -> {
                    if (success)
                        GuildCache.getInstance().removeGuild(guildid);
                    consumer.accept( success );
                }, guildid);

        return true;
    }

}