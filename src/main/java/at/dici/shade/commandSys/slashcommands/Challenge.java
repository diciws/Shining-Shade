package at.dici.shade.commandSys.slashcommands;

import at.dici.shade.commandSys.SlashCommand;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.utils.PhasResources;
import at.dici.shade.utils.Resources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Random;

public class Challenge extends SlashCommand {

    private static final SlashCommandData data;
    static {
        data = Commands.slash("challenge","Gives you a random Challenge")
                .setDescriptionLocalization(DiscordLocale.GERMAN, "Stellt dir eine zufällige Herausforderung");
    }
    @Override
    public SlashCommandData getData() {
        return data;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Lang lang = new Lang(event.getInteraction());

        Random rnd = new Random();
        int n = rnd.nextInt(PhasResources.challengesDebuff.length);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(":game_die: "+lang.getText("challenges.challenge_title")+":");
        embed.setDescription(lang.getText(PhasResources.challengesDebuff[n]));
        embed.setColor(Resources.color);

        event.replyEmbeds(embed.build()).queue();
    }
}
