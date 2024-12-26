package at.dici.shade.userapplications.phasmoQuiz.commands;

import at.dici.shade.core.databaseio.GuildIo;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.userapplications.phasmoQuiz.PQManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PQStart {
    private static final String LANG_PREFIX = "userapplications.phasmoquiz.commands.pqstart.";
    public static final String COMMAND_NAME = "pqstart";

    public static SlashCommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, "Start a round PhasmoQuiz")
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Starte eine Runde PhasmoQuiz");
    }

    public static boolean perform(SlashCommandInteractionEvent event) {
        Lang lang = new Lang(LANG_PREFIX,event.getInteraction());

        //
        //if (GuildIo.getPhasmoQuizChannelId(event.getGuild()) != event.getChannel().getIdLong()) {
        //    // wrong channel
        //    event.reply(lang.getText("reply_wrong_channel")).setEphemeral(true).queue();
        //    return false;
        //} else if (game == null) {
        //    event.reply("Let's go!").setEphemeral(true).queue();
        //    return true;
        //} else { // Game already running
        //    event.reply(lang.getText("reply_game_already_running")).setEphemeral(true).queue();
        //    return false;
        //}
        //
        return false;
    }

}
