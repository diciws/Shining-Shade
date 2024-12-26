package at.dici.shade.twitchbot;

import at.dici.shade.twitchbot.commandsys.TtvCommandListener;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

public class TwitchBot {
    private static final String LOG_PREFIX = "TwitchBot: ";
    private final TwitchClient client;
    private final HashMap<String, ChannelProperties> channels = new HashMap<>();
    private final ScheduledFuture<?> rateLimiter;
    private final ChannelJoiner channelJoiner;


    public TwitchBot(){

        TwitchClientBuilder builder = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(new OAuth2Credential("twitch", TtvConfig.ACCESS_TOKEN));

        Logger.log(LogLevel.BOT,LOG_PREFIX + "Building client in: " + this.getClass().getSimpleName());
        client = builder.build();

        Logger.log(LogLevel.BOT,LOG_PREFIX + "Registering command listener..." );
        client.getEventManager().onEvent(ChannelMessageEvent.class, new TtvCommandListener(channels)::onChannelMessage);

        Logger.log(LogLevel.BOT,LOG_PREFIX + "Starting Rate Limiter...");
        rateLimiter = TtvRateLimiter.start(channels);

        Logger.log(LogLevel.BOT, LOG_PREFIX + "System ready." );

        Logger.log(LogLevel.BOT, LOG_PREFIX + "Joining channels..." );
        channelJoiner = new ChannelJoiner(client.getChat(), TtvConfig.channels, channels);

    }

    /**
     * Stops the Bot
     */
    public void kill(){
        Logger.log(LogLevel.INFORMATION, "Killing Twitch Bot");
        rateLimiter.cancel(true);
        channelJoiner.kill();
        client.close();
        channels.clear();
    }

    /**
     * Adds a single channel to the twitch bot
     * Should only be used to add single channels at runtime and not to add all initial channels
     * @param name The channel name
     * @param verified Determines if commands are instantly be activated or verification is awaited
     */
    public void addChannel(String name, boolean verified){
        channelJoiner.queueChannel(name, channels, verified);
    }
    public void removeChannel(String name) {
        channelJoiner.removeChannel(name, channels);
    }

    public int getStoredChannelQuantity(){
        return channels.size();
    }

    public Set<String> getStoredChannels(){
        return channels.keySet();
    }
}
