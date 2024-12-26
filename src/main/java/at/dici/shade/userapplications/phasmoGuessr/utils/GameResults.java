package at.dici.shade.userapplications.phasmoGuessr.utils;

import at.dici.shade.userapplications.phasmoGuessr.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GameResults {
    HashMap<String, Player> players;
    List<Player> filteredPlayers = new ArrayList<>();
    int winnerBonus;
    int spread;

    public GameResults(HashMap<String, Player> players) {
        this.players = players;
        filterPlayers();
        filteredPlayers.sort(Collections.reverseOrder());
        if (filteredPlayers.isEmpty()) { // should not happen
            winnerBonus = 0;
            spread = 0;
        } else {
            calcSpread();
            calcWinnerBonus();
        }
    }

    public List<Player> getFilteredPlayers() {
        return filteredPlayers;
    }

    public int getWinnerBonus() {
        return winnerBonus;
    }

    public int getBonusSpread() {
        return spread;
    }

    private void filterPlayers() {
        for (Player player : players.values()) {
            if (player.getPoints() > 0) {
                filteredPlayers.add(player);
            }
        }
    }


    private void calcWinnerBonus() {
        // bonus = (points / 3) * (n - 1)
        int n = players.size();
        int p = filteredPlayers.get(0).getPoints();
        int fullBonus = (p / 3) * (n - 1);
        winnerBonus = fullBonus / spread;
    }

    private void calcSpread() {
        int points = filteredPlayers.get(0).getPoints();
        spread = 0;
        for (Player player : filteredPlayers) {
            if (points == player.getPoints()) spread++;
            else break;
        }
    }

}
