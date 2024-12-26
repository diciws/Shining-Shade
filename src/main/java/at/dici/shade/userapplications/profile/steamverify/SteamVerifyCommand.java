package at.dici.shade.userapplications.profile.steamverify;

import at.dici.shade.commandSys.SlashCommand;
import at.dici.shade.core.databaseio.Lang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SteamVerifyCommand extends SlashCommand {
    public static final String COMMAND_NAME = "verify";
    private static final String LANG_PREFIX = "userapplications.profile.steamverify.";

    @Override
    public SlashCommandData getData() {
        return Commands.slash(COMMAND_NAME, "Link your profile to Steam")
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Verbinde dein Profil mit Steam");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Lang lang = new Lang(LANG_PREFIX, event);

        event.replyModal(SteamVerifyModalListener.getModal(lang)).queue();
    }
}