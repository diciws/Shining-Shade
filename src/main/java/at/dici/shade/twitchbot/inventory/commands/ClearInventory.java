package at.dici.shade.twitchbot.inventory.commands;

import at.dici.shade.twitchbot.ChannelProperties;
import at.dici.shade.twitchbot.commandsys.Command;
import at.dici.shade.twitchbot.inventory.ItemInventory;
import at.dici.shade.twitchbot.utils.twitch.Res;
import at.dici.shade.twitchbot.utils.twitch.Util;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;

public class ClearInventory extends Command {
    public static final List<String> aliase = List.of("invreset", "resetinv", "resetinventory", "inventoryreset", "clearinv", "invclear", "clearinventory", "inventoryclear");
    public static final List<String> requiredBadges = List.of(Res.MOD_BADGE, Res.OWNER_BADGE);
    public ClearInventory() {
        super(aliase);
        setRequiredBadge(requiredBadges);
    }

    public void execute(ChannelMessageEvent event, String[] cmd, ChannelProperties channel){
        String userId = event.getUser().getId();

        ItemInventory.removeInventory(userId);

        Util.chatReply(event,"Inventory reset.");

    }
}
