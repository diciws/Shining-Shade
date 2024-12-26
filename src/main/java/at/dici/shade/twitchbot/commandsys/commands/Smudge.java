package at.dici.shade.twitchbot.commandsys.commands;

import at.dici.shade.twitchbot.ChannelProperties;
import at.dici.shade.twitchbot.commandsys.Command;
import at.dici.shade.twitchbot.utils.twitch.Res;
import at.dici.shade.twitchbot.utils.twitch.Util;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Smudge extends Command {
    public static final List<String> aliase = List.of("smudge", "timer", "smudgetimer");
    public static final List<String> requiredBadges = List.of(Res.MOD_BADGE, Res.OWNER_BADGE);
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final HashMap<String, MsgTasks> timersMap = new HashMap<>();

    public Smudge() {
        super(aliase);
        setRequiredBadge(requiredBadges);
    }


    public void execute(ChannelMessageEvent event, String[] cmd, ChannelProperties channel) {
        String chName = event.getChannel().getId();
        MsgTasks msgTasks = timersMap.get(chName);

        if (msgTasks == null) {
            timersMap.put(chName, new MsgTasks(event));
            Util.chatReply(event, "Timer started!");
        } else {
            msgTasks.restart(event);
            Util.chatReply(event, "Timer restarted!");
        }

    }

    private static class MsgTasks{
        ScheduledFuture<?> demonFuture;
        ScheduledFuture<?> othersFuture;
        ScheduledFuture<?> spiritFuture;

        MsgTasks(ChannelMessageEvent event){
            schedule(event);
        }

        private void schedule(ChannelMessageEvent event){
            demonFuture = Smudge.executor.schedule(() -> Util.chatReply(event, "A Demon could already start a hunt! (60s)"), 60L, TimeUnit.SECONDS);
            othersFuture = Smudge.executor.schedule(() -> Util.chatReply(event, "Every ghost except Sprit is able to hunt now! (90s)"), 90L, TimeUnit.SECONDS);
            spiritFuture = Smudge.executor.schedule(() -> Util.chatReply(event, "Even a Spirit could hunt by now! (180s)"), 180L, TimeUnit.SECONDS);
        }
        void restart(ChannelMessageEvent event){
            cancel();
            schedule(event);
        }

        void cancel(){
            demonFuture.cancel(false);
            othersFuture.cancel(false);
            spiritFuture.cancel(false);
        }
    }

}