package at.dici.shade.userapplications.phasmoQuiz;

import at.dici.shade.userapplications.phasmoQuiz.enums.GameType;
import at.dici.shade.userapplications.phasmoQuiz.utils.QuestionTimer;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PQGame {
    public static GameType gameType;

    public QuestionTimer timer;
    public Map<User, Integer> players;
    public List<User> givenAnswerUsers;
    public List<String> questionAlreadyDone;
    private Map<String, String> gameQuestions;
    private String messageID;
    private String currentAnswer;


    //Game, Questions
    public PQGame(Map<String, String> questions) {

        this.players = new LinkedHashMap<>();
        this.givenAnswerUsers = new ArrayList<>();
        this.questionAlreadyDone = new ArrayList<>();
        this.gameQuestions = questions;
        this.timer = new QuestionTimer(this, null);

        setGameType(GameType.WAITING);

    }

    public Map<User, Integer> getPlayers() {
        return players;
    }

    public void removePlayer(User user) {
        players.remove(user);
    }

    public void addPlayer(User player) { players.put(player, 0); }

    public GameType getGameType(){
        return gameType;
    }

    public void setGameType(GameType state){
        gameType = state;
    }

    public QuestionTimer getTimer() {
        return timer;
    }

}
