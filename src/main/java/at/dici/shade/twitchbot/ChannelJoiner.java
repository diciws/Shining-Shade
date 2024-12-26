package at.dici.shade.twitchbot;

import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import com.github.twitch4j.chat.TwitchChat;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChannelJoiner {

    TwitchChat chat;
    ConcurrentLinkedQueue<String> channelQueue = new ConcurrentLinkedQueue<>();
    private JoinThread thread;

    /**
     * Queues all channels in channelNames to join them.
     * All channels are treated as verified.
     * @param chat The TwitchChat used to join
     * @param channelNames A list of all channels that should be joined.
     * @param channels The global Hashmap of channels.
     */
    ChannelJoiner(TwitchChat chat, List<String> channelNames, HashMap<String, ChannelProperties> channels){
        this.chat = chat;

        for(String name : channelNames){
            channelQueue.offer(name);
            channels.putIfAbsent(name, new ChannelProperties(true));
        }

        thread = new JoinThread(chat, channelQueue);
        thread.start();
    }

    /**
     * Registers a single new channel and queues it to join.
     * @param name Channel name
     * @param channels The global channel hashmap
     */
    public void queueChannel(String name, HashMap<String, ChannelProperties> channels, boolean verified){
        channels.put(name, new ChannelProperties(verified));
        channelQueue.offer(name);
        if (!thread.running) {
            thread = new JoinThread(chat, channelQueue);
            thread.start();
        }
    }

    public void removeChannel(String name, HashMap<String, ChannelProperties> channels){
        chat.leaveChannel(name);
        channelQueue.remove(name);
        channels.remove(name);
    }

    public void kill(){
        thread.interrupt();
        channelQueue.clear();
    }

    static class JoinThread extends Thread{

        TwitchChat chat;
        ConcurrentLinkedQueue<String> channelQueue;
        boolean running = false;

        JoinThread(TwitchChat chat, ConcurrentLinkedQueue<String> channelQueue){
            this.chat = chat;
            this.channelQueue = channelQueue;
        }

        @Override
        public void run(){
            running = true;
            String channelName;
            while((channelName = channelQueue.poll()) != null) {
                try {
                    sleep(TtvConfig.JOIN_PERIOD);
                } catch (InterruptedException e) {
                    Logger.log(LogLevel.FATAL,"ChannelJoiner interrupted! " + e);
                    return;
                }
                Logger.log(LogLevel.DEBUG,"Joined channel: " + channelName);
                chat.joinChannel(channelName);
            }
            running = false;
        }
    }

}
