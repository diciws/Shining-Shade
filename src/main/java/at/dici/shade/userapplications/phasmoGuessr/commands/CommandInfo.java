package at.dici.shade.userapplications.phasmoGuessr.commands;

import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.userapplications.phasmoGuessr.PgUi;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandInfo {
    public static final String COMMAND_NAME = "pginfo";
    private static final String LANG_PREFIX = "userapplications.phasmoguessr.commands.CommandInfo.";
    public static SlashCommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, "Get information about PhasmoGuessr")
                .setGuildOnly(true) // For command-mentions!!
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Erhalte Infos über PhasmoGuessr");
    }

    public static void perform(SlashCommandInteractionEvent event){
        Lang lang = new Lang(LANG_PREFIX, event.getInteraction());
        PgUi ui = new PgUi(lang);

        ui.getReplyPgInfo(event).queue();
    }
}
