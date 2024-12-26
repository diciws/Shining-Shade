package at.dici.shade.commandSys.slashcommands;

import at.dici.shade.commandSys.SlashCommand;
import at.dici.shade.utils.log.Logger;
import at.dici.shade.utils.phasmophobia.Evidence;
import at.dici.shade.utils.phasmophobia.Ghost;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class GhostInfo extends SlashCommand {

    private static final String LANG_PREFIX = "commands.ghost.";
    private static final SlashCommandData data;
    static {
        data = Commands.slash("ghost","Info about each ghost")
                .setDescriptionLocalization(DiscordLocale.GERMAN, "Info über jeden Geist");

        OptionData ghostOption = new OptionData(OptionType.STRING,"ghost","Select a ghost", true)
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Wähle einen Geist");

        for (Ghost ghost : Ghost.values()) {
            if (ghostOption.getChoices().size() < 25) {
                ghostOption.addChoice(ghost.getName(), ghost.getName());
            }
        }

        data.addOptions(ghostOption);
    }

    @Override
    public SlashCommandData getData() {
        return data;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption("ghost");
        Ghost ghost;
        if (option == null) {
            ghost = Ghost.SPIRIT;
        } else {
            ghost = Ghost.getByName(option.getAsString());
            if (ghost == null) {
                Logger.error("Höö Ghost shouldn't be null! Command Ghost Info fucked up! Defaulting to spirit...");
                ghost = Ghost.SPIRIT;
            }
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("***" + ghost.getName() + "***");
        embed.setDescription("Evidence:\n");

        for (Evidence evidence : ghost.getEvidences()) {
            embed.appendDescription(evidence.getName());
        }

        embed.appendDescription("\nGuaranteed Evidence: " + ghost.getGuaranteedEvidence());

        if (ghost == Ghost.MIMIC) {
            embed.appendDescription("\n\nThe orb is not considered real evidence when dealing with a Mimic.");
        }

        embed.appendDescription("\n\nCan be fast: " + ghost.canHaveSpeed(Ghost.Speed.FAST));
        embed.appendDescription("\nCan be slow: " + ghost.canHaveSpeed(Ghost.Speed.SLOW));
        embed.appendDescription("\n\nImagine further info copied from Shuee here...");

        event.replyEmbeds(embed.build()).queue();
    }
}
