package at.dici.shade.core.connector;

import at.dici.shade.Start;
import at.dici.shade.tasks.SecondThread;
import at.dici.shade.tasks.dailychallenges.DailyChallenge;
import at.dici.shade.utils.Resources;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;


public class BotStartListener extends ListenerAdapter {

    int shardsReady = 0;
    @Override
    public void onReady (@NotNull ReadyEvent event) {

        ShardManager shardManager = Start.getInstance().getShardManager();

        if(++shardsReady == shardManager.getShardsTotal()) {
            Logger.log(LogLevel.BOT, "Total of " + shardsReady + " ready!");
            Logger.init();
            TextChannel channel;

            if (Start.TEST_MODE) {
                channel = shardManager.getTextChannelById(Resources.CHANNEL_ADMINLOG_SUPPORT_SERVER);
            } else {
                channel = shardManager.getTextChannelById(Resources.CHANNEL_ADMINLOG_SUPPORT_SERVER);
            }

            if (channel != null) {
                channel.sendMessage(shardsReady + " Shard(s) ready! v" + Start.VERSION + (Start.TEST_MODE? " Development Version" : "")).queue();
            } else {
                Logger.log(LogLevel.ERROR, "Help! I can't find a channel to send my ready notification!");
            }

            new SecondThread().start();
            if (Start.TEST_MODE) {
                DailyChallenge ste = new DailyChallenge();
                ste.startDailyChallenge();
            }

        }

    }

}
