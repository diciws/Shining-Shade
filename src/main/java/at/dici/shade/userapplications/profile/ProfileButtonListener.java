package at.dici.shade.userapplications.profile;

import at.dici.shade.core.databaseio.Rank;
import at.dici.shade.core.databaseio.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ProfileButtonListener extends ListenerAdapter {
    static final Button btnNextRank = Button.primary("profile_next_rank","Switch Rank");

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        if (event.getButton().getId().equals(btnNextRank.getId())) {
            Message.Interaction interaction = event.getMessage().getInteraction();
            if (interaction == null || !interaction.getUser().equals(event.getUser())) {
                event.reply("Can't change other user's ranks.").setEphemeral(true).queue();
            } else {
                User user = event.getUser();
                Rank newRank;
                Lang lang = new Lang(ProfileCommand.LANG_PREFIX, event.getInteraction());
                if (!Rank.isAnyRankPresent(event.getUser())) {
                    // Remove Button if no Rank is present
                    newRank = Rank.getActiveRank(user);
                    event.editMessageEmbeds(ProfileCommand.createEmbed(user, lang, newRank))
                            .setComponents()
                            .queue();
                } else {
                    newRank = Rank.setNextActive(user);
                    event.editMessageEmbeds(ProfileCommand.createEmbed(user, lang, newRank)).queue();
                }
            }
        }
    }
}
