package at.dici.shade.guild.botJoinSetup;

import at.dici.shade.core.databaseio.Lang;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class ShadeConfigCommand extends ListenerAdapter {
    public static final String LANG_PREFIX = "guild.botjoinsetup.ShadeConfigCommand.";
    public static final String COMMAND_NAME = "shadeconfig";

    private static final String SUB_NAME_OPEN_CONFIGURATOR = "settings";
    private static final String SUB_NAME_SET_PG_CHANNEL = "pgchannel";
    private static final String SUB_NAME_SET_BOT_CHANNEL = "botchannel";

    public static SlashCommandData getCommandData() {

        SubcommandData configurator = new SubcommandData(SUB_NAME_OPEN_CONFIGURATOR, "Opens the Configurator to change my settings");

        SubcommandData setPgChannel = new SubcommandData(SUB_NAME_SET_PG_CHANNEL, "Sets a channel for PhasmoGuessr")
                .addOptions(new OptionData(OptionType.CHANNEL, "channel","The PhasmoGuessr channel", false));

        SubcommandData setBotChannel = new SubcommandData(SUB_NAME_SET_BOT_CHANNEL, "Sets a channel where users can interact with the bot")
                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The bot channel", false));

        return Commands.slash(COMMAND_NAME, "Configure everything I do to your Server <3")
                .setDescriptionLocalization(DiscordLocale.GERMAN, "Konfiguriere alles, was ich deinem Server antue <3")
                .addSubcommands(configurator, setPgChannel, setBotChannel)
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    public static void perform(SlashCommandInteractionEvent event) {
        Lang lang = new Lang(LANG_PREFIX, event.getInteraction());

        if (event.getSubcommandName().equals(SUB_NAME_OPEN_CONFIGURATOR)) {
            event.reply(Configurator.createConfigMessage(lang, event.getGuild())).queue();
        }
        else {
            event.reply("Command not implemented yet!").queue();
        }

//        else if (event.getSubcommandName().equals(SUB_NAME_SET_PG_CHANNEL)) {
//            if (event.getOptions().isEmpty()) {
//                if (event.getChannelType() != ChannelType.TEXT) {
//                    event.reply("The channel for PhasmoGuessr must be a text channel").setEphemeral(true).queue();
//                } else {
//
//                }
//            }
//        }


    }

}
