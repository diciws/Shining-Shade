package at.dici.shade.utils.debug;

import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

/**
 * This class watches and logs generic events.
 * For Debugging use only.
 * To be registered as Listener only when bot is in development mode
 */
public class DebugEventWatcher extends ListenerAdapter {

    private static final String LOG_PREFIX = "EventWatcher: ";


    @Override
    public void onGenericCommandInteraction(GenericCommandInteractionEvent event){
        printInteraction(event.getInteraction());
    }

    @Override
    public void onGenericComponentInteractionCreate(GenericComponentInteractionCreateEvent event){
        printInteraction(event.getInteraction());
        String compId = event.getComponentId();
        String compType = event.getComponentType().toString();
        Logger.debug(LOG_PREFIX + "Component: [ID: " + compId + " | Type: " + compType + "]");
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        printInteraction(event.getInteraction());
        String modalId = event.getModalId();
        Logger.debug(LOG_PREFIX + "Modal ID: " + modalId);
    }


    private static void printInteraction(Interaction interaction){
        StringBuilder logLine = new StringBuilder();
        logLine.append("Interaction Type: " + interaction.getType());
        logLine.append(" | User: ");
        logLine.append(interaction.getUser().getName());
        logLine.append(" | Locale: ");
        if (interaction.isFromGuild()){
            logLine.append(interaction.getGuildLocale().getLocale());
            logLine.append("(Guild)");
        } else {
            logLine.append(interaction.getUserLocale().getLocale());
            logLine.append("(User)");
        }

        Logger.debug(LOG_PREFIX + logLine);
    }
}
