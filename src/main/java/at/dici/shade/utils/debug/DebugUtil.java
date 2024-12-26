package at.dici.shade.utils.debug;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;


public class DebugUtil {

    public static void handleDebug(MessageChannel channel, String output) {
        channel.sendMessage("One moment, uploading results: ");
            String result = DebugUtil.sendToHastebin(output);
            if (result == null) {
                channel.sendMessage("Upload hat nicht funktioniert");
            } else {
                channel.sendMessage("Hier der Error: " + result);
            }
    }

    public static String sendToHastebin(String message) {
        try {
            return "http://hastebin.com/" + handleHastebin(message);
        } catch (UnirestException ignored) {
        }
        return null;
    }

    private static String handleHastebin(String message) throws UnirestException {
        return Unirest.post("https://hastebin.com/documents").body(message).asJson().getBody().getObject().getString("key");
    }
}