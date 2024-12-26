package at.dici.shade.twitchbot;

import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TtvRateLimiter {
    public static ScheduledFuture<?> start(HashMap<String, ChannelProperties> channels){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        return executor.scheduleAtFixedRate(
                () -> {
                    try {
                        Set<String> keys = channels.keySet();
                        for (String k : keys) {
                            ChannelProperties channel = channels.get(k);
                            if (channel != null) {
                                channel.resetRateLimitCounter();
                            }
                        }
                    } catch (Exception e) {
                        Logger.log(LogLevel.FATAL, "Twitchbot RateLimiter: Exception in scheduled task: " + e);
                    }
                },
                0,
                TtvConfig.MSG_RATE_LIMIT_TIME,
                TimeUnit.SECONDS
        );
    }
}
