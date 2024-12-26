package at.dici.shade.twitchbot.commandsys.commands;

import at.dici.shade.twitchbot.ChannelProperties;
import at.dici.shade.twitchbot.commandsys.Command;
import at.dici.shade.twitchbot.utils.twitch.Util;
import at.dici.shade.utils.phasmophobia.Ghost;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.ArrayList;
import java.util.List;

public class CapacityTest extends Command {
    public static final List<String> aliase = new ArrayList<>();
    static {
        for (Ghost ghost : Ghost.values()) aliase.add(ghost.getName());
    }
    public static final List<String> requiredBadges = null;
    public static boolean isRunning = false;
    private static int counter = 0;

    public CapacityTest() {
        super(aliase);
        setRequiredBadge(requiredBadges);
        setRequiresVerified(false);
    }

    @Override
    public void execute(ChannelMessageEvent event, String[] cmd, ChannelProperties channel){
        Util.chatReply(event, cmd[0] + (++counter));
    }
}
