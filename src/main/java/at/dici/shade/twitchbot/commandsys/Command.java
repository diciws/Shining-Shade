package at.dici.shade.twitchbot.commandsys;

import at.dici.shade.twitchbot.ChannelProperties;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;
import java.util.Set;

public abstract class Command {
    private final List<String> aliase;
    private List<String> requiredBadges;
    private boolean requiresVerified = true;
    public abstract void execute(ChannelMessageEvent event, String[] cmd, ChannelProperties channel);

    public Command(List<String> aliase){
        this.aliase = aliase;
    }

    public void setRequiredBadge(List<String> requiredBadges){
        this.requiredBadges = requiredBadges;
    }

    public void setRequiresVerified(boolean requiresVerified){
        this.requiresVerified = requiresVerified;
    }

    // public Command withUserCooldown(long cooldown){}

    // .withRequiredBadge().withCooldown().withRequiredVerified()
    public boolean isCommand(String input){
        return this.aliase.contains(input);
    }
    protected boolean isPermissible(ChannelMessageEvent event){
        if (requiredBadges == null) return true;

        Set<String> userBadges = event.getMessageEvent().getBadges().keySet();
        if (userBadges.isEmpty()) return false;

        for(String badge : requiredBadges){
            if (userBadges.contains(badge)) return true;
        }
        return false;
    }

    public boolean requiresVerified(){
        return requiresVerified;
    }
}