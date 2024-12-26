package at.dici.shade.core.handler;

import at.dici.shade.core.cache.UserCache;
import at.dici.shade.core.mysql.MySqlClass;
import at.dici.shade.core.utils.UserUtil;

import java.util.function.Consumer;

public class UserHandler {
    public static boolean userAsync( String id, String permission, Consumer<Boolean> consumer ) {
        if ( UserCache.getInstance().isUser( id ) )
            return false;

        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datum = sdf.format(dt);

        int points = 0;
        int dailys = 0;
        int rank = 0;

        MySqlClass.getInstance().updateAsync( "INSERT INTO users (ID, Datum, Permission, Rank, Punkte, Dailys) VALUES (?, ?, ?, ?, ?, ?);",
                success -> {
                    if ( success )
                        UserCache.getInstance().cacheUser( new UserUtil( id, datum, permission, rank, points, dailys) );
                    consumer.accept( success );
                }, id, datum, permission, rank, points, dailys);

        return true;
    }

    public static boolean userPointsHandler( String id, int points, Consumer<Boolean> consumer){
        UserCache.userRegistry( id );

        MySqlClass.getInstance().updateAsync("UPDATE users SET Punkte = ? WHERE id = ?;",
                success -> {
                    if ( success )
                        UserCache.getInstance().cacheUser( new UserUtil( id, UserCache.getDate(id), UserCache.getPermission(id), UserCache.getRank(id), points, UserCache.getDailys(id)) );
                    consumer.accept( success );
                }, points, id);

//        UserCache.getInstance().addIfNotPresent(id, // otherwise points could get lost on first use
//                s -> MySqlClass.getInstance().updateAsync("UPDATE users SET Punkte = ? WHERE id = ?;",
//                        success -> {
//                            if ( success )
//                                UserCache.getInstance().cacheUser( new UserUtil( id, UserCache.getDate(id), UserCache.getPermission(id), points) );
//                            consumer.accept( success );
//                        }, points, id));

        return true;

    }

    public static boolean userRankHandler( String id, int rank, Consumer<Boolean> consumer){
        UserCache.userRegistry( id );

        MySqlClass.getInstance().updateAsync("UPDATE users SET Rank = ? WHERE id = ?;",
                success -> {
                    if ( success )
                        UserCache.getInstance().cacheUser( new UserUtil( id, UserCache.getDate(id), UserCache.getPermission(id), rank, UserCache.getPoints(id), UserCache.getDailys(id)) );
                    consumer.accept( success );
                }, rank, id);

        return true;

    }

    public static void removeUser( String id, Consumer<Boolean> consumer ) {
        if ( !UserCache.getInstance().isUser( id ) ) {
            consumer.accept(true);
        } else {
            MySqlClass.getInstance().updateAsync("DELETE from users WHERE id = ?;",
                    success -> {
                        if (success) UserCache.getInstance().removeUser(id);
                        consumer.accept(success);
                    }, id);
        }
    }



}
