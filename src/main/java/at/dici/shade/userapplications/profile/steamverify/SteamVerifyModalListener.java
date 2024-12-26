package at.dici.shade.userapplications.profile.steamverify;

import at.dici.shade.core.cache.SteamVerifyCache;
import at.dici.shade.core.cache.UserCache;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.core.handler.SteamVerifyHandler;
import at.dici.shade.core.handler.UserHandler;
import at.dici.shade.core.mysql.MySqlClass;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.List;

public class SteamVerifyModalListener extends ListenerAdapter {

    private static final String SV_MODAL_ID = "sv_code_modal";


    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals(SV_MODAL_ID)) {
            List<ModalMapping> vals = event.getValues();
            if (vals.isEmpty()) {
                event.reply("No code entered.").setEphemeral(true).queue();
                Logger.error("SV: ModalInteractionEvent failed. Empty vals");
                return;
            }
            String code = vals.get(0).getAsString();
            String userid = event.getUser().getId();

            if ( !SteamVerifyCache.getInstance().isExisting( userid ) ) {
                //gibts noch nicht
                //get code
                if (!MySqlClass.getInstance().codeExits(code)) {
                    event.reply("Kenne diesen Code nicht.").setEphemeral(true).queue();
                }else{
                    SteamVerifyHandler.setdiscordid( userid, code, success ->
                            Logger.log(LogLevel.INFORMATION,"BETA: Steam-Verification enabled for id: " + userid)
                    );
                    event.reply("Congrats du bist nun verifiziert.").setEphemeral(true).queue();
                }

            }else{
                //gibts schon
                event.reply("Bist bereits verifiziert.").setEphemeral(true).queue();
            }



        }
    }

    static Modal getModal(Lang lang) {
        TextInput modalInputCode = TextInput
                .create("sv_code_input", lang.getText("userapplication.steamverify.modal_input"), TextInputStyle.SHORT)
                .setMinLength(3)
                .setMaxLength(10)
                .setRequired(true)
                .build();

        return Modal
                .create(SV_MODAL_ID, "userapplication.steamverify.modal_title")
                .addActionRow(modalInputCode)
                .build();
    }
}
