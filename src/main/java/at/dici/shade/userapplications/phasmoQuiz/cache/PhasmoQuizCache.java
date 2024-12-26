package at.dici.shade.userapplications.phasmoQuiz.cache;

import at.dici.shade.core.cache.AsyncMySqlCache;
import at.dici.shade.userapplications.phasmoQuiz.utils.PQUtil;
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
public class PhasmoQuizCache {
    @Getter
    private static PhasmoQuizCache instance;

    private final AsyncMySqlCache<Integer, PQUtil> phasmoQuizCache = new AsyncMySqlCache<Integer, PQUtil>() {
        @Override
        protected String query( ) {
            return "SELECT * FROM phasmoquiz;";
        }

        @Override
        protected Function<ResultSet, Map<Integer, PQUtil>> function() {
            return input -> {
                Map<Integer, PQUtil> map = Maps.newLinkedHashMap();

                try {
                    assert input != null;
                    while ( input.next() ) {
                        int id = input.getInt( "id" );
                        String question_title = input.getString( "question_title" );
                        String question_desc = input.getString( "question_desc" );
                        map.put( id, new PQUtil( id, question_title, question_desc ) );
                    }
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
                return map;
            };
        }
    };

    public PhasmoQuizCache( ) {
        PhasmoQuizCache.instance = this;
    }

    public static void reload(){
        Logger.log(LogLevel.WARNING, "Reloading! -> PhasmoQuizCache Mapsize: "+instance.phasmoQuizCache.size());
        instance.phasmoQuizCache.invalidateAll();
        new PhasmoQuizCache();
        Logger.log(LogLevel.INFORMATION, "UserCache reloaded.");
    }

    public static PQUtil getRandomeQuestion() {
        Random rnd = new Random();
        int n = rnd.nextInt((int) instance.getPhasmoQuizCache().size());
        return instance.getPhasmoQuizCache().get(n + 1);
    }

}

