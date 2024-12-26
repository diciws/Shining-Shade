package at.dici.shade.twitchbot.commandsys.commands;

import at.dici.shade.twitchbot.ChannelProperties;
import at.dici.shade.twitchbot.commandsys.Command;
import at.dici.shade.twitchbot.utils.twitch.Res;
import at.dici.shade.twitchbot.utils.twitch.Util;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;

public class Verify extends Command {
    public static final List<String> aliase = List.of("shiningshade");
    public static final List<String> requiredBadges = List.of(Res.OWNER_BADGE);

    public Verify() {
        super(aliase);
        setRequiredBadge(requiredBadges);
        setRequiresVerified(false);
    }

    @Override
    public void execute(ChannelMessageEvent event, String[] cmd, ChannelProperties channel) {
        if (channel.isVerified()) {
            Util.chatReply(event, "I am already connected to your chat!");
        } else {
            channel.setVerified(true);
            Util.chatReply(event, "Hey, Thank you for welcoming me in your chat! Click on my name and see my profile for more information. :)");
        }
    }
}