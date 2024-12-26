package at.dici.shade.core.databaseio;

import at.dici.shade.core.cache.LanguageCache;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.Interaction;

import java.io.Serial;
import java.io.Serializable;

public class Lang implements Serializable {
    @Serial
    private static final long serialVersionUID = 400001;
    private String lineNamePrefix;
    private String languageShortcut;

    // Constructor for all events that implement interface Interaction
    public Lang(String lineNamePrefix, Interaction interaction){

        DiscordLocale locale;

        if (interaction.isFromGuild()){
            locale = interaction.getGuildLocale();
        } else {
            locale = interaction.getUserLocale();
        }

        this.lineNamePrefix = lineNamePrefix;
        languageShortcut = locale.getLocale();

    }

    public Lang(Interaction interaction){
        this("", interaction);
    }

    public Lang(String lineNamePrefix, DiscordLocale locale){
        this.lineNamePrefix = lineNamePrefix;
        languageShortcut = locale.getLocale();
    }
    public Lang(DiscordLocale locale){
        this("", locale);
    }

    public Lang setLocale(DiscordLocale locale){
        languageShortcut = locale.getLocale();
        return this;
    }

    public String getLocale() {
        return languageShortcut;
    }

    public String getText(String lineName){

        return LanguageCache.getLanguageOutput(lineNamePrefix + lineName, languageShortcut);
    }

    /**
     * Gets the output text while ignoring the lineNamePrefix set in the Lang object.
     * @param lineName The complete key to the translated text
     * @return The translated text
     */
    public String getTextIgnorePrefix(String lineName) {
        return LanguageCache.getLanguageOutput(lineName, languageShortcut);
    }

    public Lang setLineNamePrefix(String lineNamePrefix) {
        this.lineNamePrefix = lineNamePrefix;
        return this;
    }
}
