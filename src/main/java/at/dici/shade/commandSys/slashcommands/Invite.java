package at.dici.shade.commandSys.slashcommands;

import at.dici.shade.commandSys.SlashCommand;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.utils.Resources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Invite extends SlashCommand {

    private static final SlashCommandData data;
    static {
        data = Commands.slash("invite", "Get the invite link for the bot or for the support server")
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Gibt den Bot-Invite oder Bot-Server-Invite Link aus.")
                .addOptions(
                        new OptionData(OptionType.STRING, "link", "Choose which invite link you want.")
                                .setNameLocalization(DiscordLocale.GERMAN,"link")
                                .setDescriptionLocalization(DiscordLocale.GERMAN,"Welchen Invite-Link möchtest du haben?")
                                .addChoices(
                                        new Command.Choice("bot","bot")
                                                .setNameLocalization(DiscordLocale.GERMAN,"bot"),
                                        new Command.Choice("support-server","support-server")
                                                .setNameLocalization(DiscordLocale.GERMAN,"support-server")
                                )
                );
    }
    @Override
    public SlashCommandData getData() {
        return data;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping linkOption = event.getOption("link");

        String link = linkOption == null ? "" : linkOption.getAsString();

        Lang lang = new Lang(event.getInteraction());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Resources.color);
        embed.setTitle(":mailbox: "+lang.getText("commands.invite.title_new")+":");

        Button button;
        if (link.equals("support-server")) {
            button = Button.link(Resources.LINK_SUPPORT_INVITE, "support-server");
        }
        else {
            button = Button.link(Resources.LINK_BOT_INVITE, "Invite Bot");
        }

        event.replyEmbeds(embed.build())
                .addActionRow(button)
                .queue();
    }
}
