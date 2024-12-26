package at.dici.shade.tasks.dailychallenges.cache;

import at.dici.shade.core.cache.AsyncMySqlCache;
import at.dici.shade.tasks.dailychallenges.utils.DCUtils;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Getter
public class DCCache {
    @Getter
    private static DCCache instance;

    private final AsyncMySqlCache<Integer, DCUtils> dailyChallengesCache = new AsyncMySqlCache<Integer, DCUtils>() {
        @Override
        protected String query() {
            return "SELECT * FROM dailychallenges;";
        }

        @Override
        protected Function<ResultSet, Map<Integer, DCUtils>> function() {
            return input -> {
                Map<Integer, DCUtils> map = Maps.newLinkedHashMap();

                try {
                    assert input != null;
                    while (input.next()) {
                        int id = input.getInt("id");
                        String dct = input.getString("dailychallengetitle");
                        String challenge_state = input.getString("challenge_state");
                        map.put(id, new DCUtils(id, dct, challenge_state));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return map;
            };
        }
    };

    public DCCache() {
        DCCache.instance = this;
    }

    public void cacheDC(DCUtils id) {
        this.getDailyChallengesCache().put( id.getId(), id);
    }


    public static int getMaxChallengeSize(){
        return (int) instance.getDailyChallengesCache().size();
    }

    public static String getChallengeState(int id) {
        return instance.getDailyChallengesCache().get( id ).getChallenge_state();
    }

    public static String getDailyChallengetitle(int id) {
        return instance.getDailyChallengesCache().get( id ).getDct();
    }


}