package at.dici.shade.userapplications.inventory.commands;

import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.userapplications.inventory.InventoryManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandClearInventory {
    private static final String LANG_PREFIX = "userapplications.inventory.commands.CommandClearInventory.";
    public static final String COMMAND_NAME = "resetinventory";


    public static SlashCommandData getCommandData(){

        return Commands.slash(COMMAND_NAME, "Empties your inventory")
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Leert dein Inventar");
    }

    public static void perform(SlashCommandInteractionEvent event, InventoryManager manager){
        Lang lang = new Lang(LANG_PREFIX,event.getInteraction());

        manager.removeInventory(event.getUser());

        event.replyEmbeds(new EmbedBuilder()
                        .setDescription("**"+lang.getText("description_inv_cleared_new"+"**"))
                        .setColor(InventoryManager.EMBED_COLOR_DEFAULT)
                        .build())
                .setEphemeral(true)
                .queue();
    }

    public static void perform(ButtonInteractionEvent event, InventoryManager manager){
        Lang lang = new Lang(LANG_PREFIX,event.getInteraction());

        manager.removeInventory(event.getUser());
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(lang.getText("title_inv_cleared"))
                .setColor(InventoryManager.EMBED_COLOR_DEFAULT)
                .appendDescription(lang.getText("description_inv_now_empty").replace("[user]", event.getUser().getAsMention()));

        if (event.getMessage().isEphemeral()) {
            event.replyEmbeds(embed.build())
                    .setActionRow(manager.createButtons(event.getUser(), event.getInteraction()))
                    .setEphemeral(true)
                    .queue();
        } else {
            event.editMessageEmbeds(embed.build())
                    .setActionRow(manager.createButtons(event.getUser(), event.getInteraction()))
                    .queue();
        }
    }

}
