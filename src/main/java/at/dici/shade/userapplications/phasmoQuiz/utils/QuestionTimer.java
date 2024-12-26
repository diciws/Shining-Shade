package at.dici.shade.userapplications.phasmoQuiz.utils;

import at.dici.shade.userapplications.phasmoQuiz.PQGame;
import at.dici.shade.userapplications.phasmoQuiz.enums.GameType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.TimerTask;

public class QuestionTimer extends TimerTask {

    private int seconds = 0;
    private PQGame current;
    private TextChannel channel;

    public QuestionTimer(PQGame current, TextChannel channel) {
        this.current = current;
        this.channel = channel;
    }

    @Override
    public void run() {
        if(PQGame.gameType == GameType.STOPPING){
            cancel();
        }

        if(seconds == 30){
            //get all answers from player and clear them

            //send new question

        }


    }
}
