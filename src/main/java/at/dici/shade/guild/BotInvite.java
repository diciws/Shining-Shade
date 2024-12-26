package at.dici.shade.guild;

import at.dici.shade.core.handler.GuildHandler;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotInvite extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event){
        // Register new Guild on Bot invite
        GuildHandler.guildAsync( event.getGuild().getIdLong(), success ->
                Logger.log(LogLevel.INFORMATION,"Registry of new Guild success: " + event.getGuild().getName())
        );
        /**
        TODO: (dici)
         - send embed message on connect: (No solution found yet)

         possible commands:
            - shade -> DONE
            - help -> DONE
            - configurator (Admin only) -> Not necessary now!

            -> Features on support discord server
         */

    }

}
