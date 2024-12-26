package at.dici.shade.commandSys.slashcommands;

import at.dici.shade.commandSys.CommandHandler;
import at.dici.shade.commandSys.SlashCommand;
import at.dici.shade.core.cache.SettingsCache;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.utils.Resources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Shade extends SlashCommand {
    private static final String LANG_PREFIX = "commands.info.";
    private static final SlashCommandData data;
    static {
        data = Commands.slash("shade","Shows all the commands you can use")
                .setDescriptionLocalization(DiscordLocale.GERMAN, "Informiert dich über Befehle, die du nutzen kannst");
    }

    @Override
    public SlashCommandData getData() {
        return data;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Lang lang = new Lang(LANG_PREFIX, event.getInteraction());
        JDA jda = event.getJDA();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(":books: " + lang.getText("shade_title"));

        embed.appendDescription(":ghost: **__" + lang.getText("basic_title") + "__** \n");

        appendCmdInfo(jda, lang, "shade", "basic_desc.shade", embed);
        appendCmdInfo(jda, lang, "profile", "basic_desc.profile", embed);
        appendCmdInfo(jda, lang, "smudge", "basic_desc.smudge", embed);
        appendCmdInfo(jda, lang, "gea", "basic_desc.gea", embed);
        appendCmdInfo(jda, lang, "maps", "basic_desc.maps", embed);
        appendCmdInfo(jda, lang, "invite", "basic_desc.invite", embed);

        embed.appendDescription("\n:game_die: **__" + lang.getText("roll_title") + "__** \n");

        appendCmdInfo(jda, lang, "rollmap", "roll_desc.rollmap", embed);
        appendCmdInfo(jda, lang, "challenge", "roll_desc.challenge", embed);

        embed.appendDescription("\n:map: **__" + lang.getText("games_title") + "__** \n");

        appendCmdInfo(jda, lang, "pgstart", "games_desc.pgstart", embed);
        appendCmdInfo(jda, lang, "pginfo", "games_desc.pginfo", embed);

        embed.appendDescription("\n:pencil: **__" + lang.getText("inventory_title") + "__** \n");

        appendCmdInfo(jda, lang, "showinventory", "inventory_desc.showinventory", embed);
        appendCmdInfo(jda, lang, "additem", "inventory_desc.additem", embed);
        appendCmdInfo(jda, lang, "removeitem", "inventory_desc.removeitem", embed);
        appendCmdInfo(jda, lang, "resetinventory", "inventory_desc.resetinventory", embed);

//        embed.appendDescription("**/shade** ► "+lang.getText("basic_desc.shade") + "\n");
//        embed.appendDescription("**/profile** ► "+lang.getText("basic_desc.profile") + "\n");
//        embed.appendDescription("**/smudge** ► " + lang.getText("basic_desc.smudge") + "\n");
//        embed.appendDescription("**/gea** ► " + lang.getText("basic_desc.gea") + "\n");
//        embed.appendDescription("**/maps** ► " + lang.getText("basic_desc.maps") + "\n");
//        embed.appendDescription("**/invite** ► " + lang.getText("basic_desc.invite") + "\n");
//
//        embed.appendDescription("\n");
//
//        embed.appendDescription(":game_die: **__" + lang.getText("roll_title") + "__** \n");
//        embed.appendDescription("**/rollmap** ► " + lang.getText("roll_desc.rollmap") + "\n");
//        embed.appendDescription("**/challenge** ► " + lang.getText("roll_desc.challenge") + "\n");
//
//        embed.appendDescription("\n");
//
//        embed.appendDescription(":map: **__" + lang.getText("games_title") + "__** \n");
//        embed.appendDescription("**/pgstart** ► " + lang.getText("games_desc.pgstart") + "\n");
//        embed.appendDescription("**/pginfo** ► " + lang.getText("games_desc.pginfo") + "\n");
//
//        embed.appendDescription("\n");
//
//        embed.appendDescription(":pencil: **__" + lang.getText("inventory_title") + "__** \n");
//        embed.appendDescription("**/showinventory** ► " + lang.getText("inventory_desc.showinventory") + "\n");
//        embed.appendDescription("**/additem** ► " + lang.getText("inventory_desc.additem") + "\n");
//        embed.appendDescription("**/removeitem** ► " + lang.getText("inventory_desc.removeitem") + "\n");
//        embed.appendDescription("**/resetinventory** ► " + lang.getText("inventory_desc.resetinventory") + "\n");

        //embed.setDescription(lang.getText("description"));
        embed.setColor(Resources.color);

        event.replyEmbeds(embed.build())
                .addActionRow(Button.link(SettingsCache.getProperty("bot.vote.link"), "Vote").withEmoji(Emoji.fromUnicode("\uD83D\uDD25")),
                        Button.link(SettingsCache.getProperty("bot.invite.link"), "Invite").withEmoji(Emoji.fromUnicode("✨")))
                .queue();
    }

    private void appendCmdInfo(JDA jda, Lang lang, String name, String key, EmbedBuilder embed) {
        embed.appendDescription(CommandHandler.getCommandAsMention(jda, name))
                .appendDescription(" ► " + lang.getText(key) + "\n");
    }


}
