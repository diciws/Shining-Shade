package at.dici.shade.userapplications.phasmoGuessr.commands;

import at.dici.shade.core.databaseio.GuildIo;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.userapplications.phasmoGuessr.Game;
import at.dici.shade.userapplications.phasmoGuessr.PgUi;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandStart {
    private static final String LANG_PREFIX = "userapplications.phasmoguessr.commands.CommandStart.";
    public static final String COMMAND_NAME = "pgstart";

    public static SlashCommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, "Start a round PhasmoGuessr")
                .setGuildOnly(true)
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Starte eine Runde PhasmoGuessr");
    }

    public static boolean perform(SlashCommandInteractionEvent event, Game game) {
        Lang lang = new Lang(LANG_PREFIX,event.getInteraction());
        PgUi ui = new PgUi(lang);

        if (GuildIo.getPhasmoGuessrChannelId(event.getGuild()) != event.getChannel().getIdLong()) {
            // wrong channel
            ui.getReplyStartWrongChannel(event).queue();
            return false;
        } else if (game == null) {
            ui.getReplyStartSuccess(event).queue();
            return true;
        } else { // Game already running
            ui.getReplyStartGameAlreadyRunning(event).queue();
            return false;
        }
    }

}

