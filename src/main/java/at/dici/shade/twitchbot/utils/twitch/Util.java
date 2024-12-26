package at.dici.shade.twitchbot.utils.twitch;

import at.dici.shade.twitchbot.commandsys.commands.CapacityTest;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.Optional;

public class Util {

    public static void chatReply(ChannelMessageEvent event, String message) {
        Optional<String> replyId = event.getMessageEvent().getMessageId();
        TwitchChat chat = event.getTwitchChat();

        if (!CapacityTest.isRunning) {
            String channel = event.getChannel().getName();
            if (channel.equalsIgnoreCase("insym")) return; // Just to be sure that we don't spam him.
            if (replyId.isPresent()) chat.sendMessage(channel, message, "a", replyId.get());
            else chat.sendMessage(channel, message);
        }
        else chat.sendMessage("shiningshadebot", message);
    }
}
