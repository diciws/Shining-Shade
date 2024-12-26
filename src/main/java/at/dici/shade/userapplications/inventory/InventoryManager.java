package at.dici.shade.userapplications.inventory;

import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.userapplications.inventory.commands.CommandAddItem;
import at.dici.shade.userapplications.inventory.commands.CommandClearInventory;
import at.dici.shade.userapplications.inventory.commands.CommandRemoveItem;
import at.dici.shade.userapplications.inventory.commands.CommandShowInventory;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class InventoryManager extends ListenerAdapter {
    private static final String LANG_PREFIX = "userapplications.inventory.InventoryManager.";
    static final String logPrefix = "InventoryManager: ";
    boolean debugmode = false;
    private final HashMap<User, ItemInventory> inventories;
    static final Random random = new Random(System.currentTimeMillis());
    public static final Color EMBED_COLOR_DEFAULT = Color.darkGray;
    public static final Color EMBED_COLOR_ERROR = Color.RED;

    public InventoryManager(){
        inventories = new HashMap<>();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(CommandAddItem.COMMAND_NAME)) {
            CommandAddItem.perform(event, this);
        } else if (event.getName().equals(CommandRemoveItem.COMMAND_NAME)) {
            CommandRemoveItem.perform(event, this);
        } else if (event.getName().equals(CommandClearInventory.COMMAND_NAME)) {
            CommandClearInventory.perform(event, this);
        } else if (event.getName().equals(CommandShowInventory.COMMAND_NAME)) {
            CommandShowInventory.perform(event, this);
        }

    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        String id = event.getButton().getId();
        if(id.equals("InventoryManager_addRandomItem")){
            if (!sendWarningIfNotOwner(event)) {
                CommandAddItem.perform(event,this);
            }
        } else if (id.equals("InventoryManager_removeRandomItem")) {
            if (!sendWarningIfNotOwner(event)) {
                CommandRemoveItem.perform(event, this);
            }


        } else if (id.equals("InventoryManager_clearInventory")) {
            if (!sendWarningIfNotOwner(event)) {
                CommandClearInventory.perform(event,this);
            }
        }
    }

    /**
     * Sends a warning if the user is trying to interact with an inventory that does not belong to him.
     * @param event The Button Event
     * @return True if user is not owner and warning is being sent
     */
    private boolean sendWarningIfNotOwner(ButtonInteractionEvent event){
        if (event.getMessage().isEphemeral()
                || event.getMessage().getMentions().isMentioned(event.getUser()))
            return false;
        else {
            Lang lang = new Lang(LANG_PREFIX,event.getInteraction());
            EmbedBuilder embed = new EmbedBuilder()
                    .appendDescription(lang.getText("warning_not_inv_owner"));
            event.replyEmbeds(embed.build())
                    .addActionRow(createButtons(event.getUser(), event.getInteraction()))
                    .setEphemeral(true)
                    .queue();
            return true;
        }
    }

    /**
     * Creates the inventory action buttons based on the users inventory
     * @param user The user on whose inventory the buttons are based
     * @return A List of inventory aciton buttons
     */
    public List<Button> createButtons(User user, Interaction interaction){
        Lang lang = new Lang(LANG_PREFIX, interaction);

        ItemInventory inventory = inventories.get(user);

        Button btnAdd = Button.secondary("InventoryManager_addRandomItem","+ Random Item");
        if (inventory != null && inventory.isFull()) btnAdd = btnAdd.asDisabled();

        Button btnRemove = Button.secondary("InventoryManager_removeRandomItem", "- Random Item");
        Button btnClear = Button.danger("InventoryManager_clearInventory", lang.getText("btnlabel_clear"));
        if (inventory == null || inventory.isEmpty()) {
            btnRemove = btnRemove.asDisabled();
            btnClear = btnClear.asDisabled();
        }

        return List.of(btnAdd,btnRemove,btnClear);
    }

    public ItemInventory getInventory(User user){
        return inventories.get(user);
    }

    public ItemInventory addInventory(User user){
        Logger.log(LogLevel.DEBUG,logPrefix + "Adding inventory for " + user);
        ItemInventory inventory = new ItemInventory(user);
        inventories.put(user,inventory);
        return inventory;
    }

    public void removeInventory(User user){
        Logger.log(LogLevel.DEBUG,logPrefix + "Removing inventory of " + user);
        inventories.remove(user);
    }
}
