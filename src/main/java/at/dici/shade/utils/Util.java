package at.dici.shade.utils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Util {
    private static final Random random = new Random(System.currentTimeMillis());
    public static int getRandomInt(int bound){
        return random.nextInt(bound);
    }

    public static boolean isFromBotAdmin(MessageReceivedEvent event){
        return Resources.BOT_ADMINS.contains(event.getAuthor().getIdLong());
    }

    public static boolean isFromBotAdmin(Interaction interaction){
        return Resources.BOT_ADMINS.contains(interaction.getUser().getIdLong());
    }

    public static List<Long> parseLongsFromModal(ModalInteractionEvent event) {
        List<ModalMapping> mappings = event.getValues();
        List<Long> longs = new ArrayList<>();
        for (ModalMapping mapping : mappings) {
            String valueString = mapping.getAsString().replaceAll("\\s+", "");
            long value;
            try {
                value = Long.parseLong(valueString);
                longs.add(value);
            } catch (NumberFormatException ignore) {}
        }
        return longs;
    }

    public static List<Integer> parseIntsFromModal(ModalInteractionEvent event) {
        List<ModalMapping> mappings = event.getValues();
        List<Integer> ints = new ArrayList<>();
        for (ModalMapping mapping : mappings) {
            String valueString = mapping.getAsString().replaceAll("\\s+", "");
            int value;
            try {
                value = Integer.parseInt(valueString);
                ints.add(value);
            } catch (NumberFormatException ignore) {}
        }
        return ints;
    }

    //to get avatar on join eventually xd
    public static String getUserAvatarURL(User user) {
        return user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl();
    }

    public static <K, V> List<V> createListFromMapEntries (Map<K, V> map){
        return map.values().stream().collect(Collectors.toList());
    }

}