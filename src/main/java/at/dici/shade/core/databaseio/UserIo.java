package at.dici.shade.core.databaseio;

import at.dici.shade.core.cache.UserCache;
import at.dici.shade.core.handler.UserHandler;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.entities.User;

public class UserIo {

    public static void addPoints(User user, int points) {
        if (points != 0) {
            String id = user.getId();
            int oldPoints = UserCache.getPoints(id);

            UserHandler.userPointsHandler(id, oldPoints + points, success ->
                    Logger.log(LogLevel.INFORMATION, "User Points updated: " + user.getName() + " P: " + (oldPoints + points))
            );
        }
    }

    public static void addPoints(String id, int points) {
        if (points != 0) {
            int oldPoints = UserCache.getPoints(id);

            UserHandler.userPointsHandler(id, oldPoints + points, success ->
                    Logger.log(LogLevel.INFORMATION, "User Points updated. ID: " + id + " Oldpoints: " + oldPoints + " Newpoints: "+ points + " Points: "+ (oldPoints + points))
            );
        }
    }

    public static int getRankFlags(User user){
        return UserCache.getRank(user.getId());
    }

    public static int getRankFlags(String userId){
        return UserCache.getRank(userId);
    }

    public static void setRankFlags(String userId, int flags) {
        UserHandler.userRankHandler(userId, flags, success ->
                Logger.log(LogLevel.INFORMATION,"User Rank updated: " + userId + " Rank int: " + flags)
        );
    }

    public static void setRankFlags(User user, int flags){
        UserHandler.userRankHandler(user.getId(), flags, success ->
                Logger.log(LogLevel.INFORMATION,"User Rank updated: " + user.getName() + " Rank int: " + flags)
        );
    }

}
