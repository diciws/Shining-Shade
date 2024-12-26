package at.dici.shade.userapplications.phasmoQuiz;

import at.dici.shade.userapplications.phasmoQuiz.commands.PQInfo;
import at.dici.shade.userapplications.phasmoQuiz.commands.PQSetChannel;
import at.dici.shade.userapplications.phasmoQuiz.commands.PQStart;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PQManager extends ListenerAdapter {

    private static List<PQGame> games;

    public PQManager() {
        this.games = new ArrayList<>();
    }

    /**
     * Find Game by User
     * @param player
     * @return
     */

    public static PQGame findGameByUser(User player){
        for(PQGame game : games) {
            if(game.getPlayers().containsKey(player)){
                return game;
            }
        }
        return null;
    }

    /**
     * Find Game by Game ID
     *
     * @param event
     */

    // TODO

    /**
     * Create new Game on start
     * @param questions
     * @return
     */

    public PQGame startNewGame(Map<String, String> questions){
        PQGame game = new PQGame(questions);
        games.add(game);
        return game;

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.isGuildCommand()) {
            long channelId = event.getChannel().getIdLong();

            if (event.getName().equals(PQStart.COMMAND_NAME)) {
                PQStart.perform(event);
            }

            else if (event.getName().equals(PQInfo.COMMAND_NAME)) {
                PQInfo.perform(event);
            }

            else if (event.getName().equals(PQSetChannel.COMMAND_NAME)) {
                PQSetChannel.perform(event);
            }
        }
    }

}
