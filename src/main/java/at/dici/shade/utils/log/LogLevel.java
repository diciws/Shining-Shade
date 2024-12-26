package at.dici.shade.utils.log;

/**
 * @author dici
 */
public enum LogLevel {

    DEBUG(0, "\u001B[36m"),
    BOT(1, "\033[35m"),
    INFORMATION(2, "\u001B[32m"),
    URGENT(3, "\u001B[34m"),
    WARNING(4, "\u001B[33m"),
    ERROR(5, "\u001b[31;1m"),
    FATAL(6, "\u001B[31m");
    private final int level;
    private final String color;

    LogLevel(int level, String color) {
        this.level = level;
        this.color = color;
    }

    public int getLevel() {
        return this.level;
    }

    public String getColor() {
        return this.color;
    }

}