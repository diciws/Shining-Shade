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

public class Maps extends SlashCommand {

    private static final String LANG_PREFIX = "commands.maps.";

    private static final SlashCommandData data;
    static {
        data = Commands.slash("maps","Returns all Phasmophobia maps")
                .setDescriptionLocalization(DiscordLocale.GERMAN, "Zeigt alle Phasmophobia Karten");
    }
    @Override
    public SlashCommandData getData() {
        return data;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Lang lang = new Lang(LANG_PREFIX, event.getInteraction());

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(":map: " + lang.getText("maps_title"));
        StringBuilder descrBuilder = new StringBuilder();

        descrBuilder.append(":small_blue_diamond: " + lang.getText("small_desc")+" \n");
        descrBuilder.append("\n");

        for(int id : PhasResources.MapData.smallIds) {
            descrBuilder.append("[" + PhasResources.maps.get(id).name + "](" + PhasResources.maps.get(id).link + ") \n");
        }

        descrBuilder.append("\n");
        descrBuilder.append(":small_orange_diamond: " + lang.getText("medium_large_desc")+" \n");
        descrBuilder.append("\n");

        for(int id : PhasResources.MapData.largeIds) {
            descrBuilder.append("[" + PhasResources.maps.get(id).name + "](" + PhasResources.maps.get(id).link + ")\n");
        }

        builder.setColor(Resources.color);
        builder.setDescription(descrBuilder.toString());
        builder.setFooter(lang.getText("footer"));

        event.replyEmbeds(builder.build()).queue();
    }
}
