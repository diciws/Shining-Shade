package at.dici.shade.userapplications.phasmoQuiz.commands;

import at.dici.shade.core.databaseio.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PQInfo {
    public static final String COMMAND_NAME = "pqinfo";
    private static final String LANG_PREFIX = "userapplications.phasmoquiz.commands.pqinfo.";
    public static SlashCommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, "Get information about PhasmoQuiz")
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Erhalte Infos über PhasmoQuiz");
    }

    public static void perform(SlashCommandInteractionEvent event){
        Lang lang = new Lang(LANG_PREFIX, event.getInteraction());

        event.replyEmbeds(new EmbedBuilder()
                        .setTitle("PhasmoQuiz Info")
                        .appendDescription(lang.getText("description"))
                        .build())
                .setEphemeral(true)
                .queue();
    }
}