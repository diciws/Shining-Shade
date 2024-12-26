package at.dici.shade.core.cache;

import at.dici.shade.core.handler.UserHandler;
import at.dici.shade.core.utils.UserUtil;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Getter
public class UserCache {
    @Getter
    private static UserCache instance;

    private final AsyncMySqlCache<String, UserUtil> userCache = new AsyncMySqlCache<String, UserUtil>() {
        @Override
        protected String query( ) {
            return "SELECT * FROM users;";
        }

        @Override
        protected Function<ResultSet, Map<String, UserUtil>> function( ) {
            return input -> {
                Map<String, UserUtil> map = Maps.newLinkedHashMap();

                try {
                    assert input != null;
                    while ( input.next() ) {
                        String clientid = input.getString( "id" );
                        String datum = input.getString( "datum" );
                        String permission = input.getString( "permission" );
                        int rank = input.getInt( "rank" );
                        int points = input.getInt( "punkte" );
                        int dailyc = input.getInt( "dailys" );
                        map.put( clientid, new UserUtil( clientid, datum, permission, rank, points, dailyc) );
                    }
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
                return map;
            };
        }
    };
    public UserCache( ) {
        UserCache.instance = this;
    }

    public long CacheSize( ){
        return this.getUserCache().size();
    }

    public static void reload(){
        Logger.log(LogLevel.WARNING, "Reloading! -> UserCache Mapsize: "+instance.userCache.size()+ "-> Parameters -> "+instance.userCache.parameters());
        instance.userCache.invalidateAll();
        new UserCache();
        Logger.log(LogLevel.INFORMATION, "UserCache reloaded.");
    }

    public boolean isUser( String id ) {
        UserUtil user = this.getUserCache().getIfPresent( id );
        return  user != null;
    }

    public static UserUtil getPresentUser(String id){
        UserUtil user = instance.getUserCache().getIfPresent( id );
        return user;
    }

    public static void userRegistry( String id ){
        UserUtil user = instance.getUserCache().getIfPresent( id );
        if(user == null){
            UserHandler.userAsync( id, "User", success ->
				Logger.log(LogLevel.INFORMATION,"Registry new User success, User-ID: " + id)
		    );
        }
    }

    public void cacheUser(UserUtil user) {
        this.getUserCache().put( user.getId(), user);
    }

    public void removeUser( String id ) {
        userCache.invalidate( id );
    }

    public static int getPoints(String id){
        if(getPresentUser( id ) == null){
            return 0;
        }
        else return instance.getUserCache().get(id).getPoints();
    }

    public static String getDate(String id){
        if(getPresentUser( id ) == null){
            return "now";
        }
        else return instance.getUserCache().get(id).getDatum();
    }

    public static String getPermission(String id){
        if(getPresentUser( id ) == null){
            return "User";
        }
        else return instance.getUserCache().get(id).getPermission();
    }

    public static int getRank(String id){
        if(getPresentUser( id ) == null){
            return 0;
        }
        else return instance.getUserCache().get(id).getRank();
    }

    public static int getDailys(String id){
        if(getPresentUser( id ) == null){
            return 0;
        }
        else return instance.getUserCache().get(id).getDailyc();
    }

}
