package at.dici.shade.commandSys;

import at.dici.shade.Start;
import at.dici.shade.commandSys.slashcommands.*;
import at.dici.shade.userapplications.ghostevidenceanalyzer.CommandOpenGea;
import at.dici.shade.userapplications.inventory.commands.CommandAddItem;
import at.dici.shade.userapplications.inventory.commands.CommandClearInventory;
import at.dici.shade.userapplications.inventory.commands.CommandRemoveItem;
import at.dici.shade.userapplications.inventory.commands.CommandShowInventory;
import at.dici.shade.userapplications.phasmoGuessr.commands.CommandGuess;
import at.dici.shade.userapplications.phasmoGuessr.commands.CommandInfo;
import at.dici.shade.userapplications.phasmoGuessr.commands.CommandSetChannel;
import at.dici.shade.userapplications.phasmoGuessr.commands.CommandStart;
import at.dici.shade.userapplications.phasmoQuiz.commands.PQInfo;
import at.dici.shade.userapplications.phasmoQuiz.commands.PQSetChannel;
import at.dici.shade.userapplications.phasmoQuiz.commands.PQStart;
import at.dici.shade.userapplications.profile.ProfileCommand;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandHandler extends ListenerAdapter {
    private static final String LOG_PREFIX = "CommandUpdater: ";

    static List<? extends SlashCommand> slashCommands = List.of(

            // general commands
            new Challenge(),
            //new GhostInfo(),
            new Shade(),
            new Invite(),
            new Maps(),
            new RollMap(),
            new Smudge(),

            // additional commands
            new ProfileCommand(),
            new CommandOpenGea()
            //new SteamVerifyCommand()
    );

    private static final HashMap<Integer, HashMap<String, String>> commandIdMaps = new HashMap<>();

    /**
     * Gets the command as mention. </br>
     * If the command is not yet known by the shard it will return "** + / + name + **"
     * @param jda The JDA of this command
     * @param name The name of the command
     * @return The mention string or formatted name if command not found.
     */
    public static String getCommandAsMention(JDA jda, String name) {
        int shardId = jda.getShardInfo().getShardId();
        String id = commandIdMaps.get(shardId).get(name);
        if (id != null) return "</" + name + ":" + id + ">";
        else return "**/" + name + "**";
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        String name = event.getName();
        Logger.log(LogLevel.DEBUG, "SlashCommand used: " + name + " by user: " + event.getUser().getName());

        for (SlashCommand cmd : slashCommands) {
            if (name.equals(cmd.getData().getName())) {
                cmd.execute(event);
                break;
            }
        }

    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        JDA jda = event.getJDA();

        updateGlobalCommands(jda);

        // Update guild Commands:

        event.getJDA().getGuilds().forEach(this::updateGuildCommands);
        Logger.log(LogLevel.BOT, "CommandUpdater: Guild commands registered!");
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        updateGuildCommands(event.getGuild());
    }

    public static void updateGlobalCommands(JDA jda) {
        String shardInfo = " | Shard " + jda.getShardInfo().getShardString();
        Collection<CommandData> commands = new ArrayList<>();

        for (SlashCommand cmd : slashCommands) {
            commands.add(cmd.getData());
        }

        // PG
        commands.add(CommandGuess.getCommandData());
        commands.add(CommandStart.getCommandData());
        commands.add(CommandInfo.getCommandData());
        commands.add(CommandSetChannel.getCommandData());

        // Inv
        commands.add(CommandAddItem.getCommandData());
        commands.add(CommandClearInventory.getCommandData());
        commands.add(CommandRemoveItem.getCommandData());
        commands.add(CommandShowInventory.getCommandData());

        Logger.bot(LOG_PREFIX + "Retrieving global commands..." + shardInfo);

        jda.retrieveCommands()
                .queue(registeredCommands -> {

                    int shardId = jda.getShardInfo().getShardId();
                    HashMap<String, String> commandIds = commandIdMaps.get(shardId);
                    if (commandIds != null) commandIds.clear();
                    else {
                        commandIds = new HashMap<>();
                        commandIdMaps.put(shardId, commandIds);
                    }

                    for (CommandData cmdData : commands) {
                        boolean registered = false;
                        for (Command command : registeredCommands) {
                            commandIds.put(command.getName(), command.getId());
                            if (cmdData.getName().equals(command.getName())) {
                                Logger.bot("Updating registered global command: " + cmdData.getName() + shardInfo);
                                command.editCommand().apply(cmdData).queue();
                                registered = true;
                                break;
                            }
                        }
                        if (!registered) {
                            Logger.bot("Registering new global command: " + cmdData.getName() + shardInfo);
                            jda.upsertCommand(cmdData).queue();
                        }
                    }

                    for (Command command : registeredCommands) {
                        boolean intended = false;
                        for (CommandData cmdData : commands) {
                            if (cmdData.getName().equals(command.getName())) {
                                intended = true;
                            }
                        }
                        if (!intended) {
                            Logger.info("Deleting global command: " + command.getName() + shardInfo);
                            command.delete().queue();
                        }
                    }

                });
    }

    private void updateGuildCommands(Guild guild){

        Collection<CommandData> commands = new ArrayList<>();

        //PQ
        if(Start.TEST_MODE) {
            commands.add(PQInfo.getCommandData());
            commands.add(PQSetChannel.getCommandData());
            commands.add(PQStart.getCommandData());
        }

//        // System
//        if (GuildIo.hasPrivilegeLevel(guild, GuildUtil.PrivLevel.PHASMOLEGENDS)) {
//            Logger.log(LogLevel.BOT, LOG_PREFIX + "Adding restricted CommandData! Level: " + GuildUtil.PrivLevel.PHASMOLEGENDS + " Guild: " + guild.getName());
//
//            // No restricted Commands present for now...
//
//        }


        guild.updateCommands().addCommands(commands).queue();
    }
}
