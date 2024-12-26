package at.dici.shade.twitchbot.inventory;

import at.dici.shade.utils.PhasResources;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;

import java.util.*;

public class ItemInventory {
    private static final String LOG_PREFIX = "ItemInventory: ";
    private static final HashMap<String, ItemInventory> inventories = new HashMap<>();
    private static final Random random = new Random(System.currentTimeMillis());

    private final List<String> inventory;
    private final List<String> disposal;

    public ItemInventory() {
        inventory = new ArrayList<>();
        disposal = new ArrayList<>(Arrays.asList(PhasResources.items));
    }

    public static ItemInventory getInventory(String userId){
        return inventories.get(userId);
    }

    public static ItemInventory addInventory(String userId){
        Logger.log(LogLevel.DEBUG,LOG_PREFIX + "adding inventory for user: " + userId);
        ItemInventory inventory = new ItemInventory();
        inventories.put(userId,inventory);
        return inventory;
    }
    public static void removeInventory(String userId){
        if (inventories.remove(userId) != null)
            Logger.log(LogLevel.DEBUG,LOG_PREFIX + "removing inventory for user: " + userId);
    }
    public boolean isFull(){
        return disposal.isEmpty();
    }

    public boolean isEmpty(){
        return inventory.isEmpty();
    }


    public String getAsChatMessage(){
        if (isEmpty()) {
            return "Your inventory is empty.";
        }
        else {
            StringBuilder msg = new StringBuilder("Your inventory:\n");
            boolean first = true;
            for(String item : inventory){
                if (!first) msg.append(", ");
                else first = false;
                msg.append(item);
            }
            return msg.toString();
        }
    }

    /**
     * Adds all missing items to the inventory
     */
    public void fill(){
        inventory.addAll(disposal);
        disposal.clear();
    }


    /**
     * Add a random item to the inventory.
     * @return null if the inventory is full.
     */
    public String addItemRandom(){
        if(isFull()) {
            return null;
        }
        else {
            int n = random.nextInt(disposal.size());
            String item = disposal.remove(n);
            inventory.add(item);
            return item;
        }
    }

    /**
     * Removes a random item from inventory.
     * @return null if the inventory is empty.
     */
    public String removeItemRandom(){
        if(isEmpty()) {
            return null;
        }
        else {
            int n = random.nextInt(inventory.size());
            String item = inventory.remove(n);
            disposal.add(item);
            return item;
        }
    }


}
