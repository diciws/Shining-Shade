package at.dici.shade.userapplications.privacy;

import at.dici.shade.core.handler.UserHandler;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DataDeletionRequestListener extends ListenerAdapter {

    private static final String TEMP_TITLE_1 = "Delete Personal Data";
    private static final String TEMP_DESC_1 = "To request the deletion of all your data we store click the button below." +
            "\nThis will delete the entire set of data including you PhasmoGuessr points and your rank. Both can be seen by using **/profile**" +
            "\nIt can take up to 24 hours until your data is deleted.";
    private static final String TEMP_TITLE_2 = "Are you sure you want to delete all your data?";
    private static final String TEMP_DESC_2 = "";
    private final Set<String> triggerWords = Set.of("privacy", "delete", "data");
    private static final Button btnDeleteRequest = Button.of(ButtonStyle.PRIMARY, "privacy_request_data_deletion", "Delete my Data", Emoji.fromFormatted("❌"));
    private static final Button btnConfirmDeletion = Button.of(ButtonStyle.DANGER, "privacy_confirm_data_deletion", "Confirm Deletion", Emoji.fromFormatted("✅"));

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (triggerWords.contains(event.getMessage().getContentRaw().toLowerCase())) {
                event.getMessage().reply(createReply()).queue();
            }

        }
    }

    private MessageCreateData createReply(){
        return new MessageCreateBuilder()
                .addEmbeds(new EmbedBuilder()
                        .setTitle(TEMP_TITLE_1)
                        .setDescription(TEMP_DESC_1)
                        .build())
                .addActionRow(btnDeleteRequest)
                .build();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId().equals(btnDeleteRequest.getId())) {
            event.reply(createConfirmReply()).queue();
        } else if (event.getButton().getId().equals(btnConfirmDeletion.getId())) {
            User user = event.getUser();
            UserHandler.removeUser(user.getId(),
                    success ->
                    {
                        if (success)
                            Logger.info("Removed user due to privacy request. U: " + user.getName() + " ID: " + user.getId());
                        else
                            Logger.fatal("User data deletion (privacy request) failed! U: " + user.getName() + " ID: " + user.getId());
                    });
            event.reply("Your data will soon be deleted from our database.").queue();
        }
    }

    private MessageCreateData createConfirmReply(){
        return new MessageCreateBuilder()
                .addEmbeds(new EmbedBuilder()
                        .setTitle(TEMP_TITLE_2)
                        .setDescription(TEMP_DESC_2)
                        .build())
                .addActionRow(btnConfirmDeletion)
                .build();
    }
}
