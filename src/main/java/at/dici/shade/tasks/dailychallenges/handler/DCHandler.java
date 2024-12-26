package at.dici.shade.tasks.dailychallenges.handler;

import at.dici.shade.core.cache.UserCache;
import at.dici.shade.core.mysql.MySqlClass;
import at.dici.shade.core.utils.UserUtil;
import at.dici.shade.tasks.dailychallenges.cache.DCCache;
import at.dici.shade.tasks.dailychallenges.utils.DCUtils;

import java.util.function.Consumer;

public class DCHandler {

    public static boolean asyncdailyChallenge( int id, String state, Consumer<Boolean> consumer ) {

        MySqlClass.getInstance().updateAsync("UPDATE dailychallenges SET challenge_state = ? WHERE id = ?;",
                success -> {
                    if ( success )
                        DCCache.getInstance().cacheDC( new DCUtils( id, DCCache.getDailyChallengetitle(id), state));
                    consumer.accept( success );
                }, state, id);

        return true;
    }

    public static boolean userDailyCHHandler( String id, int dailys, Consumer<Boolean> consumer){
        UserCache.userRegistry( id );

        MySqlClass.getInstance().updateAsync("UPDATE users SET Dailys = ? WHERE id = ?;",
                success -> {
                    if ( success )
                        UserCache.getInstance().cacheUser( new UserUtil( id, UserCache.getDate(id), UserCache.getPermission(id), UserCache.getRank(id), UserCache.getPoints(id), dailys) );
                    consumer.accept( success );
                }, dailys, id);

        return true;

    }


}
