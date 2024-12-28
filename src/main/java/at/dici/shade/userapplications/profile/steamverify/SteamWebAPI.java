package at.dici.shade.userapplications.profile.steamverify;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.getplayerbans.GetPlayerBans;
import com.lukaspradel.steamapi.data.json.ownedgames.GetOwnedGames;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetOwnedGamesRequest;
import com.lukaspradel.steamapi.webapi.request.GetPlayerBansRequest;
import com.lukaspradel.steamapi.webapi.request.GetPlayerSummariesRequest;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SteamWebAPI {

    private final String name;
    private final String steamid;
    private final boolean havePhasmo;
    private final boolean haveVACation;
    private final SteamWebApiClient client = new SteamWebApiClient.SteamWebApiClientBuilder("56DC7CB71EF7B0C40F5601011E2AA2AD").build();

    public SteamWebAPI(String steamid) {
        this.steamid = steamid;
        this.haveVACation = haveVACation();
        this.name = getName1();
        this.havePhasmo = hasPhasmo();
    }

    public String getName() {
        return this.name;
    }

    public boolean haveVAC() {
        return this.haveVACation;
    }

    public boolean havePhasmo() {
        return this.havePhasmo;
    }

    private String getName1() {
        List<String> list = new ArrayList<>();
        list.clear();
        list.add(steamid);

        GetPlayerSummariesRequest request = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(list);

        JSONObject json = null;
        try {
            GetPlayerSummaries playerinfos = client.processRequest(request);
            json = new JSONObject(playerinfos);
        } catch (JSONException | SteamApiException e) {
            e.printStackTrace();
        }

        JSONObject response = json.getJSONObject("response");

        JSONArray playersarray = (JSONArray) response.get("players");

        JSONObject playerinfo = (JSONObject) playersarray.get(0);

        return playerinfo.getString("personaname");
    }

    private boolean haveVACation() {

        List<String> list = new ArrayList<>();
        list.clear();
        list.add(steamid);

        GetPlayerBansRequest request = SteamWebApiRequestFactory.createGetPlayerBansRequest(list);

        JSONObject json = null;
        try {
            GetPlayerBans playerbans = client.processRequest(request);
            json = new JSONObject(playerbans);
        } catch (JSONException | SteamApiException e) {
            e.printStackTrace();
        }

        JSONArray players = json.getJSONArray("players");
        JSONObject player = (JSONObject) players.get(0);

        return player.getBoolean("VACBanned");
    }

    private boolean hasPhasmo() {
        GetOwnedGamesRequest request = SteamWebApiRequestFactory.createGetOwnedGamesRequest(steamid);

        JSONObject json = null;
        try {
            GetOwnedGames owngames = client.processRequest(request);
            json = new JSONObject(owngames);
        } catch (SteamApiException e1) {
            e1.printStackTrace();
        }

        JSONObject response = json.getJSONObject("response");

        JSONArray playersarray = (JSONArray) response.get("games");

        for (int i = 0; i < playersarray.length(); i++) {
            JSONObject games1 = (JSONObject) playersarray.get(i);
            System.out.println(games1.get("appid"));
            if (games1.get("appid").toString().contains("739630")) {
                return true;
            }
        }
        return false;
    }
}
