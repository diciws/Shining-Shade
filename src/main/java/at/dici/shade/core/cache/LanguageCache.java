package at.dici.shade.core.cache;

import at.dici.shade.core.utils.LanguageUtil;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Getter
public class LanguageCache {
    @Getter

    private static LanguageCache instance;

    private final AsyncMySqlCache<String, LanguageUtil> languageCache = new AsyncMySqlCache<String, LanguageUtil>() {
        @Override
        protected String query() {
            return "SELECT * FROM language;";
        }

        @Override
        protected Function<ResultSet, Map<String, LanguageUtil>> function() {
            return input -> {
                Map<String, LanguageUtil> map =  Maps.newLinkedHashMap();

                try{
                    assert input != null;
                    while ( input.next() ) {
                        String languageoutput = input.getString( "languageoutput" );
                        String de = input.getString( "de" );
                        String en = input.getString( "en" );
                        String es = input.getString( "es" );
                        String it = input.getString( "it" );
                        String ro = input.getString( "ro" );
                        String ru = input.getString( "ru" );
                        String fr = input.getString( "fr" );
                        String pl = input.getString( "pl" );
                        String hi = input.getString( "hi" );
                        String cs = input.getString( "cs" );
                        map.put( languageoutput, new LanguageUtil( languageoutput, de, en, es, it, ro, ru, fr, pl, hi, cs) );
                    }
                    return map;
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }

                return null;
            };
        }
    };

    public LanguageCache(){
        LanguageCache.instance = this;
    }

    public static void reload(){
        Logger.log(LogLevel.WARNING, "Reloading! -> LanguageCache Mapsize: "+instance.languageCache.size()+ "-> Parameters -> "+instance.languageCache.parameters());
        instance.languageCache.invalidateAll();
        new LanguageCache();
        Logger.log(LogLevel.INFORMATION, "LanguageCache reloaded.");
    }

    public LanguageUtil keyisinCache(String languagekey ) {
        return this.getLanguageCache().getIfPresent(languagekey);
    }

    //EN language Check and return
    public static String checkEN(String languageoutput,String lang){
        if (instance.getLanguageCache().get(languageoutput).getEn().isEmpty()) {
            Logger.log(LogLevel.FATAL, "Loading Language -> " + instance.getLanguageCache().get(languageoutput));
            return "Error loading default Language";
        } else {
            Logger.log(LogLevel.ERROR, "Loading Lang -> "+lang+" ! Using loaded EN as default. Error: "+languageoutput);
            return instance.getLanguageCache().get(languageoutput).getEn();
        }
    }

    /**
     *
     * Output right language by entering right setting input
     *
     * @param languageoutput
     * @return"Cache Error missing key: "+getlocalestring;
     */
    public static String getLanguageOutput( String languageoutput , String getlocalestring) {
        if(instance.keyisinCache(languageoutput) != null) {
            if(!getlocalestring.isEmpty()) {
                switch (getlocalestring) {
                    case "de" -> {
                        if(instance.getLanguageCache().get(languageoutput).getDe().isEmpty())
                            return checkEN(languageoutput,"DE");
                        else
                            return instance.getLanguageCache().get(languageoutput).getDe(); }
                    case "es-ES" -> {
                        if(instance.getLanguageCache().get(languageoutput).getEs().isEmpty())
                            return checkEN(languageoutput,"ES");
                        else
                            return instance.getLanguageCache().get(languageoutput).getEs(); }
                    case "it" -> {
                        if(instance.getLanguageCache().get(languageoutput).getIt().isEmpty())
                            return checkEN(languageoutput,"IT");
                        else
                            return instance.getLanguageCache().get(languageoutput).getIt(); }
                    case "ro" -> {
                        if(instance.getLanguageCache().get(languageoutput).getRo().isEmpty())
                            return checkEN(languageoutput,"RO");
                        else
                            return instance.getLanguageCache().get(languageoutput).getRo(); }
                    case "ru" -> {
                        if(instance.getLanguageCache().get(languageoutput).getRu().isEmpty())
                            return checkEN(languageoutput,"RU");
                        else
                            return instance.getLanguageCache().get(languageoutput).getRu(); }
                    case "fr" -> {
                        if(instance.getLanguageCache().get(languageoutput).getFr().isEmpty())
                            return checkEN(languageoutput,"FR");
                        else
                            return instance.getLanguageCache().get(languageoutput).getFr(); }
                    case "pl" -> {
                        if(instance.getLanguageCache().get(languageoutput).getPl().isEmpty())
                            return checkEN(languageoutput,"PL");
                        else
                            return instance.getLanguageCache().get(languageoutput).getPl(); }
                    case "hi" -> {
                        if(instance.getLanguageCache().get(languageoutput).getHi().isEmpty())
                            return checkEN(languageoutput,"HI");
                        else
                            return instance.getLanguageCache().get(languageoutput).getHi(); }
                    case "cs" -> {
                        if(instance.getLanguageCache().get(languageoutput).getCs().isEmpty())
                            return checkEN(languageoutput,"CS");
                        else
                            return instance.getLanguageCache().get(languageoutput).getCs(); }
                    default -> {
                        //en-UK || en-US is default aswell
                        if (instance.getLanguageCache().get(languageoutput).getEn().isEmpty()) {
                            return checkEN(languageoutput,"EN");
                        } else {
                            return instance.getLanguageCache().get(languageoutput).getEn();
                        }
                    }
                }
            }else {
                Logger.log(LogLevel.ERROR, "Not possible to load getLanguageOutput -> language key is empty!");
                return "Cache Error Key 1.0";
            }
        }else {
            Logger.log(LogLevel.ERROR, "Loading Cache Key: " + languageoutput);
            return "Error loading Cache Key: " + languageoutput;
        }
    }

}
