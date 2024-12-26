package at.dici.shade.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SteamVerifyUtil {
    private int id;
    private String steamname;
    private String steamid;
    private String realsteamname;
    private String phasmo_game;
    private int phasmo_hours_min;
    private String discordid;
    private String verify_code;

}
