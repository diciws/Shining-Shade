package at.dici.shade.userapplications.inventory.commands;

import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.userapplications.inventory.InventoryManager;
import at.dici.shade.userapplications.inventory.ItemInventory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandShowInventory {
    private static final String LANG_PREFIX = "userapplications.inventory.commands.CommandShowInventory.";
    public static final String COMMAND_NAME = "showinventory";


    public static SlashCommandData getCommandData(){

        return Commands.slash(COMMAND_NAME, "Shows your inventory")
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Zeigt dein Inventar");
    }

    public static void perform(SlashCommandInteractionEvent event, InventoryManager manager){
        Lang lang = new Lang(LANG_PREFIX,event.getInteraction());

        ItemInventory inventory = manager.getInventory(event.getUser());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(InventoryManager.EMBED_COLOR_DEFAULT);

        if(inventory == null || inventory.isEmpty()) {
            embed.setDescription("**"+lang.getText("description_inv_empty_new")+"**");
        } else {
            inventory.getItemsAsEmbed(embed, new Lang(event.getInteraction()));
        }
        event.reply(event.getUser().getAsMention())
                .addEmbeds(embed.build())
                .addActionRow(manager.createButtons(event.getUser(), event.getInteraction()))
                .queue();
    }
}
