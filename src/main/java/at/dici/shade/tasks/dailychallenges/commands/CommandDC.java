package at.dici.shade.tasks.dailychallenges.commands;

import at.dici.shade.tasks.dailychallenges.DailyChallenge;
import at.dici.shade.tasks.dailychallenges.cache.DCCache;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandDC {
    public static final String COMMAND_NAME = "dailychallenge";
    public static SlashCommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, "Shows the daily challenge")
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Zeigt die tägliche Aufgabe");
    }

    public static void perform(SlashCommandInteractionEvent event){

        event.replyEmbeds(new EmbedBuilder()
                        .setTitle("✨ Daily Challenge: ")
                        .appendDescription(DCCache.getDailyChallengetitle(DailyChallenge.ActiveChallenge.get("active")))
                        .setFooter("Nächste Daily Challenge in "+DailyChallenge.returnRemainingDCTime()+" verfügbar!")
                        .build())
                .queue();
    }
}
