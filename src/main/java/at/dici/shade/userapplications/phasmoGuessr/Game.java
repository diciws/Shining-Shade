package at.dici.shade.userapplications.phasmoGuessr;

import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.core.databaseio.UserIo;
import at.dici.shade.userapplications.phasmoGuessr.cache.PhasmoGuessrCache;
import at.dici.shade.userapplications.phasmoGuessr.utils.GameResults;
import at.dici.shade.userapplications.phasmoGuessr.utils.ImageData;
import at.dici.shade.userapplications.phasmoGuessr.utils.PgUtil;
import at.dici.shade.utils.PhasResources;
import at.dici.shade.utils.doofutil.BaseLong;
import at.dici.shade.utils.doofutil.Disguiser;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
The Core of PhasmoGuessr

Each Game running on any Server will run a separate instance of this class!

 */

public class Game implements Serializable {
    @Serial
    private static final long serialVersionUID = 100001L;
    //private int gameVersion = 0;
    static final String LOG_PREFIX = "PG_Game: ";
    private static final String LANG_PREFIX = "userapplications.phasmoguessr.Game.";
    private final Lang lang;
    private transient PgUi ui;
    boolean debugMode = false;
    boolean debugVerbose = false;
    private ImageData currentImage;
    private transient GuildMessageChannel channel;
    private transient Message currentMessage;
    private transient JDA jda; // only used for command mention yet
    private State state;
    private int round;
    private int messagesSinceImage;
    private final HashMap<String, Player> players;
    private final List<Integer> usedImages;
    private int wrongGuesses;
    private int roomCharsRevealed;
    private long timeOfWrongGuessesIncrement;
    private transient boolean shouldSendNewImageInfo;
    private transient int bonus;

    /**
     * Starts a new game in given channel <br>
     * Sets Game.state to PENDING <br>
     * Sends the first Image-Message after 1 Second <br>
     * Game.state will be set to GUESSING_MAP after Message arrived. <br>
     * @param channel The channel the game shall run in
     */
    public Game(GuildMessageChannel channel, Interaction interaction, int bonus){
        Logger.info( LOG_PREFIX + "Starting a new Game...");
        lang = new Lang(LANG_PREFIX, interaction);
        ui = new PgUi(lang);
        this.channel = channel;
        jda = channel.getJDA();
        players = new HashMap<>();
        usedImages = new ArrayList<>();
        reset();
        this.bonus = bonus;
        sendNewImage().queueAfter(1,TimeUnit.SECONDS, null, createErrorHandler());
        Logger.detailedLog(LogLevel.INFORMATION,
                LOG_PREFIX + "Starting process complete" ,
                LOG_PREFIX + "Started new game:\n" + getEnvironmentInfo());
    }

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        // for later game updates
        oos.defaultWriteObject();
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // for later game updates
        ois.defaultReadObject();
        ui = new PgUi(lang);
        bonus = 0;
    }


    /**
     * Resets all values of this game without starting a new one nor sending a new image. <br>
     * Game.state will be set to PENDING.
     */
    public void reset() {
        debugLog("reset()");
        state = State.PENDING;
        if (currentMessage != null)
            currentMessage.editMessageEmbeds(
                    currentMessage.getEmbeds()
                            .isEmpty() ? currentMessage.getEmbeds() : List.of(currentMessage.getEmbeds().get(0))
            ).queue(
                    m -> currentMessage = null, // wtf this isn't error handling
                    t -> currentMessage.delete().queue(
                            dm -> currentMessage = null,
                            tm -> currentMessage = null
                    )
            );
        messagesSinceImage = 0;
        players.clear();
        usedImages.clear();
        round = 0;
        wrongGuesses = 0;
        roomCharsRevealed = 0;
        timeOfWrongGuessesIncrement = 0L;
        shouldSendNewImageInfo = false;
    }

    /**
     * Sets the channel and JDA only if it has not been set yet.
     * @param channel The channel to use
     */
    public void initChannel(GuildMessageChannel channel) {
        if (this.channel == null) this.channel = channel;
        if (jda == null) jda = channel.getJDA();
    }

    /**
     * Manages the game after the map has been correctly guessed. <br>
     * Only to be called if the Map was NOT REVEALED BEFORE and the room was not correctly guessed at the same time.
     * @param guesser The User who made the guess
     * @return An EmbedBuilder containing the reply message
     */
    public EmbedBuilder onOnlyMapCorrectGuess(Member guesser) {
        debugLog("onOnlyMapCorrectGuess");
        state = State.GUESSING_ROOM;

        Player player = getPlayer(guesser);
        int reward = PgUtil.calcRewardMap(player.getFails(), bonus);
        player.addPoints(reward);
        player.resetFails();

        // Add Mapname to image-message
        sendImgInfoEmbed(false);

        return addDebugFooter(ui.getEmbedCorrectMap(
                guesser.getAsMention(), reward, currentImage.phasmoMapId
        ));
    }

    public EmbedBuilder onRoomCorrectGuess(Member guesser) {
        debugLog("onRoomCorrectGuess()");
        EmbedBuilder reply = ui.getEmbedCorrectRoom();

        Player player = getPlayer(guesser);

        int reward;
        if (state == State.GUESSING_MAP) {
            // Map + Room in one guess

            reward = PgUtil.calcRewardRoom(player.getFails(), true, bonus);

            ui.modifyEmbedCorrectBoth(reply, guesser.getAsMention(), reward);

        } else {
            // Map was revealed before

            reward = PgUtil.calcRewardRoom(player.getFails(), false, bonus);

            ui.modifyEmbedCorrectOnlyRoom(reply, guesser.getAsMention(), reward);
        }

        // Add Map + Room to image-message
        sendImgInfoEmbed(true);
        Logger.debug(LOG_PREFIX + "sendImgInfoEmbed() complete");

        player.addPoints(reward);

        if (nextRound()) {
            ui.modifyEmbedWaitingForNextImg(reply);
        }
        players.forEach((id, p) -> p.resetFails()); // reset fails for all players
        if (currentMessage != null && !currentMessage.getEmbeds().isEmpty()) {
            currentMessage.editMessageEmbeds(currentMessage.getEmbeds().get(0))
                    .queueAfter(PgSettings.DELAY_REMOVE_INFO_FROM_IMAGE, TimeUnit.SECONDS,
                            null, // No need for error handling at this point.
                            t -> {
                            }
                    );
        }
        Logger.debug(LOG_PREFIX + "onRoomCorrectGuess() complete");
        return addDebugFooter(reply);
    }

    public EmbedBuilder onWrongGuess(Member guesser, String guess) {
        debugLog("onWrongGuess()");

        getPlayer(guesser).failed();

        if (timeOfWrongGuessesIncrement < System.currentTimeMillis() - PgSettings.COOLDOWN_COUNT_WRONG_GUESSES) {
            if (debugMode) Logger.info(LOG_PREFIX + "Incrementing wrong guesses.");
            wrongGuesses++;
            timeOfWrongGuessesIncrement = System.currentTimeMillis();


            if (state == State.GUESSING_MAP && wrongGuesses >= PgSettings.MAX_MAP_FAILS_GAME) {
                // Add Mapname to image-message
                sendImgInfoEmbed(false);
                state = State.GUESSING_ROOM;
                wrongGuesses = 0;
                players.forEach((id, player) -> player.resetFails()); // reset fails for all players
            } else if (state == State.GUESSING_ROOM) {
                if (wrongGuesses >= PgSettings.MAX_ROOM_FAILS_GAME) {

                    if (roomCharsRevealed < currentImage.room.length() - PgSettings.CHARS_TO_NOT_REVEAL) {
                        // Only reveal one more letter, if the string.length allows it

                        roomCharsRevealed++;
                        sendImgInfoEmbed(false);
                    } else if (wrongGuesses > (
                            currentImage.room.length()
                                    + PgSettings.MAX_ROOM_FAILS_GAME
                                    + PgSettings.MAX_FAILS_AFTER_FULL_REVEAL)) {

                        EmbedBuilder messageTooManyFails = ui.getEmbedTooManyFails();

                        if (nextRound()) {
                            ui.modifyEmbedWaitingForNextImg(messageTooManyFails);
                        }

                        channel.sendMessageEmbeds(
                                messageTooManyFails.build()
                        ).queue();

                    }
                } else {
                    // In case part of guess was correct:
                    if (shouldSendNewImageInfo) sendImgInfoEmbed(false);
                }
            }
        }

        return ui.getEmbedWrongGuess(guess);
    }

    /**
     * Sends Typing and then the next image which resets some values.
     * Will trigger game finish if max rounds are reached.
     * @return false if max rounds are reached
     */
    boolean nextRound() {
        state = State.ROUND_FINISHED;
        players.forEach((id, player) -> player.resetFails()); // reset fails for all players

        if (round >= PgSettings.MAX_ROUNDS) {
            finishGame();
            return false;
        }
        else if (channel != null) {
            channel.sendTyping().queueAfter(400, TimeUnit.MILLISECONDS,
                    null, // No need to handle error on typing
                    t -> {
                    }
            );
            sendNewImage().queueAfter(PgSettings.DELAY_BETWEEN_ROUNDS, TimeUnit.SECONDS,
                    null,
                    createErrorHandler()
            );

        } else {
            Logger.error(LOG_PREFIX + "Game over because channel is null in nextRound()");
            reset();
            state = State.GAME_OVER;
        }
        return true;
    }

    void finishGame() {
        debugLog("finishGame()");
        // queueAfter 1-2 secs!!!
        state = State.GAME_OVER;

        GameResults results = new GameResults(players);
        payOut(results);

        MessageEmbed rankingEmbed = ui.getEmbedRanking(results);


        round = 0;
        players.clear();
        usedImages.clear();
        if (channel != null) {
            channel.sendTyping().queue(
                    s -> channel.sendMessageEmbeds(rankingEmbed)
                            .addActionRow(Button.success(PgSettings.BUTTON_ID_START, lang.getText("btnlabel_start_new_round")))
                            .queueAfter(2L, TimeUnit.SECONDS, null, createErrorHandler()),
                    t -> {
                    });
        } else {
            Logger.fatal("Could not send game results because channel is null!!");
        }
    }

    private void payOut(GameResults results) {
        int bonusSpreadCounter = 0;
        for (Player player : results.getFilteredPlayers()) {
            int bonus;
            if (results.getBonusSpread() > bonusSpreadCounter++) {
                bonus = results.getWinnerBonus();
            } else {
                bonus = 0;
            }
            UserIo.addPoints(player.getId(), player.getPoints() + bonus);
        }
    }

    RestAction<Message> sendNewImage() {
        debugLog("sendNewImage()");
        round++;
        wrongGuesses = 0;
        messagesSinceImage = 0;
        roomCharsRevealed = 0;
        do {
            currentImage = PhasmoGuessrCache.getRandomImage();
        } while (usedImages.contains(currentImage.imageindex));
        usedImages.add(currentImage.imageindex);

        return sendImage(true);
    }

    RestAction<Message> sendImage(boolean setStateOnSuccess) {
        debugLog("sendImage()");
        String id = BaseLong.toString(PgSettings.IMG_ID_CHARSET, Disguiser.encode(currentImage.imageindex, 10));
        MessageEmbed embed = createImageEmbed(id);

        if (channel == null) {
            Logger.fatal(LOG_PREFIX + "Could not send new image because channel is null!! This should never happen!");
            state = State.GAME_OVER;
        }

        return channel.sendMessageEmbeds(embed).onSuccess((message) -> {
            currentMessage = message;
            List<MessageEmbed> embeds = message.getEmbeds();
            if (embeds.isEmpty()) {
                print("Sent message is missing the embedded image!");
                message.getChannel()
                        .sendMessage("Error! Failed to send next picture. :( Please make sure to grant me the needed permissions. Game over.")
                        .queue(null, t -> {}); // No need for Errorhandling!
                state = State.GAME_OVER;
            } else if (embeds.get(0).getImage() == null) {
                Logger.log(LogLevel.WARNING, LOG_PREFIX + "Failed to send image! " + id);
                message.editMessageEmbeds(createImageEmbed(id + " (r)")).queueAfter(3L, TimeUnit.SECONDS,
                        m -> {
                            if (!m.getEmbeds().isEmpty() && m.getEmbeds().get(0).getImage() != null){
                                Logger.info(LOG_PREFIX + "Re-send successful. " + id);
                                state = State.GUESSING_MAP;
                            } else {
                                Logger.warn(LOG_PREFIX + "Failed to re-send image! Game over. " + id);
                                state = State.GAME_OVER;
                                message.getChannel()
                                        .sendMessage("Sorry! Looks like I am having some problems" +
                                                " sending the next picture. Try to restart the game...")
                                        .addActionRow(
                                                Button.success(PgSettings.BUTTON_ID_START,
                                                        lang.getText("btnlabel_start_new_round")))
                                        .queue(null, createErrorHandler());
                            }
                        },
                        createErrorHandler()
                );
            } else if (setStateOnSuccess) {
                state = State.GUESSING_MAP;
            }
        });
    }

    /**
     * creates the embed with the current image. Should not be used for reposting to save performance!
     * @param id The encrypted Image-ID (This param could be removed if needed. A new ID can be calculated anytime!)
     * @return The complete embed.
     */
    private MessageEmbed createImageEmbed(String id) {

        EmbedBuilder embed = ui.getEmbedImage(id, jda, round, currentImage.url);

        addDebugFooter(embed);

        return embed.build();
    }

    private void sendImgInfoEmbed(boolean showRoom) {
        debugLog("sendImgInfoEmbed()");
        shouldSendNewImageInfo = false;
        if (currentMessage != null) {
            // check if embeds were deleted by user
            List<MessageEmbed> embeds = currentMessage.getEmbeds();
            if (!embeds.isEmpty()) {
                State storedState = state;
                state = State.PENDING;
                EmbedBuilder embed = new EmbedBuilder()
                        .setDescription(
                                "Map: **[" + PhasResources.maps.get(currentImage.phasmoMapId).name +
                                        "](" + PhasResources.maps.get(currentImage.phasmoMapId).link + ")**")
                        .setColor(PgSettings.EmbedColor.REVEALED_MAP);
                if (showRoom) embed.appendDescription(" - **" + currentImage.room + "**");
                else if (roomCharsRevealed > 0) {
                    String roomTip = currentImage.room.substring(0, roomCharsRevealed)
                            + (currentImage.room
                            .substring(roomCharsRevealed))
                            .replaceAll("[^ ]", "_");
                    embed.appendDescription(" - **`" + roomTip + "`**");
                }
                currentMessage.editMessageEmbeds(embeds.get(0), embed.build())
                        .queue(
                                m -> {
                                    currentMessage = m;
                                    state = storedState;
                                },
                                createErrorHandler());
            }
        }
    }

    public boolean isCorrectMap(int mapId){
        debugLog("isCorrectMap() Guessed MapID: " + mapId + " CurrentImage.mapId: " + currentImage.phasmoMapId);
        return mapId == currentImage.phasmoMapId;
    }

    public boolean isCorrectRoom(String guess){
        debugLog("isCorrectRoom() Guessed Room: " + guess + " currentImage.room: " + currentImage.room);
        String regexToRemove = "\\(.*\\)|\\W";
        return guess.replaceAll(regexToRemove, "")
                .equalsIgnoreCase(currentImage.room.replaceAll(regexToRemove,""));
    }

    public void revealMatchingChars(String guess) {
        debugLog("revealMatchingChars()");
        if (!guess.isEmpty()) {
            if (roomCharsRevealed < currentImage.room.length() - PgSettings.CHARS_TO_NOT_REVEAL) {
                // Only reveal chars, if the string.length allows it
                int i;
                for (i = 0; i < guess.length() - 1 && i < currentImage.room.length() - 1; i++) {
                    if (guess.toLowerCase(Locale.ROOT).charAt(i) != currentImage.room.toLowerCase(Locale.ROOT).charAt(i)) {
                        break;
                    }
                }

                if (roomCharsRevealed < i) {
                    wrongGuesses += (i - roomCharsRevealed - 1);
                    roomCharsRevealed = i;
                    shouldSendNewImageInfo = true;
                }
            }
        }
    }

    private Player getPlayer(Member member) {
        players.putIfAbsent(member.getId(), new Player(member));
        return players.get(member.getId());
    }

    public State getState(){
        return state;
    }

    public void setRound(int r){
        round = r;
    }

    /**
     * checks the amount of messages sent after currentMessage
     * Re-sends the Image if needed
     */
    public void messageCounter(GuildMessageChannel msgChannel){
        if (state == State.GUESSING_MAP || state == State.GUESSING_ROOM) {
            messagesSinceImage++;
            if (messagesSinceImage == PgSettings.MESSAGES_BEFORE_REFRESH) {
                if (channel == null) channel = msgChannel;
                debugLog("Message Limit reached!");
                messagesSinceImage = 0;
                if (currentMessage != null) {
                    channel.sendMessageEmbeds(currentMessage.getEmbeds()).onSuccess(m -> {
                        currentMessage.delete().queue();
                        currentMessage = m;
                    }).queue();
                } else {
                    sendImage(false).queue();
                }
            }
        }
    }

    private ErrorHandler createErrorHandler() {

        String channelName;
        String guildName;
        if (channel != null) channelName = channel.getName();
        else channelName = "[Unknown]";
        if (currentMessage == null) guildName = "[Unknown]";
        else guildName = currentMessage.getGuild().getName();
        String gameInfo = "Guild: " + guildName + " Channel: " + channelName;

        return new ErrorHandler()
                .handle(ErrorResponse.CANNOT_SEND_TO_USER,
                        e -> {
                            Logger.log(LogLevel.ERROR, LOG_PREFIX +
                                    "Trying to send game message in DM. Should never happen. Wtf? Game over. " +
                                    gameInfo);
                            reset();
                            state = State.GAME_OVER;
                        })
                .handle(ErrorResponse.UNKNOWN_CHANNEL,
                        e -> {
                            Logger.log(LogLevel.ERROR, LOG_PREFIX +
                                    "Trying to send in unknown (deleted) channel. Game over." +
                                    gameInfo);
                            reset();
                            state = State.GAME_OVER;
                        })
                .handle(ErrorResponse.MISSING_ACCESS,
                        e -> {
                            Logger.log(LogLevel.WARNING, LOG_PREFIX +
                                    "Missing access to message! Game over. Sending error message to channel..." +
                                    gameInfo);
                            if (channel != null)
                                channel
                                    .sendMessage("Lost access to preceding PhasmoGuessr message. Game reset.")
                                    .addActionRow(Button.success("pg_start", "Start PhasmoGuessr"))
                                    .queue(null, t -> {});
                            reset();
                            state = State.GAME_OVER;
                        })
                .handle(ErrorResponse.UNKNOWN_MESSAGE,
                        e -> {
                            Logger.log(LogLevel.WARNING, LOG_PREFIX +
                                    "Unknown message (deleted). Game over. Sending error message to channel..." +
                                    gameInfo);
                            if (channel != null)
                                channel
                                        .sendMessage("Could not find preceding PhasmoGuessr message. Game reset.")
                                        .addActionRow(Button.success("pg_start", "Start PhasmoGuessr"))
                                        .queue(null, t -> {});
                            reset();
                            state = State.GAME_OVER;
                        });
    }

    EmbedBuilder addDebugFooter(EmbedBuilder embed) {
        if (debugMode){
            return embed.setFooter(getGameInfo() + "\n" + getPlayerInfo());
        } else return embed;
    }

    public void debugLog(String msg){
        if (debugMode) {
            Logger.log(LogLevel.INFORMATION, LOG_PREFIX + msg);
            if (debugVerbose) {
                print(LOG_PREFIX + " - VERBOSE -");
            }
        }
    }

    void print(String msg) {
        Logger.detailedLog(LogLevel.URGENT,
                LOG_PREFIX + msg,
                LOG_PREFIX + msg + "\n" + getEnvironmentInfo() + "\n" + getGameInfo() + "\n" + getPlayerInfo());
    }
    MessageEmbed getDebugEmbed(){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Debug Info")
                .appendDescription(getEnvironmentInfo())
                .appendDescription("\n" + getGameInfo());

        if (players.size() > 15) embed.appendDescription("[Too many players to display]");
        else embed.appendDescription("\n" + getPlayerInfo());

        return embed.build();
    }

    String getEnvironmentInfo() {
        StringBuilder infoBuilder = new StringBuilder();

        infoBuilder.append("---- ENVIRONMENT ----")
                .append("\nGuild: ");

        if (currentMessage != null) infoBuilder.append(currentMessage.getGuild().getName());
        else if (channel != null) infoBuilder.append(channel.getGuild().getName());
        else infoBuilder.append("n/a");

        infoBuilder.append("\nChannel: ")
                .append(channel == null ? "n/a" : channel.getName())
                .append("\nDebug: ").append(debugMode)
                .append(" Verbose: ").append(debugVerbose)
                .append("\nLanguage: ").append(lang.getLocale())
                .append("\nBonus: ").append(bonus);

        return infoBuilder.toString();
    }

    String getGameInfo() {
        StringBuilder infoBuilder = new StringBuilder();

        infoBuilder
                .append("------ GAME INFO ------")
                .append("\nState: ").append(state.name())
                .append("\nRound: ").append(round)
                .append("\nMsg since image: ").append(messagesSinceImage)
                .append("\nwGuesses: ").append(wrongGuesses)
                .append(" -> Passed millis: ").append(System.currentTimeMillis() - timeOfWrongGuessesIncrement)
                .append("\nRevealed chars: ").append(roomCharsRevealed)
                .append("\nUsed Images: ").append(usedImages)
                .append("\n------ IMG INFO ------");
        if (currentImage == null) {
            infoBuilder.append("\nn/a");
        } else {
            infoBuilder.append("\nIndex: ").append(currentImage.imageindex)
                    .append("\nMap: [ID: ").append(currentImage.phasmoMapId)
                    .append("] -> ").append(PhasResources.maps.get(currentImage.phasmoMapId).name)
                    .append("\nRoom: ").append(currentImage.room);
        }

        return infoBuilder.toString();
    }

    String getPlayerInfo() {
        StringBuilder infoBuilder = new StringBuilder("---- PLAYER INFO ----");
        players.forEach((id, p) ->
                infoBuilder.append("\nP: ").append(p.getPoints())
                        .append(" F: ").append(p.getFails())
                        .append(" User: ").append(p.getName()));
        return infoBuilder.toString();
    }

}
