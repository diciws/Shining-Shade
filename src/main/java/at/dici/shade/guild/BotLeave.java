package at.dici.shade.guild;

import at.dici.shade.core.handler.GuildHandler;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotLeave extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        //Remove from guild
        GuildHandler.removeGuild( event.getGuild().getIdLong(), success ->
                Logger.log(LogLevel.INFORMATION,"Removed Guild success: " + event.getGuild().getName())
        );

    }
}