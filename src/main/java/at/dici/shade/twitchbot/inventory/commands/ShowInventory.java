package at.dici.shade.twitchbot.inventory.commands;

import at.dici.shade.twitchbot.ChannelProperties;
import at.dici.shade.twitchbot.commandsys.Command;
import at.dici.shade.twitchbot.inventory.ItemInventory;
import at.dici.shade.twitchbot.utils.twitch.Res;
import at.dici.shade.twitchbot.utils.twitch.Util;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;

public class ShowInventory extends Command {
    public static final List<String> aliase = List.of("inv", "inventory", "showinv", "invshow", "showinventory", "inventoryshow");
    public static final List<String> requiredBadges = List.of(Res.MOD_BADGE, Res.OWNER_BADGE);

    public ShowInventory() {
        super(aliase);
        setRequiredBadge(requiredBadges);
    }

    public void execute(ChannelMessageEvent event, String[] cmd, ChannelProperties channel){
        String userId = event.getUser().getId();
        ItemInventory inventory = ItemInventory.getInventory(userId);

        if (inventory == null || inventory.isEmpty())
            Util.chatReply(event, "Your inventory is empty.");
        else
            Util.chatReply(event, inventory.getAsChatMessage());
    }
}