package at.dici.shade.userapplications.phasmoGuessr.commands;

import at.dici.shade.core.databaseio.GuildIo;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.userapplications.phasmoGuessr.Game;
import at.dici.shade.userapplications.phasmoGuessr.PgUi;
import at.dici.shade.userapplications.phasmoGuessr.State;
import at.dici.shade.utils.PhasResources;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandGuess {

    private static final String LANG_PREFIX = "userapplications.phasmoguessr.commands.CommandGuess.";
    private static final String LOG_PREFIX = "PG_GuessCommand ";
    public static final String COMMAND_NAME = "guess";
    public static SlashCommandData getCommandData(){

        OptionData maps = new OptionData(OptionType.INTEGER,"map","On which map is this?", true)
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Auf welcher Karte ist das?");

        PhasResources.maps.forEach((id, map) -> {
            if(id != PhasResources.MapData.Id.SUNNYMEADOWS_RESTRICTED) maps.addChoice(map.name, id);
        });

        OptionData room = new OptionData(OptionType.STRING,"room","In which room is the photographer?",false)
                .setDescriptionLocalization(DiscordLocale.GERMAN, "In welchem Raum ist der Fotograf?");

        return Commands.slash(COMMAND_NAME, "Guess the location in PhasmoGuessr")
                .addOptions(maps, room)
                .setGuildOnly(true)
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Gib deinen PhasmoGuessr-Tipp ab");
    }

    public static void perform(SlashCommandInteractionEvent event, Game game) {
        PgUi ui = new PgUi(new Lang(LANG_PREFIX, event));

        if (event.getChannel().getIdLong() != GuildIo.getPhasmoGuessrChannelId(event.getGuild())) {
            // wrong channel
            ui.getReplyWrongChannel(event).queue();
        } else if (game == null) {
            // no game running in this channel
            ui.getReplyNoGameRunning(event).queue();
        } else if (game.getState() == State.PENDING) {
            ui.getReplyGamePending(event).queue();
        } else if (game.getState() == State.GUESSING_MAP || game.getState() == State.GUESSING_ROOM) {
            if (event.getOptions().size() == 1 && game.getState() == State.GUESSING_ROOM) {
                // no room guess when game in room-guess-state
                ui.getReplyMapAlreadyRevealed(event).queue();
            } else {
                int mapGuess = event.getOption("map").getAsInt();
                String roomGuess = "";
                if (event.getOptions().size() == 2) {
                    // Map + Room guess
                    roomGuess = event.getOption("room").getAsString();
                }

                game.debugLog(LOG_PREFIX + "Valid guess! MapID: " + mapGuess + " Room: " + roomGuess);

                if (game.isCorrectMap(mapGuess)) {
                    game.debugLog(LOG_PREFIX + "Map Correct");
                    if (game.isCorrectRoom(roomGuess)) {
                        game.debugLog(LOG_PREFIX + "Room Correct");
                        // map + room correct
                        event.replyEmbeds(game.onRoomCorrectGuess(event.getMember()).build()).queue();
                        Logger.debug(LOG_PREFIX + "Replied...");

                    } else {
                        // only map correct

                        game.revealMatchingChars(roomGuess);
                        if (game.getState() == State.GUESSING_MAP) {
                            event.replyEmbeds(game.onOnlyMapCorrectGuess(event.getMember()).build())
                                    //.onSuccess(hook -> hook.deleteOriginal().queueAfter(PgSettings.DELAY_DELETE_REPLY_MAP_CORRECT, TimeUnit.SECONDS))
                                    .queue();
                        } else {
                            event.replyEmbeds(game.onWrongGuess(event.getMember(), roomGuess).build()).setEphemeral(true).queue();
                        }


                    }
                } else {
                    // wrong guess
                    event.replyEmbeds(game.onWrongGuess(event.getMember(), PhasResources.maps.get(mapGuess).name).build())
                            .setEphemeral(true).queue();

                }
            }

        } else {
            ui.getReplyWrongState(event).queue();
        }
    }
}
