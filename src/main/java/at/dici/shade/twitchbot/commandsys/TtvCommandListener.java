package at.dici.shade.twitchbot.commandsys;

import at.dici.shade.twitchbot.ChannelProperties;
import at.dici.shade.twitchbot.commandsys.commands.CapacityTest;
import at.dici.shade.twitchbot.commandsys.commands.RollMap;
import at.dici.shade.twitchbot.commandsys.commands.Smudge;
import at.dici.shade.twitchbot.inventory.commands.*;
import at.dici.shade.userapplications.controlpanel.StatsTracker;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.HashMap;
import java.util.List;

public class TtvCommandListener {
    private static final String LOG_PREFIX = "Twitch CommandListener: ";
    private final List<? extends Command> commands = List.of(
            // Register commands here
            new CapacityTest(),
            new Smudge(),
            new RollMap(),
            //new Verify(),
            new AddItem(),
            new ClearInventory(),
            new FillInventory(),
            new RemoveItem(),
            new ShowInventory()
    );

    private final HashMap<String, ChannelProperties> channels;
    public TtvCommandListener(HashMap<String, ChannelProperties> channels){
        this.channels = channels;
    }
    public void onChannelMessage(ChannelMessageEvent event){

        // this whole following if-block can be removed when Capacity Test is being remove.
        if (CapacityTest.isRunning) {
            String msg = event.getMessage();
            String[] cmd = msg.toLowerCase().split(" ", 5);
            Command command = commands.get(0);
            if (command.isCommand(cmd[0])) {
                String channelName = event.getChannel().getName();
                ChannelProperties channel = channels.get(channelName);
                if (channel == null) {
                    Logger.log(LogLevel.ERROR,"Command detected in unregistered Channel!");
                    return;
                }
                else if (channel.rateLimit()) {
                    Logger.log(LogLevel.WARNING,"Channel exceeding rate limit: " + channelName);
                    return;
                }
                else {
                    Logger.log(LogLevel.DEBUG,LOG_PREFIX + event.getUser().getName() + " - Cmd: " + cmd[0]);
                    StatsTracker.ttvCommandUsed();
                    if (channel.isVerified() || !command.requiresVerified()){
                        if (command.isPermissible(event)) {
                            command.execute(event, cmd, channel);
                        }
                    }
                }
            }
        }
        // +++

        if(event.getMessage().startsWith("!") && event.getMessage().length() > 1){

            String msg = event.getMessage();
            msg = msg.substring(1);
            String[] cmd = msg.toLowerCase().split(" ", 5); // all (sub-)commands need to be defined in lowercase

            for(Command command : commands){
                if (command.isCommand(cmd[0])) {
                    String channelName = event.getChannel().getName();
                    ChannelProperties channel = channels.get(channelName);
                    if (channel == null) {
                        Logger.log(LogLevel.ERROR,"Command detected in unregistered Channel!");
                        return;
                    }
                    else if (channel.rateLimit()) {
                        Logger.log(LogLevel.WARNING,"Channel exceeding rate limit: " + channelName);
                        return;
                    }
                    else {
                        Logger.log(LogLevel.DEBUG,LOG_PREFIX + event.getUser().getName() + " - Cmd: " + cmd[0]);
                        StatsTracker.ttvCommandUsed();
                        if (channel.isVerified() || !command.requiresVerified()){
                            if (command.isPermissible(event)) {
                                command.execute(event, cmd, channel);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}
