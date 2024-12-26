package at.dici.shade.userapplications.inventory.commands;

import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.userapplications.inventory.InventoryManager;
import at.dici.shade.userapplications.inventory.ItemInventory;
import at.dici.shade.utils.PhasResources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandAddItem {
    private static final String logPrefix = "Inventory.CommandAddItem: ";
    private static final String LANG_PREFIX = "userapplications.inventory.commands.CommandAddItem.";
    public static final String COMMAND_NAME = "additem";

    public static SlashCommandData getCommandData() {
        OptionData item = new OptionData(OptionType.STRING, "item", "Which item shall be added? Leave free for random", false);

        for (String s : PhasResources.items) {
            item.addChoice(s, s);
        }
        item.addChoice("Random","random");
        item.setDescriptionLocalization(DiscordLocale.GERMAN, "Welches Item soll hinzugefügt werden? Freilassen für Zufall");

        return Commands.slash(COMMAND_NAME,"Add an item to your inventory")
                .addOptions(item)
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Füge ein Item zu deinem Inventar hinzu");
    }

    public static void perform(SlashCommandInteractionEvent event, InventoryManager manager) {
        String item = event.getOption("item") == null ? null : event.getOption("item").getAsString();

        EmbedBuilder embed = complete(event.getUser(), manager, item, event.getInteraction());
        event.replyEmbeds(embed.build())
                .setActionRow(manager.createButtons(event.getUser(), event.getInteraction()))
                .setEphemeral(true)
                .queue();
    }

    public static void perform(ButtonInteractionEvent event, InventoryManager manager) {
        EmbedBuilder embed = complete(event.getUser(), manager, null, event.getInteraction());

        // private (ephemeral) message
        if (event.getMessage().isEphemeral()) {
            event.replyEmbeds(embed.build())
                    .setActionRow(manager.createButtons(event.getUser(), event.getInteraction()))
                    .setEphemeral(true)
                    .queue();
        }
        // public message
        else {
            event.editMessageEmbeds(embed.build())
                    .setActionRow(manager.createButtons(event.getUser(), event.getInteraction()))
                    .queue();
        }
    }

    private static EmbedBuilder complete(User user, InventoryManager manager, String item, Interaction interaction){
        Lang lang = new Lang(LANG_PREFIX,interaction);

        ItemInventory inventory = manager.getInventory(user);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(InventoryManager.EMBED_COLOR_DEFAULT);

        if(inventory == null) inventory = manager.addInventory(user);
        if (item == null || item.equals("random")){
            item = inventory.addItemRandom();
            if (item == null) {
                embed.setTitle(lang.getText("invtitle_inv_full"));
                embed.setColor(InventoryManager.EMBED_COLOR_ERROR);
            } else {
                embed.setTitle(lang.getText("invtitle_added_item").replace("[item]", item));
            }
        } else if (inventory.addItem(item)) {
            embed.setTitle(lang.getText("invtitle_added_item").replace("[item]", item));
        } else {
            embed.setTitle(lang.getText("invtitle_item_already_there").replace("[item]", item));
            embed.setColor(InventoryManager.EMBED_COLOR_ERROR);
        }
        return inventory.getItemsAsEmbed(embed, new Lang(interaction));
    }

}
