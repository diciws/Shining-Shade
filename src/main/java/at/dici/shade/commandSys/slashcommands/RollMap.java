package at.dici.shade.commandSys.slashcommands;

import at.dici.shade.commandSys.SlashCommand;
import at.dici.shade.utils.PhasResources;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Random;

public class RollMap extends SlashCommand {

    private static final SlashCommandData data;
    static {
        data = Commands.slash("rollmap", "Rolls a random map.")
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Würfelt eine zufällige Map aus.")
                .addOptions(
                        new OptionData(OptionType.STRING, "size", "Choose the size of the map")
                                .setNameLocalization(DiscordLocale.GERMAN,"größe")
                                .setDescriptionLocalization(DiscordLocale.GERMAN,"Welche Größe soll die gewürfelte Map haben?")
                                .addChoices(
                                        new Command.Choice("small","small")
                                                .setNameLocalization(DiscordLocale.GERMAN,"klein"),
                                        new Command.Choice("large","large")
                                                .setNameLocalization(DiscordLocale.GERMAN,"groß"),
                                        new Command.Choice("random","random")
                                                .setNameLocalization(DiscordLocale.GERMAN,"zufällig")
                                )
                );
    }
    @Override
    public SlashCommandData getData() {
        return data;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Random rnd = new Random();
        int n;
        PhasResources.MapData map;
        String size;

        OptionMapping option = event.getOption("size");
        if (option == null) size = "random";
        else size = option.getAsString();

        if (size.equals("small")) {
            n = rnd.nextInt(PhasResources.MapData.smallIds.length);
            map = PhasResources.maps.get(PhasResources.MapData.smallIds[n]);
        } else if (size.equals("large")) {
            n = rnd.nextInt(PhasResources.MapData.largeIds.length);
            map = PhasResources.maps.get(PhasResources.MapData.largeIds[n]);
        } else { // random size
            n = rnd.nextInt(PhasResources.MapData.smallIds.length + PhasResources.MapData.largeIds.length);
            if (n >= PhasResources.MapData.smallIds.length) {
                n -= PhasResources.MapData.smallIds.length;
                map = PhasResources.maps.get(PhasResources.MapData.largeIds[n]);
            } else {
                map = PhasResources.maps.get(PhasResources.MapData.smallIds[n]);
            }
        }

        event.reply(":map::")
                .addActionRow(Button.link(map.link, map.name))
                .queue();
    }
}
