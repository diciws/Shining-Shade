package at.dici.shade.userapplications.ghostevidenceanalyzer;

import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.utils.Util;
import at.dici.shade.utils.log.Logger;
import at.dici.shade.utils.phasmophobia.Evidence;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GeaListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction (@NotNull ButtonInteractionEvent event) {
        if (GeaController.active) {
            String buttonId = event.getButton().getId();
            if (buttonId != null && buttonId.startsWith(GeaUi.ID_PREFIX)) {
                Logger.debug(GEA.getSessionInfo());
                GEA gea = GEA.getShared(event.getUser());

                if (buttonId.startsWith(GeaUi.BTN_RESET_ID_PREFIX)) {
                    if (gea != null) gea.reset();
                    else gea = new GEA();
                } else {

                    if (gea == null) {
                        // not shared
                        List<ActionRow> rows = event.getMessage().getActionRows();
                        boolean breakLoop = false;
                        for (ActionRow row : rows) {
                            for (Button btn : row.getButtons()) {
                                String id = btn.getId();
                                if (id != null && id.startsWith(GeaUi.BTN_RESET_ID_PREFIX)) {
                                    String serializedGea = id.substring(GeaUi.BTN_RESET_ID_PREFIX.length());
                                    gea = GeaStringifier.deserialize(serializedGea);
                                    breakLoop = true;
                                    break;
                                }
                            }
                            if (breakLoop) break;
                        }

                        if (gea == null) gea = new GEA();
                    }

                    if (buttonId.equals(GeaUi.BTN_ID_SPEED)) {
                        gea.setNextSpeed();
                    } else if (buttonId.equals(GeaUi.BTN_ID_EVIDENCE_QUANTITY)) {
                        gea.setNextEvidenceQuantity();
                    } else if (buttonId.equals(GeaUi.BTN_ID_SHARE)) {
                        int code = gea.share(event.getMessage(), event.getInteraction());
                        event.getMessage().editMessageComponents(gea.getUi().createActionRows()).queue();
                        event.reply("Your friends can enter using this code: **" + code + "**")
                                .setEphemeral(true)
                                .queue();
                        return;
                    } else if (buttonId.equals(GeaUi.BTN_ID_JOIN)) {
                        event.replyModal(gea.getUi().getModalShare()).queue();
                        return;
                    } else if (buttonId.equals(GeaUi.BTN_ID_LEAVE)) {
                        if (gea.isShared()) {
                            gea.leave(new GEA.WrappedUser(event));
                            gea = new GEA(gea);
                        }
                    } else {
                        Evidence evidence = Evidence.getByName(buttonId.substring(GeaUi.ID_PREFIX.length()));
                        gea.setEvidence(evidence);
                    }

                }

                if (gea.isShared()) {
                    gea.editMessages(event.getUser());
                }
                Lang lang = new Lang(event);

                event.editMessageEmbeds(gea.getUi().createGeaEmbed(lang))
                        .setComponents(gea.getUi().createActionRows())
                        .queue();

                gea.interacted();
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (GeaController.active) {
            if (event.getModalId().equals(GeaUi.MODAL_ID_SHARE)) {
                List<Integer> ints = Util.parseIntsFromModal(event);
                if (ints.isEmpty()) {
                    event.reply("Malformed code :c")
                            .setEphemeral(true)
                            .queue();
                } else {
                    GEA gea = GEA.getByCode(ints.get(0));
                    if (gea != null) {
                        if (gea.isFull()) {
                            event.reply("Team full.")
                                    .setEphemeral(true)
                                    .queue();
                        } else {
                            Lang lang = new Lang(event);
                            gea.join(event.getMessage(), event.getInteraction());
                            event.editMessageEmbeds(gea.getUi().createGeaEmbed(lang))
                                    .setComponents(gea.getUi().createActionRows())
                                    .queue();
                            gea.interacted();
                        }
                    } else {
                        event.reply("Team not found.")
                                .setEphemeral(true)
                                .queue();
                    }
                }
            }
        }
    }
}