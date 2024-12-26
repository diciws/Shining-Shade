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

public class CommandRemoveItem {
    private static final String logPrefix = "Inventory.CommandRemoveItem: ";
    private static final String LANG_PREFIX = "userapplications.inventory.commands.CommandRemoveItem.";
    public static final String COMMAND_NAME = "removeitem";

    public static SlashCommandData getCommandData() {
        OptionData item = new OptionData(OptionType.STRING, "item", "Which item shall be removed? Leave free for random", false);

        for (String s : PhasResources.items) {
            item.addChoice(s, s);
        }
        item.addChoice("Random","random");
        item.setDescriptionLocalization(DiscordLocale.GERMAN, "Welches Item soll entfernt werden? Freilassen für Zufall");

        return Commands.slash(COMMAND_NAME,"Remove an item from your inventory")
                .addOptions(item)
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Entferne ein Item aus deinem Inventar");
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

        if(inventory == null || inventory.isEmpty()){
            embed.setDescription(lang.getText("description_inv_already_empty"));
            embed.setColor(InventoryManager.EMBED_COLOR_ERROR);
        }
        else if (item == null || item.equals("random")){
            item = inventory.removeItemRandom();
            embed.setTitle(lang.getText("title_item_removed").replace("[item]", item));
        }
        else if (inventory.removeItem(item)) {
            embed.setTitle(lang.getText("title_item_removed").replace("[item]", item));
        }
        else {
            embed.setTitle(lang.getText("title_item_not_found").replace("[item]", item));
            embed.setColor(InventoryManager.EMBED_COLOR_ERROR);
        }
        if (inventory != null) return inventory.getItemsAsEmbed(embed, new Lang(interaction));
        else return embed;
    }
}
