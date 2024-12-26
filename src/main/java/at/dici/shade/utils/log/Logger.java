package at.dici.shade.utils.log;

import at.dici.shade.Start;
import at.dici.shade.utils.Resources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author dici
 */
public class Logger {
    public static final String RESET_CONSOLE_COLOR = "\u001B[0m";
    public static TextChannel channel = null;

    /**
     * Has to be called after ShardManager is built.
     */
    public static void init() {
        Logger.bot("Initializing Logger for Discord");
        ShardManager shardManager = Start.getInstance().getShardManager();
        if (shardManager != null) {
            channel = shardManager.getTextChannelById(Resources.getLogChannelId());
        }
        if (channel == null) Logger.error("Logger: Log channel not found.");
    }

    public static void log(LogLevel level, String message) {
        if (level == LogLevel.DEBUG && !Start.TEST_MODE) return;

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", new Locale("de", "DE"));
        Date date = new Date();

        System.out.println("[" + format.format(date) + " - " + level + "] " + level.getColor() + message + RESET_CONSOLE_COLOR);

        String filename = "logs/" + (new SimpleDateFormat("dd-MM-yyyy").format(new Date())) + ".log";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.write("[" + format.format(date) + " - " + level + "] " + message + "\n");
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (level.getLevel() >= LogLevel.URGENT.getLevel()
                && channel != null) {
            String mention;
            if (level == LogLevel.FATAL) {
                mention = "@everyone\n";
            }
            else mention = "";
            channel.sendMessageEmbeds(
                    new EmbedBuilder()
                            .setTitle(level.name())
                            .setDescription(mention)
                            .appendDescription(message)
                            .build()
            ).queue();
        }
    }

    public static void detailedLog(LogLevel level, String logMessage, String detailedMessage) {
        if (level == LogLevel.DEBUG && !Start.TEST_MODE) return;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", new Locale("de", "DE"));
        Date date = new Date();

        if (logMessage != null) info("DETAILED-LOGGER: " + logMessage);

        String filename = "detailed_logs/" + (new SimpleDateFormat("dd-MM-yyyy").format(new Date())) + ".log";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.write("[" + format.format(date) + " - " + level + "] " + detailedMessage + "\n");
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (level.getLevel() >= LogLevel.URGENT.getLevel()
                && channel != null) {
            String mention;
            if (level == LogLevel.FATAL) {
                mention = "@everyone\n";
            }
            else mention = "";
            channel.sendMessageEmbeds(
                    new EmbedBuilder()
                            .setTitle(level.name())
                            .setDescription(mention)
                            .appendDescription(detailedMessage)
                            .build()
            ).queue();
        }
    }

    public static void detailedLog(LogLevel level, String detailedMessage) {
        detailedLog(level, null, detailedMessage);
    }

    public static void debug(String message){
        log(LogLevel.DEBUG, message);
    }
    public static void bot(String message) {
        log(LogLevel.BOT, message);
    }

    public static void info(String message) {
        log(LogLevel.INFORMATION, message);
    }
    public static void urgent(String message) {
        log(LogLevel.URGENT, message);
    }
    public static void warn(String message) {
        log(LogLevel.WARNING, message);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public static void fatal(String message) {
        log(LogLevel.FATAL, message);
    }
}
