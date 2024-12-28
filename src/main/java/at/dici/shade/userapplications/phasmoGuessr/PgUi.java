package at.dici.shade.userapplications.phasmoGuessr;

import at.dici.shade.commandSys.CommandHandler;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.core.databaseio.Rank;
import at.dici.shade.userapplications.phasmoGuessr.utils.GameResults;
import at.dici.shade.utils.PhasResources;
import at.dici.shade.utils.Resources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class PgUi {

    static final Button btnStart = Button.success(PgSettings.BUTTON_ID_START, "Start");
    static final Button btnDebugMode = Button.secondary("pg_debugmode", "Debug Mode");
    static final Button btnVerbose = Button.secondary("pg_verbose", "Verbose");
    static final Button btnReset = Button.danger("pg_reset", "Game reset");
    static final Button btnJumpRound = Button.secondary("pg_jumpround", "Jumpround");
    static final Button btnDebugMenu = Button.secondary("pg_debugmenu", "Reload Debug Menu");
    public static final Button btnPrintAllGames = Button.primary("pg_print", "PG Print");
    public static final Button btnSetGuildBonus = Button.success("pg_set_guild_bonus", "PG Guild Bonus");
    final static TextInput inputId = TextInput
            .create("pg_input_id", "ID", TextInputStyle.SHORT)
            .setMinLength(10)
            .setRequired(true)
            .build();

    final static TextInput inputBonus = TextInput
            .create("pg_input_bonus", "Bonus in %", TextInputStyle.SHORT)
            .setMaxLength(4)
            .setRequired(false)
            .build();

    final static Modal modalAddGuildBonus = Modal
            .create("pg_add_guild_bonus", "Add a bonus for a guild. (Guild-ID + Bonus)")
            .addActionRow(inputId)
            .addActionRow(inputBonus)
            .build();

    Lang lang;
    public PgUi(Lang lang) {
        this.lang = lang;
    }

    // ### GAME ############################
    EmbedBuilder getEmbedCorrectMap(String userMention, int reward, int mapId) {
        return new EmbedBuilder()
                .setTitle(PhasResources.maps.get(mapId).name)
                .setColor(PgSettings.EmbedColor.CORRECT_GUESS)
                .setDescription(lang
                        .getText("description_correct_map")
                        .replace("[user]", userMention)
                        .replace("[points]", reward + ""));
    }

    EmbedBuilder getEmbedCorrectRoom() {
        return new EmbedBuilder()
                .setTitle(lang.getText("title_correct_room"))
                .setColor(PgSettings.EmbedColor.CORRECT_GUESS);
    }

    void modifyEmbedCorrectBoth(EmbedBuilder embed, String userMention, int reward) {
        embed.setDescription(lang
                .getText("description_correct_both")
                .replace("[user]", userMention)
                .replace("[points]", reward + ""));
    }

    void modifyEmbedCorrectOnlyRoom(EmbedBuilder embed, String userMention, int reward) {
        embed.setDescription(lang
                .getText("description_correct_room")
                .replace("[user]", userMention)
                .replace("[points]", reward + ""));
    }

    void modifyEmbedWaitingForNextImg(EmbedBuilder embed) {
        embed.appendDescription("\n\n" + lang.getText("description_waiting_for_next_img"));
    }

    EmbedBuilder getEmbedTooManyFails() {
        return new EmbedBuilder()
                .setTitle(lang.getText("title_end_round_too_many_fails"))
                .setDescription(lang.getText("decription_end_round_too_many_fails"))
                .setColor(PgSettings.EmbedColor.WRONG_GUESS);
    }

    EmbedBuilder getEmbedWrongGuess(String guess) {
        return new EmbedBuilder()
                .setTitle(lang.getText("title_wrong_guess"))
                .setDescription(lang.getText("description_wrong_guess").replace("[guess]", guess))
                .setColor(PgSettings.EmbedColor.WRONG_GUESS);
    }

    MessageEmbed getEmbedRanking(GameResults results) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(lang.getText("title_scoreboard"))
                .setDescription(lang.getText("description_scoreboard_top") + ":")
                .setColor(PgSettings.EmbedColor.GAME_OVER)
                .appendDescription("```ansi\n");

        int i = 0;
        for (Player player : results.getFilteredPlayers()) {
            if (++i <= PgSettings.WINNER_LIST_MAX_SIZE) {
                embed.appendDescription(
                        "==========\u001B[1;2m\u001B[1;37m " +
                                (i) + ". \u001B[0m\u001B[0m==========\n");

                embed.appendDescription("\u001B[1;2m" + player.getPoints());
                if (results.getBonusSpread() >= i) {
                    embed.appendDescription("\u001B[1;33m +" + results.getWinnerBonus() + "\u001B[1;37m");
                }
                embed.appendDescription(" Points");
                //embed.appendDescription(" (" + (UserCache.getPoints(player.getId()) + player.getPoints()) + ")");
                embed.appendDescription("\u001B[0m\n");

                embed.appendDescription(Rank.getFormatted(player.getId(), player.getName(), true, false, 55) + "\n");
            }
        }

        embed.appendDescription("========================```");
        embed.appendDescription(
                " [Invite](" + Resources.LINK_BOT_INVITE +
                        ") » [Vote](" + Resources.LINK_VOTE +
                        ") » [Help](" + Resources.LINK_SUPPORT_INVITE +
                        ") ");

        return embed.build();
    }

    EmbedBuilder getEmbedImage(String imgId, JDA jda, int round, String url) {
        return new EmbedBuilder()
                .setTitle(lang.getText("send_img_title"))
                .setDescription(lang.getText("send_img_description")
                        .replace("**/guess**", CommandHandler.getCommandAsMention(jda, "guess")))
                .appendDescription(
                        "\n***" + round + "/" + PgSettings.MAX_ROUNDS +
                                "**     ID: " + imgId + "*")
                .setImage(url)
                .setColor(PgSettings.EmbedColor.IMAGE);
    }

    // ### COMMAND GUESS #########################
    public ReplyCallbackAction getReplyWrongChannel(SlashCommandInteractionEvent event) {
        return event.reply(lang.getText("reply_wrong_channel")
                        .replace("**/pginfo**", CommandHandler.getCommandAsMention(event.getJDA(), "pginfo"))
                ).setEphemeral(true);
    }

    public ReplyCallbackAction getReplyNoGameRunning(SlashCommandInteractionEvent event) {
        return event
                .replyEmbeds(new EmbedBuilder()
                        .setTitle(lang.getText("title_no_game_running"))
                        .setDescription(lang.getText("description_no_game_running"))
                        .build())
                .addActionRow(btnStart);
    }

    public ReplyCallbackAction getReplyGamePending(SlashCommandInteractionEvent event) {
        return event.reply(lang.getText("reply_game_pending"))
                .setEphemeral(true);
    }

    public ReplyCallbackAction getReplyMapAlreadyRevealed(SlashCommandInteractionEvent event) {
        return event.reply(lang.getText("reply_map_already_revealed"))
                .setEphemeral(true);
    }

    public ReplyCallbackAction getReplyWrongState(SlashCommandInteractionEvent event) {
        return event.reply(lang.getText("reply_wrong_state"))
                .setEphemeral(true);
    }

    // ### COMMAND PGINFO ###########################

    public ReplyCallbackAction getReplyPgInfo(SlashCommandInteractionEvent event) {
        return event.replyEmbeds(new EmbedBuilder()
                        .setTitle("PhasmoGuessr Info")
                        .appendDescription(
                                lang.getText("description")
                                        .replace("**/guess**", CommandHandler.getCommandAsMention(event.getJDA(), "guess"))
                                        .replace("**/pgstart**", CommandHandler.getCommandAsMention(event.getJDA(), "pgstart"))
                                        .replace("**/pgsetchannel**", CommandHandler.getCommandAsMention(event.getJDA(), "pgsetchannel"))
                        )
                        .setColor(PgSettings.EmbedColor.PGINFO)
                        .build())
                .setEphemeral(true);
    }

    // ### COMMAND PGSETCHANNEL ######################

    public MessageEmbed getEmbedSetChannel(JDA jda) {
        return new EmbedBuilder()
                .setTitle("PhasmoGuessr")
                .setDescription(
                        lang.getText("testmsg_desc_welcome")
                                .replace("**/pgstart**", CommandHandler.getCommandAsMention(jda, "pgstart")))
                .setImage("https://cdn.discordapp.com/attachments/1026827206082695228/1046399147969875988/doof_34.jpg")
                .build();
    }
    public String getTextSetChannelSuccess() {
        return lang.getText("reply_success");
    }

    public String getTextSetChannelError() {
        return lang.getText("reply_error");
    }

    // ### COMMAND PGSTART ######################

    public ReplyCallbackAction getReplyStartWrongChannel(SlashCommandInteractionEvent event) {
        return event.reply(
                lang.getText("reply_wrong_channel")
                        .replace("**/pginfo**", CommandHandler.getCommandAsMention(event.getJDA(), "pginfo")
                        ))
                .setEphemeral(true);
    }

    public ReplyCallbackAction getReplyStartSuccess(SlashCommandInteractionEvent event) {
        return event.reply("Let's go!")
                .setEphemeral(true);
    }

    public ReplyCallbackAction getReplyStartGameAlreadyRunning(SlashCommandInteractionEvent event) {
        return event.reply(lang.getText("reply_game_already_running"))
                .setEphemeral(true);
    }
}
