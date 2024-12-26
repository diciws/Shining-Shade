package at.dici.shade.userapplications.controlpanel;

import at.dici.shade.Start;
import at.dici.shade.commandSys.CommandHandler;
import at.dici.shade.core.cache.LanguageCache;
import at.dici.shade.core.cache.UserCache;
import at.dici.shade.core.databaseio.Rank;
import at.dici.shade.core.databaseio.UserIo;
import at.dici.shade.core.handler.UserHandler;
import at.dici.shade.twitchbot.TwitchBot;
import at.dici.shade.twitchbot.commandsys.commands.CapacityTest;
import at.dici.shade.userapplications.ghostevidenceanalyzer.GeaController;
import at.dici.shade.userapplications.phasmoGuessr.PgUi;
import at.dici.shade.userapplications.phasmoGuessr.cache.PhasmoGuessrCache;
import at.dici.shade.utils.Util;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// TODO: Button to open modal to add/remove twitch channel. Button to print all twitch channels. Number of Twitch channels joined.
public class ControlPanel extends ListenerAdapter {

    private double averagePing = 0;
    private final static String TIME_START = DateTimeFormatter.ofPattern("dd.MM.yy - HH:mm:ss").format(LocalDateTime.now());

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event){
        if (Util.isFromBotAdmin(event)) {
            String msg = event.getMessage().getContentRaw();

            if (msg.startsWith("controlpanel")) {
                event.getChannel().sendMessage("**Bot Control Panel**").setActionRow(
                        CpUiElements.btnReloadControlPanel)
                        .queue();
            }
        }
    }


    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String id = event.getButton().getId();

        if (!id.startsWith(CpUiElements.ID_PREFIX)) return;

        User user = event.getUser();


        if (id.equals(CpUiElements.btnReloadControlPanel.getId())
                && Rank.BOT_TEAM.isPermittedTo(user)) {
            averagePing = Start.getInstance().getShardManager().getAverageGatewayPing();
            event.editMessageEmbeds(createPanelEmbed())
                    .setComponents(getComponents())
                    .queue();

        }

        else if(id.equals(CpUiElements.btnCommandPrompt.getId())
                && Rank.BOT_DEV.isPermittedTo(user)) {
            event.replyModal(CpUiElements.modalCommandPrompt).queue();
        }

        else if(id.equals(CpUiElements.btnReloadLangCache.getId())
                && Rank.BOT_DEV.isPermittedTo(user)) {
            event.reply("Reloading lang Cache...").setEphemeral(true).queue();
            LanguageCache.reload();
        }

        else if(id.equals(CpUiElements.btnReloadUserCache.getId())
                && Rank.BOT_DEV.isPermittedTo(user)) {
            event.reply("Reloading user Cache...").setEphemeral(true).queue();
            UserCache.reload();
        }

        else if(id.equals(CpUiElements.btnReloadPgCache.getId())
                && Rank.BOT_DEV.isPermittedTo(user)) {
            event.reply("Reloading PhasmoGuessr Cache...").setEphemeral(true).queue();
            PhasmoGuessrCache.reload();
        }

        else if(id.equals(CpUiElements.btnPrintGuilds.getId())
                && Rank.BOT_TEAM.isPermittedTo(user)) {
            event.reply("Printing guild info to log").setEphemeral(true).queue();
            printGuilds();
        }

        else if(id.equals(CpUiElements.btnPrintTtvChannels.getId())
                && Rank.BOT_TEAM.isPermittedTo(user)) {
            event.reply("Printing TTV channel names to log").setEphemeral(true).queue();
            printTtvChannels();
        }

        else if(id.equals(CpUiElements.btnRestartTwitchBot.getId())
                && Rank.BOT_DEV.isPermittedTo(user)) {
            if (Start.TEST_MODE) {
                event.reply("Restarting Twitch Bot...").setEphemeral(true).queue();
                Start.getInstance().twitchBot.kill();
                Start.getInstance().twitchBot = new TwitchBot();
            }
        }

        else if (id.equals(CpUiElements.btnEditUser.getId())
                && Rank.BOT_TEAM.isPermittedTo(user)) {
            event.replyModal(CpUiElements.modalInputUserId).queue();
        }

        else if (id.equals(CpUiElements.btnEditUserPoints.getId())
                && Rank.BOT_TEAM.isPermittedTo(user)) {
                event.replyModal(CpUiElements.modalEditUserPoints).queue();
        }

        else if (id.equals(CpUiElements.btnReloadCommands.getId())
                && Rank.BOT_DEV.isPermittedTo(user)) {
            event.reply("Reloading all commands...").setEphemeral(true).queue();

            for (JDA jda : Start.getInstance().getShardManager().getShards()) {
                CommandHandler.updateGlobalCommands(jda);
            }
        }

        else if (id.equals(CpUiElements.btnDeleteCommand.getId())
                && Rank.BOT_DEV.isPermittedTo(user)) {
            event.reply("not yet implemented").queue();
        }

        else if(id.equals(CpUiElements.btnTwitchBotCapacityTest.getId())
                && Rank.BOT_DEV.isPermittedTo(user)) {
            if (Start.TEST_MODE) {
                if (CapacityTest.isRunning) {
                    event.reply("Stopping performance test...").setEphemeral(true).queue();
                    Start.getInstance().twitchBot.removeChannel("stachelsheriff");
                    CapacityTest.isRunning = false;
                } else {
                    event.reply("Starting performance test...").setEphemeral(true).queue();
                    CapacityTest.isRunning = true;
                    Start.getInstance().twitchBot.addChannel("stachelsheriff", false);
                }
            }
        }

        else if (id.equals(CpUiElements.btnPgSave.getId())
                && Rank.BOT_DEV.isPermittedTo(user)) {
            event.deferReply(true).queue();
            Start.getInstance()
                    .getPhasmoGuessr()
                    .saveAllGames(false,
                            b -> event.getHook().editOriginal("Save completed.").queue());
        }

        else if (id.equals(CpUiElements.btnPgLoad.getId())
                && Rank.BOT_DEV.isPermittedTo(user)) {
            event.reply("Trying to load PG game instances from save file...").setEphemeral(true).queue();
            Start.getInstance().getPhasmoGuessr().loadGames();
        }

        else {
            event.reply("Can't use this button! Either button-ID error or missing permission!")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals(CpUiElements.modalCommandPrompt.getId())) {
            ModalMapping commandNameMapping = event.getValue(CpUiElements.inputCommandPromptName.getId());
            ModalMapping commandValueMapping = event.getValue(CpUiElements.inputCommandPromptValue.getId());
            ModalMapping commandModuleMapping = event.getValue(CpUiElements.inputCommandPromptModule.getId());

            if (commandNameMapping == null
                    || commandValueMapping == null
                    || commandModuleMapping == null) {
                event.reply("Error, ModalMapping not found.")
                        .setEphemeral(true)
                        .queue();
            } else {
                String commandModule = commandModuleMapping.getAsString();
                String commandName = commandNameMapping.getAsString();
                String commandValue = commandValueMapping.getAsString();

                if (commandModule.equals(GeaController.moduleName)) {
                    event.reply(GeaController.set(commandName, commandValue))
                            .setEphemeral(true)
                            .queue();
                }
                else {
                    event.reply("Unknown Module").setEphemeral(true).queue();
                }

            }

        } else if (event.getModalId().equals(CpUiElements.modalInputUserId.getId())) {
            ModalMapping userIdMapping = event.getValue(CpUiElements.inputUserId.getId());
            if (userIdMapping == null) {
                event.reply("Error, ModalMapping not found.")
                        .setEphemeral(true)
                        .queue();
            } else {
                boolean canEditEverything = Util.isFromBotAdmin(event.getInteraction());
                String targetId = userIdMapping.getAsString();
                Rank rankOfEditor = Rank.getHighestPermission(event.getUser().getId());

                if (!canEditEverything
                        && rankOfEditor.isPermittedTo(targetId)) {
                    event.reply("You can't edit this user's rank.")
                            .setEphemeral(true)
                            .queue();
                    return;
                } else {
                    Logger.info("CP: User " + event.getUser().getName() + " (Rank: " + rankOfEditor.getName() +
                            ") editing another user (ID: " + targetId + ")");
                }

                List<Rank> ranks = Rank.getUserRanks(targetId);
                List<SelectOption> options = new ArrayList<>();



                for (Rank r : Rank.values()) {
                    if (canEditEverything || !r.hasPermissionOf(rankOfEditor)) {
                        options.add(
                                SelectOption.of(r.getName(), Integer.toString(r.getBitOffset()))
                                        .withDefault(ranks.contains(r)));
                    }
                }

                StringSelectMenu menu = StringSelectMenu
                        .create(CpUiElements.ID_MENU_USER_RANKS)
                        .setMinValues(0)
                        .setMaxValues(25)
                        .addOptions(options)
                        .setPlaceholder(userIdMapping.getAsString())
                        .build();

                event.replyEmbeds(
                        new EmbedBuilder()
                                .setTitle(targetId)
                                .setDescription("Edit User\n")
                                .appendDescription("Points: " + UserCache.getPoints(targetId))
                                .build()
                        )
                        .addActionRow(CpUiElements.btnEditUserPoints)
                        .addActionRow(menu)
                        .setEphemeral(true)
                        .queue();
            }

        } else if (event.getModalId().equals(CpUiElements.modalEditUserPoints.getId())) {

            if (event.getMessage() == null) {
                event.reply("Error: Message not found.").setEphemeral(true).queue();
            } else {
                List<MessageEmbed> embeds = event.getMessage().getEmbeds();
                if (embeds.isEmpty()) {
                    event.reply("Embed not found").setEphemeral(true).queue();
                } else {
                    String userId = embeds.get(0).getTitle();
                    ModalMapping setPointsMapping = event.getValue(CpUiElements.inputSetUserPoints.getId());
                    ModalMapping addPointsMapping = event.getValue(CpUiElements.inputAddUserPoints.getId());
                    String setPoints = "";
                    String addPoints = "";
                    if (setPointsMapping != null) setPoints = setPointsMapping.getAsString();
                    if (addPointsMapping != null) addPoints = addPointsMapping.getAsString();

                    if (!addPoints.isEmpty()) {
                        int addP;
                        try {
                            addP = Integer.parseInt(addPointsMapping.getAsString());
                            UserIo.addPoints(userId, addP);
                            event.reply("Adding " + addP + " P to User " + userId)
                                    .setEphemeral(true)
                                    .queue();
                        } catch (IllegalArgumentException e) {
                            event.reply("Error: Parse error").setEphemeral(true).queue();
                        }
                    } else if (!setPoints.isEmpty()) {
                        int setP;
                        try {
                            setP = Integer.parseInt(setPointsMapping.getAsString());
                            UserHandler.userPointsHandler(userId, setP, success ->
                                    Logger.log(LogLevel.INFORMATION, "User Points set via CP. " +
                                            "ID: " + userId + " Points: " + setP + " set by staff: " + event.getUser().getName())
                            );
                            event.reply("Setting points to " + setP + " for user with ID: " + userId)
                                    .setEphemeral(true)
                                    .queue();
                        } catch (IllegalArgumentException e) {
                            event.reply("Error: Parse error").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("Error: Both empty").setEphemeral(true).queue();
                    }

                }

            }
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals(CpUiElements.ID_MENU_USER_RANKS)) {
            String userId = event.getComponent().getPlaceholder();
            List<SelectOption> options = event.getInteraction().getSelectedOptions();
            List<Integer> ints = new ArrayList<>();
            for (SelectOption option : options) {
                ints.add(Integer.parseInt(option.getValue()));
            }

            int ranks = 0;
            for (Integer i : ints) {
                ranks = ranks | (1 << i);
            }

            event.replyEmbeds(
                    new EmbedBuilder()
                            .setTitle("Setting user ranks...")
                            .setDescription("User-ID: " + userId +
                                    "\nNew int value: " + ranks +
                                    "\nNew bit mask: " + Integer.toBinaryString(ranks))
                            .build())
                    .setEphemeral(true)
                    .queue();

            Logger.info("Setting user ranks via CP for ID: " + userId + " to " + ranks + " / " + Integer.toBinaryString(ranks) + "...");

            UserIo.setRankFlags(userId, ranks);
        }
    }

    private MessageEmbed createPanelEmbed() {
        return new EmbedBuilder()
                .setTitle("Bot Control Panel")
                .setDescription(getDescription())
                .setFooter("Last refresh: " + DateTimeFormatter.ofPattern("dd.MM.yy - HH:mm:ss").format(LocalDateTime.now()))
                .build();
    }

    private Set<LayoutComponent> getComponents(){
        return Set.of(
                ActionRow.of(
                        CpUiElements.btnReloadControlPanel,
                        CpUiElements.btnCommandPrompt
                ),
                ActionRow.of(
                        CpUiElements.btnReloadLangCache,
                        CpUiElements.btnReloadUserCache,
                        CpUiElements.btnReloadPgCache
                ),
                ActionRow.of(
                        CpUiElements.btnPrintGuilds,
                        CpUiElements.btnPrintTtvChannels,
                        CpUiElements.btnRestartTwitchBot,
                        CpUiElements.btnTwitchBotCapacityTest
                ),
                ActionRow.of(
                        CpUiElements.btnEditUser,
                        CpUiElements.btnReloadCommands,
                        CpUiElements.btnPgSave,
                        CpUiElements.btnPgLoad
                ),
                ActionRow.of(
                        PgUi.btnPrintAllGames,
                        PgUi.btnSetGuildBonus
                )
        );
    }
    private String getDescription(){
        ShardManager sManager = Start.getInstance().getShardManager();

        return "```ANSI\n" + "\u001B[1;37mVersion: \u001B[1;34m" + Start.VERSION +
                "\u001B[1;37m - Devmode: \u001B[1;36m" + Start.TEST_MODE +
                "\n\u001B[1;37mStarting time: \u001B[1;31m" + TIME_START +
                "\n\u001B[1;37mMemory (MB):" +
                "\n\u001B[1;37mMax: \u001B[1;34m" + formatMemory(Runtime.getRuntime().maxMemory()) +
                "\u001B[1;37m | Total: \u001B[1;34m" + formatMemory(Runtime.getRuntime().totalMemory()) +
                "\u001B[1;37m | Used: \u001B[1;34m" + formatMemory(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) +
                "\n\u001B[1;37mAverage Ping: " + getFormattedPing() +
                "\n\u001B[1;37mShards:" +
                "\nTotal: \u001B[1;34m" + sManager.getShardsTotal() +
                "\u001B[1;37m  |  Running: \u001B[1;34m" + sManager.getShardsRunning() +
                "\u001B[1;37m  |  Queued: \u001B[1;34m" + sManager.getShardsQueued() +
                //"\n\u001B[1;37mDB Writes:" +
                //"\nUsers: \u001B[1;34m" + StatsTracker.getDbUserWrites() +
                //"\u001B[1;37m  |  Guilds: \u001B[1;34m" + StatsTracker.getDbGuildWrites() +
                //"\u001B[1;37m  |  Bot: \u001B[1;34m" + StatsTracker.getDbBotWrites() +
                "\n\u001B[0m-------------------------------------" +
                "\n\u001B[1;37mCached Guilds:     \u001B[1;34m" + sManager.getGuildCache().size() +
                "\n\u001B[1;37mCommands used:     \u001B[1;34m" + StatsTracker.getCommandsUsed() +
                "\n\u001B[1;37mButtons clicked:   \u001B[1;34m" + StatsTracker.getButtonsClicked() +
                "\n\u001B[1;37mTimes mentioned:   \u001B[1;34m" + StatsTracker.getTimesMentioned() +
                "\n\n\u001B[1;37mPG Games running:  \u001B[1;34m" + StatsTracker.getNumberPgGames() +
                "\n\n\u001B[1;37mTTV Commands used: \u001B[1;34m" + StatsTracker.getTtvCommandsUsed() +
                "\n\u001B[1;37mTTV Channels:      \u001B[1;34m" + StatsTracker.getTtvChannelQuantity() +
                "\n\u001B[1;37mTTV Performance Test:\u001B[1;36m" + CapacityTest.isRunning +
                "```";
    }

    private String formatMemory(long value) {
        double outVal = (double) value / 1000000;
        return String.format("%.2f", outVal);
    }

    private String getFormattedPing() {
        String col;
        if (averagePing < 100)
            col = "\u001B[1;34m";
        else if (averagePing < 400)
            col = "\u001B[1;36m";
        else if (averagePing < 700)
            col = "\u001B[1;32m";
        else if (averagePing < 1000)
            col = "\u001B[1;33m";
        else col = "\u001B[1;31m";

        return col + String.format("%.1f", averagePing);
    }

    private void printGuilds(){
        SnowflakeCacheView<Guild> guildCache = Start.getInstance().getShardManager().getGuildCache();

        StringBuilder builder = new StringBuilder("[ControlPanel]: All cached guilds:");
        guildCache.forEach(g -> {
            builder.append("\n");
            builder.append(g.getName());
            builder.append(" | ID: ").append(g.getId());
            builder.append(" | Owner: ");
            if (g.getOwner() != null) builder.append(g.getOwner().getUser().getName());
            else builder.append("Unknown");
        });
        Logger.info(builder.toString());
    }

    private void printTtvChannels(){
         TwitchBot tBot = Start.getInstance().getTwitchBot();
         if (tBot == null) {
             Logger.info("ControlPanel: Twitch bot not active. No channels to print.");
             return;
         }
         StringBuilder builder = new StringBuilder("ControlPanel: All stored Twitch channels:");
         for (String chName : tBot.getStoredChannels()) {
             builder.append("\n");
             builder.append(chName);
         }
         Logger.info(builder.toString());
    }

}
