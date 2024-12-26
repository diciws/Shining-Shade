package at.dici.shade.core.databaseio;

import at.dici.shade.Start;
import at.dici.shade.core.cache.GuildCache;
import at.dici.shade.core.handler.GuildHandler;
import at.dici.shade.core.utils.GuildFlag;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.function.Consumer;

public class GuildIo {
    public static long getPhasmoGuessrChannelId(Guild guild){
        return GuildCache.getPhasmoGuessrChannelId(guild.getIdLong());
    }

    public static long getBotChannelId(Guild guild) {
        return GuildCache.getBotChannelId(guild.getIdLong());
    }

    /**
     * Gets the PhasmoGuessr Channel or null if it is not set or the channel cannot be found.
     * @param guild The guild to get the channel from
     * @return The TextChannel for PhasmoGuessr
     */
    public static TextChannel getPhasmoGuessrChannel(Guild guild){
        return guild.getTextChannelById(getPhasmoGuessrChannelId(guild));
    }

    /**
     * Gets the Bot Channel of this guild or null if it is not set or the channel cannot be found.
     * @param guild The guild to get the channel from
     * @return The TextChannel for Bot spam
     */
    public static TextChannel getBotChannel(Guild guild){
        return guild.getTextChannelById(getBotChannelId(guild));
    }

    public static boolean hasFlag(GuildFlag flag, Guild guild){
        int flags = GuildCache.getFlags(guild.getIdLong());
        return (flags & flag.getValue()) != 0;
    }

    public static int getPrivilegeLevel(Guild guild){
        return GuildCache.getPrivilegeLevel(guild.getIdLong());
    }

    public static boolean hasPrivilegeLevel(Guild guild, int privLevel){
        return GuildCache.getPrivilegeLevel(guild.getIdLong()) >= privLevel;
    }

    public static void setFlag(GuildFlag flag, boolean state, Guild guild){
        setFlag(flag, state, guild, s -> {});
    }

    public static void setFlag(GuildFlag flag, boolean state, Guild guild, Consumer<Boolean> success){
        if (Start.TEST_MODE) Logger.log(LogLevel.DEBUG, "Setting Guildflag: " + flag + " " + state + " for G: " + guild.getName());

        int flags = GuildCache.getFlags(guild.getIdLong());

        if(state){
            flags |= flag.getValue();
        } else {
            flags &= ~flag.getValue();
        }

        GuildHandler.setFlags(guild.getIdLong(),flags,success);
    }

    public static void toggleFlag(GuildFlag flag, Guild guild){
        toggleFlag(flag, guild, s -> {});
    }

    public static void toggleFlag(GuildFlag flag, Guild guild, Consumer<Boolean> success){
        setFlag(flag, !hasFlag(flag, guild), guild, success);
    }

    public static void setPhasmoGuessrChannel(long channelId, Guild guild){
        setPhasmoGuessrChannel(channelId, guild, s -> {});
    }
    public static void setPhasmoGuessrChannel(long channelId, Guild guild, Consumer<Boolean> success){
        GuildHandler.setphasmoguessrchannel(guild.getIdLong(),channelId, success);
    }

    public static void setPhasmoQuizChannel(long channelId, Guild guild){
        setPhasmoQuizChannel(channelId, guild, s -> {});
    }
    public static void setPhasmoQuizChannel(long channelId, Guild guild, Consumer<Boolean> success){
        GuildHandler.setphasmoquizchannel(guild.getIdLong(),channelId, success);
    }

}
