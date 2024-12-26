package at.dici.shade.twitchbot.commandsys.commands;

import at.dici.shade.twitchbot.ChannelProperties;
import at.dici.shade.twitchbot.commandsys.Command;
import at.dici.shade.twitchbot.utils.twitch.Res;
import at.dici.shade.twitchbot.utils.twitch.Util;
import at.dici.shade.utils.PhasResources;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;

public class RollMap extends Command {
    public static final List<String> aliase = List.of("rollmap", "rmap", "randommap");
    public static final List<String> requiredBadges = List.of(Res.MOD_BADGE, Res.OWNER_BADGE);
    public RollMap() {
        super(aliase);
        setRequiredBadge(requiredBadges);
    }

    @Override
    public void execute(ChannelMessageEvent event, String[] cmd, ChannelProperties channel){
        int n;
        PhasResources.MapData map;

        n = at.dici.shade.utils.Util.getRandomInt(PhasResources.MapData.smallIds.length + PhasResources.MapData.largeIds.length);
        if (n >= PhasResources.MapData.smallIds.length) {
            n -= PhasResources.MapData.smallIds.length;
            map = PhasResources.maps.get(PhasResources.MapData.largeIds[n]);
        } else {
            map = PhasResources.maps.get(PhasResources.MapData.smallIds[n]);
        }

        Util.chatReply(event, map.name);

    }
}