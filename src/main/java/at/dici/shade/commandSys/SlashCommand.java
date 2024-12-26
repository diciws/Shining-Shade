package at.dici.shade.commandSys;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class SlashCommand {
    public abstract SlashCommandData getData();
    public abstract void execute(SlashCommandInteractionEvent event);
}
