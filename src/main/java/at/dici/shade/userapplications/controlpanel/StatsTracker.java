package at.dici.shade.userapplications.controlpanel;

import at.dici.shade.Start;
import at.dici.shade.twitchbot.TwitchBot;
import at.dici.shade.userapplications.phasmoGuessr.Game;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class StatsTracker extends ListenerAdapter {

    private static int commandsUsed = 0;
    private static int buttonsClicked = 0;
    private static int timesMentioned = 0;
    private static int dbUserWrites = 0;
    private static int dbGuildWrites = 0;
    private static int dbBotWrites = 0;
    private static int ttvCommandsUsed = 0;
    private static HashMap<Long, Game> pgGames;

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getMessage().getMentions().isMentioned(event.getJDA().getSelfUser())) {
            timesMentioned++;
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
        commandsUsed++;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
        buttonsClicked++;
    }

    static int getCommandsUsed() {
        return commandsUsed;
    }

    static int getButtonsClicked() {
        return buttonsClicked;
    }

    static int getTimesMentioned() {
        return timesMentioned;
    }

    static int getDbUserWrites() {
        return dbUserWrites;
    }

    static int getDbGuildWrites() {
        return dbGuildWrites;
    }

    static int getDbBotWrites() {
        return dbBotWrites;
    }

    static int getNumberPgGames(){
        if (pgGames == null) return 0;
        return pgGames.size();
    }

    static int getTtvCommandsUsed() {
        return ttvCommandsUsed;
    }

    static int getTtvChannelQuantity(){
        TwitchBot tBot = Start.getInstance().getTwitchBot();
        if (tBot != null) return tBot.getStoredChannelQuantity();
        else return 0;
    }

    /**
     * To be called every time anything is written to the user DB
     */
    public static void dbUserWritten() {
        dbUserWrites++;
    }

    /**
     * To be called every time anything is written to the guild DB
     */
    public static void dbGuildWritten() {
        dbGuildWrites++;
    }

    /**
     * To be called every time anything is written to the bot DB (settings / global stats)
     */
    public static void dbBotWritten() {
        dbBotWrites++;
    }
    public static void setPgGames(HashMap<Long, Game> games){
        pgGames = games;
    }
    public static void ttvCommandUsed(){
        ttvCommandsUsed++;
    }

}
