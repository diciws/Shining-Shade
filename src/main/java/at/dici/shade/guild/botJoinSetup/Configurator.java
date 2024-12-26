package at.dici.shade.guild.botJoinSetup;

import at.dici.shade.core.databaseio.GuildIo;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.core.utils.GuildFlag;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;


public class Configurator extends ListenerAdapter {

    /* Was alles in die Config muss:

    - set Botchannel
    - set PG-Channel

    - on/off smudgetimer
    - on/off mapdice

     */

    private static final String LANG_PREFIX = "botjoinsetup.configurator.";
    private static final String LOG_PREFIX = "Configurator: ";

    private static final String BTN_ID_PREFIX = "config_";
    private static final String BTN_ID_PG = BTN_ID_PREFIX + "toggle_pg";
    private static final String BTN_ID_GEA = BTN_ID_PREFIX + "toggle_gea";
    private static final String BTN_ID_INVENTORY = BTN_ID_PREFIX + "toggle_inventory";
    private static final String BTN_ID_SMUDGETIMER = BTN_ID_PREFIX + "toggle_smudgetimer";
    private static final String BTN_ID_MAPDICE = BTN_ID_PREFIX + "toggle_mapdice";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(ShadeConfigCommand.COMMAND_NAME)){
            ShadeConfigCommand.perform(event);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        if (event.getButton().getId().startsWith(BTN_ID_PREFIX) && event.isFromGuild()) {

            Lang lang = new Lang(LANG_PREFIX, event.getInteraction());

            if (event.isFromGuild() && event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

                String id = event.getButton().getId();

                GuildFlag flag;

                if (id.equals(BTN_ID_PG)) flag = GuildFlag.PHASMO_GUESSR;

                else if (id.equals(BTN_ID_GEA)) flag = GuildFlag.GEA_DM;

                else if (id.equals(BTN_ID_INVENTORY)) flag = GuildFlag.INVENTORY;

                else if (id.equals(BTN_ID_SMUDGETIMER)) flag = GuildFlag.SMUDGETIMER;

                else if (id.equals(BTN_ID_MAPDICE)) flag = GuildFlag.MAPDICE;

                else flag = GuildFlag.EMPTY;

                event.deferEdit().queue();

                GuildIo.toggleFlag(flag, event.getGuild(),
                        s -> event.getHook().editOriginal(editConfigMessage(lang, event.getGuild())).queue(
                                m -> {},
                                t -> Logger.log(LogLevel.ERROR,LOG_PREFIX + "Interaction hook expired. Toggling guild flags in DB took too long!")));
            } else {
                event.reply(lang.getText("button_denied")).setEphemeral(true).queue();
            }
        }
    }

    private static MessageEmbed createConfigEmbed(Lang lang, Guild guild){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(lang.getText("title"))
                .appendDescription(lang.getText("description.your_settings_top") + "\n\n");

        TextChannel botChannel = GuildIo.getBotChannel(guild);
        if (botChannel == null) embed.appendDescription("❌ [" + lang.getText("description.channel_not_found") + "]");
        else embed.appendDescription("✅ " + botChannel.getAsMention());
        embed.appendDescription(" - Bot Channel\n");

        TextChannel pgChannel = GuildIo.getPhasmoGuessrChannel(guild);
        if (pgChannel == null) embed.appendDescription("❌ [" + lang.getText("description.channel_not_found") + "]");
        else embed.appendDescription("✅ " + pgChannel.getAsMention());
        embed.appendDescription(" - PhasmoGuessr Channel\n");

        embed.appendDescription("\n\n**Commands**");

        embed.appendDescription(getToggleString(GuildFlag.PHASMO_GUESSR,guild) + "PhasmoGuessr");
        embed.appendDescription(getToggleString(GuildFlag.GEA_DM,guild) + "Ghost Evidence Analyzer");
        embed.appendDescription(getToggleString(GuildFlag.INVENTORY,guild) + "Inventory System");
        embed.appendDescription(getToggleString(GuildFlag.SMUDGETIMER,guild) + "SmudgeTimer");
        embed.appendDescription(getToggleString(GuildFlag.MAPDICE,guild) + "MapDice");

        embed.appendDescription("\n\n" + lang.getText("description.bottom"));

        return embed.build();
    }

    private static String getToggleString(GuildFlag flag, Guild guild){
        if (GuildIo.hasFlag(flag, guild)) return "\n✅ ";
        else return "\n❌ ";
    }

    private static ActionRow createConfigActionRow(Lang lang, Guild guild){
        return ActionRow.of(
                createToggleButton(BTN_ID_PG, "PhasmoGuessr", GuildIo.hasFlag(GuildFlag.PHASMO_GUESSR, guild)),
                createToggleButton(BTN_ID_GEA, "Ghost Evidence Analyzer", GuildIo.hasFlag(GuildFlag.GEA_DM, guild)),
                createToggleButton(BTN_ID_INVENTORY, "Inventory", GuildIo.hasFlag(GuildFlag.INVENTORY, guild)),
                createToggleButton(BTN_ID_SMUDGETIMER, "SmudgeTimer", GuildIo.hasFlag(GuildFlag.SMUDGETIMER, guild)),
                createToggleButton(BTN_ID_MAPDICE, "MapDice", GuildIo.hasFlag(GuildFlag.MAPDICE, guild))
        );
    }

    static MessageCreateData createConfigMessage(Lang lang, Guild guild){
        lang.setLineNamePrefix(LANG_PREFIX);
        return new MessageCreateBuilder()
                .addEmbeds(createConfigEmbed(lang, guild))
                .addComponents(createConfigActionRow(lang,guild))
                .build();
    }

    static MessageEditData editConfigMessage(Lang lang, Guild guild){
        return new MessageEditBuilder()
                .setEmbeds(createConfigEmbed(lang,guild))
                .setComponents(createConfigActionRow(lang,guild))
                .build();
    }

    private static Button createToggleButton(String id, String label, boolean state){
        Emoji emoji;
        if (state) {
            emoji = Emoji.fromFormatted("✅");
        } else {
            emoji = Emoji.fromFormatted("❌");
        }
        return Button.of(ButtonStyle.SECONDARY, id, label, emoji);
    }

}
