package at.dici.shade.twitchbot.inventory.commands;

import at.dici.shade.twitchbot.ChannelProperties;
import at.dici.shade.twitchbot.commandsys.Command;
import at.dici.shade.twitchbot.inventory.ItemInventory;
import at.dici.shade.twitchbot.utils.twitch.Res;
import at.dici.shade.twitchbot.utils.twitch.Util;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;

public class AddItem extends Command {
    public static final List<String> aliase = List.of("rollitem", "item", "additem", "itemadd", "invadd", "addinv");
    public static final List<String> requiredBadges = List.of(Res.MOD_BADGE, Res.OWNER_BADGE);

    public AddItem() {
        super(aliase);
        setRequiredBadge(requiredBadges);
    }

    public void execute(ChannelMessageEvent event, String[] cmd, ChannelProperties channel){
        String userId = event.getUser().getId();
        ItemInventory inventory = ItemInventory.getInventory(userId);

        if (inventory == null) inventory = ItemInventory.addInventory(userId);

        String item = inventory.addItemRandom();

        if (item == null) Util.chatReply(event, "Your inventory is full!");
        else Util.chatReply(event,item + " added.");

    }
}