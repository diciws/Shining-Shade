package at.dici.shade.twitchbot.inventory.commands;

import at.dici.shade.twitchbot.ChannelProperties;
import at.dici.shade.twitchbot.commandsys.Command;
import at.dici.shade.twitchbot.inventory.ItemInventory;
import at.dici.shade.twitchbot.utils.twitch.Res;
import at.dici.shade.twitchbot.utils.twitch.Util;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;

public class RemoveItem extends Command {
    public static final List<String> aliase = List.of("removeitem", "itemremove", "invremove", "removeinv");
    public static final List<String> requiredBadges = List.of(Res.MOD_BADGE, Res.OWNER_BADGE);

    public RemoveItem() {
        super(aliase);
        setRequiredBadge(requiredBadges);
    }

    public void execute(ChannelMessageEvent event, String[] cmd, ChannelProperties channel){
        String userId = event.getUser().getId();
        ItemInventory inventory = ItemInventory.getInventory(userId);

        if (inventory == null || inventory.isEmpty()) Util.chatReply(event,"Your inventory is already empty!");
        else {
            String item = inventory.removeItemRandom();
            String msgEmpty;
            if(inventory.isEmpty()) {
                ItemInventory.removeInventory(userId);
                msgEmpty = " No items left in your inventory.";
            } else msgEmpty = "";
            Util.chatReply(event,item + " removed." + msgEmpty);
        }
    }
}