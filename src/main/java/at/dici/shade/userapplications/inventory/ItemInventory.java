package at.dici.shade.userapplications.inventory;

import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.utils.PhasResources;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemInventory {

    static final String logPrefix = "ItemInventory: ";
    private static final String LANG_PREFIX = "userapplications.inventory.ItemInventory.";
    private boolean debugmode = false;
    private final List<String> inventory;
    private final List<String> disposal;
    private final User user;

    public ItemInventory(User user) {
        inventory = new ArrayList<>();
        disposal = new ArrayList<>(Arrays.asList(PhasResources.items));
        this.user = user;
    }

    public List<String> getDisposal(){
        return Collections.unmodifiableList(disposal);
    }

    /**
     * Only appends a description text to the given embed.
     * No further attributes are being set.
     * @param embedBuilder The EmbedBuilder to modify
     * @return The same instance of EmbedBuilder as the parameter
     */
    public EmbedBuilder getItemsAsEmbed(EmbedBuilder embedBuilder, Lang lang){
        lang.setLineNamePrefix(LANG_PREFIX);

        if (inventory.isEmpty()) {
            embedBuilder.appendDescription(lang.getText("description_inv_of_user_empty").replace("[user]", user.getAsMention()));
        }
        else {
            embedBuilder.appendDescription(lang.getText("description_inv_of_user").replace("[user]", user.getAsMention()));
        }
        for(String item : inventory){
            embedBuilder.appendDescription("\n - " + item);
        }
        return embedBuilder;
    }

    /**
     * Adds an item to the inventory if it is available in disposal.
     * @param item The item to add
     * @return true if the item was added
     */
    public boolean addItem(String item){
        if (!disposal.contains(item)) {
            log("addItem() failed. Item not found in disposal. Item: " + item);
            return false;
        }
        else{
            log("addItem() success - Item: " + item);
            disposal.remove(item);
            inventory.add(item);
            return true;
        }
    }

    /**
     * Removes an item from inventory if it's in there.
     * @param item The item to remove
     * @return true if the item was removed.
     */
    public boolean removeItem(String item){
        if (disposal.contains(item)) {
            log("removeItem() failed. Item not found in Inventory. Item: " + item);
            return false;
        }
        else{
            disposal.add(item);
            inventory.remove(item);
            log("removeItem() success - Item: " + item);
            return true;
        }
    }

    /**
     * Add a random item to the inventory.
     * @return null if the inventory is full.
     */
    public String addItemRandom(){
        if(isFull()) {
            log("addItemRandom() fail (inv full)");
            return null;
        }
        else {
            int n = InventoryManager.random.nextInt(disposal.size());
            String item = disposal.remove(n);
            inventory.add(item);
            log("addItemRandom() success - Item: " + item);
            return item;
        }
    }

    /**
     * Removes a random item from inventory.
     * @return null if the inventory is empty.
     */
    public String removeItemRandom(){
        if(isEmpty()) {
            log("removeItemRandom() fail (inv empty)");
            return null;
        }
        else {
            int n = InventoryManager.random.nextInt(inventory.size());
            String item = inventory.remove(n);
            disposal.add(item);
            log("removeItemRandom() success - Item: " + item);
            return item;
        }
    }

    public boolean isFull(){
        return disposal.isEmpty();
    }

    public boolean isEmpty(){
        return inventory.isEmpty();
    }

    /**
     * Clears the inventory without deleting it. Should not be needed.
     */
    void clear(){
        disposal.addAll(inventory);
        inventory.clear();
        log("Inv cleared!");
    }

    /**
     * Adds every item possible to the inventory
     */
    void fill(){
        inventory.addAll(disposal);
        disposal.clear();
        log("Inv filled!");
    }

    void setDebugmode(boolean debugmode){
        this.debugmode = debugmode;
    }

    void log(String msg){
        Logger.log(LogLevel.DEBUG,logPrefix + "User: " + user.getName() + " - " + msg);
    }

}
