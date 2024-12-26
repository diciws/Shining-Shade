package at.dici.shade.userapplications.phasmoGuessr;

import at.dici.shade.core.databaseio.GuildIo;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.core.databaseio.Rank;
import at.dici.shade.userapplications.controlpanel.StatsTracker;
import at.dici.shade.userapplications.phasmoGuessr.commands.CommandGuess;
import at.dici.shade.userapplications.phasmoGuessr.commands.CommandInfo;
import at.dici.shade.userapplications.phasmoGuessr.commands.CommandSetChannel;
import at.dici.shade.userapplications.phasmoGuessr.commands.CommandStart;
import at.dici.shade.utils.Resources;
import at.dici.shade.utils.Util;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class PgManager extends ListenerAdapter {
    private static final String LOG_PREFIX = "PG_Manager: ";
    private static final String LANG_PREFIX = "userapplications.phasmoguessr.PgManager.";

    private HashMap<Long,Game> games; // Key = Channel-ID
    private final HashMap<Long, Integer> guildBonusMap = new HashMap<>();
    private int globalBonus = 0;
    private boolean saving = false;
    private boolean restarting = true;

    public PgManager(){
        loadGames();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        long channelId = GuildIo.getPhasmoGuessrChannelId(event.getGuild());
        Game game = getGame(channelId);
        if (game != null) {
            Logger.info(LOG_PREFIX + "Removing PG instance for guild: " + event.getGuild().getName());
            games.remove(channelId);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getGuild() != null) {
            GuildMessageChannel channel = event.getGuildChannel();
            if (event.getName().equals(CommandGuess.COMMAND_NAME)) {
                Game game = getGame(event.getGuildChannel());
                if (restarting) {
                    event.reply("I'm in the process of restarting right now. Try again in a minute! c:")
                            .setEphemeral(true)
                            .queue();
                } else {
                    CommandGuess.perform(event, game);
                }
                if (game != null && game.getState() == State.GAME_OVER) {
                    games.remove(channel.getIdLong());
                    Logger.log(LogLevel.DEBUG, LOG_PREFIX + "Game finished and removed.");
                }
            }

            else if (event.getName().equals(CommandStart.COMMAND_NAME)) {
                if (restarting) {
                    event.reply("I'm in the process of restarting right now. Try again in a minute! c:")
                            .setEphemeral(true)
                            .queue();
                } else if (CommandStart.perform(event, getGame(channel))) {
                    games.put(channel.getIdLong(),
                            new Game(
                                    event.getGuildChannel(),
                                    event.getInteraction(),
                                    sumBonus(event.getGuild())
                            )
                    );
                }
            }

            else if (event.getName().equals(CommandInfo.COMMAND_NAME)) {
                CommandInfo.perform(event);
            }

            else if (event.getName().equals(CommandSetChannel.COMMAND_NAME)) {
                long oldChannelId = GuildIo.getPhasmoGuessrChannelId(event.getGuild());
                Game game = getGame(oldChannelId);
                if (game != null){
                    games.remove(oldChannelId);
                    Logger.log(LogLevel.DEBUG, LOG_PREFIX + "Game removed to set new PG-channel");
                }
                CommandSetChannel.perform(event);
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getGuild() != null
                && event.getButton().getId().startsWith("pg_")) {

            String btnId = event.getButton().getId();
            GuildMessageChannel channel = event.getGuildChannel();
            Game game = getGame(channel);

            if (btnId.equals(PgUi.btnStart.getId())) {
                Lang lang = new Lang(LANG_PREFIX, event.getInteraction());

                if (GuildIo.getPhasmoGuessrChannelId(event.getGuild()) == channel.getIdLong()) {

                    if (game != null) {
                        // Game in HashMap = running
                        event.reply(lang.getText("reply_game_already_running")).setEphemeral(true).queue();
                    } else if (restarting) {
                        event.reply("I'm in the process of restarting right now. Try again in a minute! c:")
                                .setEphemeral(true)
                                .queue();
                    } else {
                        // Start a game
                        Logger.log(LogLevel.DEBUG, LOG_PREFIX +
                                "game start triggered via button by " + event.getUser().getName() +
                                " in guild " + event.getGuild().getName());
                        event.deferEdit().queue();
                        games.put(channel.getIdLong(),
                                new Game(
                                        event.getGuildChannel(),
                                        event.getInteraction(),
                                        sumBonus(event.getGuild())
                                )
                        );
                    }
                } else {
                    event.reply(lang.getText("reply_wrong_channel")).setEphemeral(true).queue();
                }
            } else if(btnId.equals(PgUi.btnPrintAllGames.getId())) {
                if (Rank.BOT_DEV.isPermittedTo(event.getUser())) { // Only because Testers might see this button in CP
                    if (games == null) {
                        event.reply("Can't print games! HashMap is null!").setEphemeral(true).queue();
                    } else {
                        event.reply("Alright! Will print all PG instances to log.").setEphemeral(true).queue();
                        games.forEach((id, gameInstance) -> gameInstance.print("PG game with ID: " + id));
                    }
                } else {
                    event.reply("Insufficient Permission!").setEphemeral(true).queue();
                }
            } else if(btnId.equals(PgUi.btnSetGuildBonus.getId())) {
                if (Rank.BOT_TEAM.isPermittedTo(event.getUser())) { // Only because Testers might see this button in CP
                    event.replyModal(PgUi.modalAddGuildBonus).queue();
                } else {
                    event.reply("Insufficient Permission!").setEphemeral(true).queue();
                }
            } else if (game != null) {
                if (btnId.equals(PgUi.btnDebugMenu.getId())) {
                    if (Resources.BOT_ADMINS.contains(event.getUser().getIdLong())) {
                        Logger.log(LogLevel.INFORMATION, LOG_PREFIX +
                                "User accessing debug menu! U: " + event.getUser().getName() +
                                " G: " + event.getGuild().getName());
                        event.reply("").addEmbeds(game.getDebugEmbed())
                                .addActionRow(
                                        PgUi.btnDebugMode,
                                        PgUi.btnVerbose
                                        )
                                .addActionRow(
                                        PgUi.btnStart,
                                        PgUi.btnReset
                                        )
                                .addActionRow(
                                        PgUi.btnJumpRound,
                                        PgUi.btnDebugMenu
                                        )
                                .setEphemeral(true)
                                .queue();
                        Message msg = event.getMessage();
                        if (!msg.isEphemeral()) msg.delete().queue();
                    } else {
                        event.reply("Dieser Button ist nur für meine Entwickler c:").setEphemeral(true).queue();
                    }
                } else if (btnId.equals(PgUi.btnDebugMode.getId())) {

                    game.debugMode = !game.debugMode;
                    event.reply("Debugmode: " + game.debugMode).setEphemeral(true).queue();

                } else if (btnId.equals(PgUi.btnVerbose.getId())) {

                    game.debugVerbose = !game.debugVerbose;
                    event.reply("Verbose: " + game.debugVerbose).setEphemeral(true).queue();

                } else if (btnId.equals(PgUi.btnReset.getId())) {
                    if (saving || restarting) {
                        event.reply("Can't reset while saving games or restarting.").setEphemeral(true).queue();
                    } else {
                        game.reset();
                        games.remove(channel.getIdLong());
                        event.reply("Game reset.").setEphemeral(true).queue();
                    }
                } else if (btnId.equals(PgUi.btnJumpRound.getId())) {

                    game.setRound(PgSettings.MAX_ROUNDS);
                    event.reply("Jumped to round " + (PgSettings.MAX_ROUNDS)).setEphemeral(true).queue();

                }
            } else {
                event.reply("game not found").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals(PgUi.modalAddGuildBonus.getId())) {
            List<Long> values = Util.parseLongsFromModal(event);
            long guildId;
            if (values.isEmpty()) {
                event.reply("No parsable Guild ID!").setEphemeral(true).queue();
            } else {
                long bonus;
                if (values.size() == 1) {
                    guildId = values.get(0);
                    bonus = 0L;
                }
                else {
                    guildId = Long.max(values.get(0), values.get(1));
                    bonus = Long.min(values.get(0), values.get(1));
                }
                if (bonus == 0) guildBonusMap.remove(guildId);
                else guildBonusMap.put(guildId, (int) bonus);
                event.reply("Bonus set to " + bonus + " for guild with ID: " + guildId)
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.isFromGuild()) {
            Game game = getGame(event.getGuildChannel());

            if (game != null) {
                game.messageCounter(event.getGuildChannel());

                if (Util.isFromBotAdmin(event)
                        && event.getMessage().getMentions().isMentioned(event.getJDA().getSelfUser())) {
                    String msg = event.getMessage().getContentRaw();

                    if (msg.startsWith("debug")) {
                        event.getMessage().reply("x)").setActionRow(Button.secondary("pg_debugmenu", "debug menu")).queue();
                    }
                }
            }
        }
    }

    /**
     * Gets the game from the hashmap or if state is GAME_OVER removes the game.
     * @param channelId The ID of the channel
     * @return The running game or null if not present or state was GAME_OVER
     */
    public Game getGame(long channelId) {
        Game game = games.get(channelId);
        if (game != null) {
            if (game.getState() == State.GAME_OVER) {
                games.remove(channelId);
                return null;
            }
        }
        return game;
    }

    /**
     * Gets the game from the hashmap or if state is GAME_OVER removes the game.
     * If the game instance is present Game#initChannel() is called.
     * @param channel The PG channel
     * @return The running game or null if not present or state was GAME_OVER
     */
    public Game getGame(GuildMessageChannel channel) {
        Game game = getGame(channel.getIdLong());
        if (game != null) game.initChannel(channel);
        return game;
    }

    private int sumBonus(Guild guild) {
        int guildBonus = guildBonusMap.getOrDefault(guild.getIdLong(), 0);
        return guildBonus + globalBonus;
    }

    public void saveAllGames(boolean shutdown, Consumer<Boolean> consumer) {
        Logger.info(LOG_PREFIX + "Saving game instances...");
        saving = true;
        if (shutdown) restarting = true;
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        List<Long> deadGames = new ArrayList<>();
                        games.forEach((id, game) -> {
                            State state = game.getState();
                            if (state == State.GAME_OVER) {
                                deadGames.add(id);
                            } else if (state != State.GUESSING_MAP && state != State.GUESSING_ROOM) {
                                game.print(LOG_PREFIX + "Found game in a state unable to save. Will remove...");
                                deadGames.add(id);
                            }
                        });

                        for (Long id : deadGames) {
                            games.remove(id);
                        }

                        /*
                        writeObject() here...
                         */
                        writeToFile();
                        Logger.info(LOG_PREFIX + "Saved game instances.");
                        saving = false;
                        consumer.accept(true);
                    }
                },
                5000
        );
    }

    /**
     * Should not be called from outside PgManager class unless for debug reason!!!
     */
    public void loadGames() {
        Logger.info(LOG_PREFIX + "Loading game instances...");
        games = readFromFile();
        if (games == null) games = new HashMap<>();
        StatsTracker.setPgGames(games);
        restarting = false;
    }

    private void writeToFile() {
        File file = new File(PgSettings.SAVE_FILE_NAME);
        FileOutputStream fileOut;
        ObjectOutputStream oos;

        try {
            fileOut = new FileOutputStream(file);
            oos = new ObjectOutputStream(fileOut);
            oos.writeObject(games);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            Logger.fatal(LOG_PREFIX + "Saving PG failed: " + e);
        }
    }

    private HashMap<Long, Game> readFromFile() {
        File file = new File(PgSettings.SAVE_FILE_NAME);
        FileInputStream fileIn;
        ObjectInputStream ois;
        HashMap<Long, Game> loadedGames = null;

        if (file.exists()) {
            try {
                fileIn = new FileInputStream(file);
                ois = new ObjectInputStream(fileIn);
                loadedGames = (HashMap) ois.readObject();
                Logger.bot(LOG_PREFIX + "Loaded " + loadedGames.size() + " game instances from save file!");
            } catch (IOException | ClassNotFoundException e) {
                Logger.fatal(LOG_PREFIX + "Loading PG games failed: " + e);
            }
        } else {
            Logger.warn(LOG_PREFIX + "Save file does not exist. Starting with no old game instances");
        }

        return loadedGames;
    }

}