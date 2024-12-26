package at.dici.shade.userapplications.phasmoGuessr.cache;

import at.dici.shade.core.cache.AsyncMySqlCache;
import at.dici.shade.userapplications.phasmoGuessr.utils.ImageData;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

@Getter
public class PhasmoGuessrCache {
    @Getter
    private static PhasmoGuessrCache instance;

    private final AsyncMySqlCache<Integer, ImageData> phasmoGuessrCache = new AsyncMySqlCache<Integer, ImageData>() {
        @Override
        protected String query( ) {
            return "SELECT * FROM phasmoguessr;";
        }

        @Override
        protected Function<ResultSet, Map<Integer, ImageData>> function() {
            return input -> {
                Map<Integer, ImageData> map = Maps.newLinkedHashMap();

                try {
                    assert input != null;
                    while ( input.next() ) {
                        String url = input.getString( "url" );
                        int phasmomap = input.getInt( "map" );
                        String room = input.getString( "room" );
                        int imageindex = input.getInt( "imageindex" );
                        map.put( imageindex, new ImageData( url, phasmomap, room, imageindex) );
                    }
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
                return map;
            };
        }
    };

    public PhasmoGuessrCache( ) {
        PhasmoGuessrCache.instance = this;
    }

    public static void reload() {
        Logger.log(LogLevel.WARNING, "Reloading! -> PhasmoGuessrCache Old map size: " + instance.phasmoGuessrCache.size());
        instance.phasmoGuessrCache.invalidateAll();
        new PhasmoGuessrCache();
        Logger.log(LogLevel.INFORMATION, "PhasmoGuessrCache reloaded. New map size: " + instance.phasmoGuessrCache.size());
    }

    public static ImageData getRandomImage() {
        Random rnd = new Random();
        int n = rnd.nextInt((int) instance.getPhasmoGuessrCache().size());
        return instance.getPhasmoGuessrCache().get(n + 1);
    }

}
