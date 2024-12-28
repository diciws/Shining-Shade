package at.dici.shade.userapplications.ghostevidenceanalyzer;

import at.dici.shade.utils.util.StringParser;

public class GeaController {
    public static final String moduleName = "gea";
    static boolean active = true;
    static long maxInactivity = 1000 * 60 * 60 * 8; // 8h
    static int maxTeamSize = 4;
    static long refreshInterval = 6100;

    public static String set(String name, String value) {
        switch (name.toLowerCase()) {
            case "active":
                if (value.equals("true")) {
                    active = true;
                } else if (value.equals("false")) {
                    active = false;
                } else {
                    return "Could not parse value";
                }
                break;
            case "maxinactivity":
                Long parsedMaxInactivity = StringParser.parseLong(value);
                if (parsedMaxInactivity == null) return "Could not parse value";
                else maxInactivity = parsedMaxInactivity;
                break;
            case "maxteamsize":
                Integer parsedMaxTeamSize = StringParser.parseInteger(value);
                if (parsedMaxTeamSize == null) return "Could not parse value";
                else maxTeamSize = parsedMaxTeamSize;
                break;
            case "refreshinterval":
                Long parsedRefreshInterval = StringParser.parseLong(value);
                if (parsedRefreshInterval == null) return "Could not parse value";
                else refreshInterval = parsedRefreshInterval;
                break;
            case "read":
                return "GEA-CONTROLLER read: " +
                        "\nactive: " + active +
                        "\nmaxInactivity: " + maxInactivity +
                        "\nmaxTeamSize: " + maxTeamSize +
                        "\nrefreshInterval: " + refreshInterval;
            default:
                return "GEA-Command not found";
        }

        return "GEA-CONTROLLER set: " + name + " " + value;
    }
}
