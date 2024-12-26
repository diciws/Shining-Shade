package at.dici.shade.userapplications.phasmoGuessr.utils;

import at.dici.shade.userapplications.phasmoGuessr.PgSettings;

public class PgUtil {

    /**
     * Calculates the reward for guessing the Map. <br>
     * The higher the fails the lower the reward.
     * @param fails The amount of times the User made a wrong guess in this round
     * @return The points gained from this single guess
     */
    public static int calcRewardMap(int fails, int bonus) {
        int reward = rawMapReward(fails);
        return addBonus(reward, bonus);
    }

    private static int rawMapReward(int fails) {
        if(fails == 0) return PgSettings.POINTS_MAX_MAP;
        else if (fails <= PgSettings.MAX_MAP_FAILS_PLAYER) return PgSettings.POINTS_MAX_MAP - 1 - fails;
        else return PgSettings.POINTS_MIN_MAP;
    }

    /**
     * Calculates the reward for guessing the Room. <br>
     * The higher the fails the lower the reward. <br>
     * Guessing Map and Room in one will give a Bonus. <br>
     * If both are guessed in one only this method shall be used.
     * @param fails The amount of time the User made a wrong guess in this round or if the map name was revealed before since then
     * @param bothInOneGuess true if map and room were guessed correctly in one
     * @return The points gained from this single guess
     */
    public static int calcRewardRoom(int fails, boolean bothInOneGuess, int bonus) {
        int reward = rawRoomReward(fails, bothInOneGuess);
        return addBonus(reward, bonus);
    }

    private static int rawRoomReward(int fails, boolean bothInOneGuess) {
        int reward = 0;
        if (bothInOneGuess){
            reward = PgSettings.POINTS_BONUS_FOR_BOTH_IN_ONE + calcRewardMap(fails, 0);
            fails = 0;
        }
        if (fails == 0) reward += PgSettings.POINTS_MAX_ROOM;
        else if (fails <= PgSettings.MAX_ROOM_FAILS_PLAYER) reward += PgSettings.POINTS_MAX_ROOM - 2 - fails;
        else reward += PgSettings.POINTS_MIN_ROOM;
        return reward;
    }

    private static int addBonus(int rawReward, int bonus) {
        if (bonus == 0) return rawReward;
        return rawReward + ((bonus * rawReward) / 100);
    }
}
