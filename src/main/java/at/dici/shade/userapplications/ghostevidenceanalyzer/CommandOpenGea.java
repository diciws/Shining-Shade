package at.dici.shade.userapplications.ghostevidenceanalyzer;

import at.dici.shade.commandSys.SlashCommand;
import at.dici.shade.core.databaseio.Lang;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandOpenGea extends SlashCommand {

    @Override
    public SlashCommandData getData() {
        return Commands.slash("gea","Opens the Ghost Evidence Analyzer")
                .setDescriptionLocalization(DiscordLocale.GERMAN, "Öffnet den Ghost Evidence Analyzer");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        Lang lang = new Lang(event.getUserLocale());
        event.deferReply(true).queue(
                hook -> user.openPrivateChannel()
                        .flatMap(c -> {
                            GEA gea = new GEA();
                            return gea.getUi().createGeaMessage(lang, c);
                        })
                        .queue(
                                s -> hook.editOriginal("GEA sent via private message! :)").queue(),
                                t -> hook.sendMessage("Failed to send DM. Make sure to allow direct messages from this server or send me a DM first.").queue()));
    }

}
