package at.dici.shade.core.cache;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Getter
public class SettingsCache {
    @Getter
    private static SettingsCache instance;
    private final AsyncMySqlCache<String, String> settingsCache = new AsyncMySqlCache<String, String>() {
        @Override
        protected String query( ) {
            return "SELECT * FROM settings;";
        }

        @Override
        protected Function<ResultSet, Map<String, String>> function( ) {
            return input -> {
                try {
                    Map<String, String> map = Maps.newLinkedHashMap();
                    assert input != null;
                    while ( input.next() ) {
                        map.put( input.getString( "name" ), input.getString( "value" ) );
                    }
                    return map;
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
                return null;
            };
        }
    };


    public SettingsCache( ) {
        SettingsCache.instance = this;
    }

    public static String getProperty(String name) {
        return instance.getSettingsCache().get( name );
    }

}
