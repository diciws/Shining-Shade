package at.dici.shade.guild;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Eastereggs extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event){

        // Adds a Heart to every Message mentioning this lovely bot
        if (event
                .getMessage()
                .getMentions()
                .isMentioned(event.getJDA().getSelfUser(), Message.MentionType.USER)) {
            try {
                event.getMessage()
                        .addReaction(Emoji.fromFormatted("♥"))
                        .queue(null, unused -> {});
            } catch (InsufficientPermissionException ignored) {} // Permission Error can't be handled via ErrorResponse for some reason
        }

    }
}
