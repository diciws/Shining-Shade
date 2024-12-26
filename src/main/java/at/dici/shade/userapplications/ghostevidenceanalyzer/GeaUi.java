package at.dici.shade.userapplications.ghostevidenceanalyzer;

import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.utils.Resources;
import at.dici.shade.utils.phasmophobia.Evidence;
import at.dici.shade.utils.phasmophobia.Ghost;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.util.ArrayList;
import java.util.List;

public class GeaUi {
    private static final String LANG_PREFIX = "userapplications.gea.";
    static final String ID_PREFIX = "gea_";
    static final String BTN_RESET_ID_PREFIX = ID_PREFIX + "reset";
    static final String BTN_ID_EVIDENCE_QUANTITY = ID_PREFIX + "eqty";
    static final String BTN_ID_SPEED = ID_PREFIX + "speed";
    static final String BTN_ID_SHARE = ID_PREFIX + "share";
    static final String BTN_ID_JOIN = ID_PREFIX + "join";
    static final String BTN_ID_LEAVE = ID_PREFIX + "leave";
    static final String MODAL_ID_SHARE = ID_PREFIX + "share";
    private static final TextInput modalInputShare = TextInput
            .create(ID_PREFIX + "input_share", "Enter a code to join a team:", TextInputStyle.SHORT)
            .setMinLength(3)
            .setMaxLength(10)
            .setRequired(false)
            .build();

    private static final Button btnShare = Button.of(
            ButtonStyle.SECONDARY,
            BTN_ID_SHARE,
            "Share",
            Emoji.fromFormatted("\uD83D\uDC40")
    );

    private static final Button btnJoin = Button.of(
            ButtonStyle.SECONDARY,
            BTN_ID_JOIN,
            "Join",
            Emoji.fromFormatted("\uD83D\uDD11")
    );

    private static final Button btnLeave = Button.of(
            ButtonStyle.SECONDARY,
            BTN_ID_LEAVE,
            "Leave",
            Emoji.fromFormatted("\uD83C\uDF43")
    );

    private final GEA gea;

    GeaUi(GEA gea) {
        this.gea = gea;
    }

    Modal getModalShare() {
        return Modal
                .create(MODAL_ID_SHARE, "Join another GEA")
                .addActionRow(modalInputShare)
                .build();
    }
    MessageCreateAction createGeaMessage(Lang lang, PrivateChannel channel) {
        lang.setLineNamePrefix(LANG_PREFIX);
        return channel.sendMessage(
                        channel.getUser().getAsMention()
                                + " " + lang.getText("message_top"))
                .addEmbeds(createGeaEmbed(lang))
                .setComponents(createActionRows());
    }

    MessageEmbed createGeaEmbed(Lang lang) {
        lang.setLineNamePrefix(LANG_PREFIX);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Resources.color);
        embed.setTitle("Ghost Evidence Analyzer (GEA)");
        embed.setDescription(lang.getText("description_top")
                .replace("[green]", ":green_square:")
                .replace("[red]",":red_square:"));
        String sharedText;
        if (gea.isShared()) {
            StringBuilder sharedTextBuilder = new StringBuilder("\nYour Team: ");
            gea.getSessionMembers().forEach(
                    (user, msg) -> sharedTextBuilder
                            .append("\n**")
                            .append(user.user.getEffectiveName())
                            .append("**")
            );
            sharedText = sharedTextBuilder.toString();
            embed.appendDescription(sharedText);
        }
        displayGhosts(embed);
        return embed.build();
    }

    void displayGhosts(EmbedBuilder embed) {
        for (Ghost ghost : Ghost.values()) {
            String name;
            if (gea.isGhostCrossedOff(ghost)) name = "||~~" + ghost.getName() + "~~||";
            else name = "**" + ghost.getName() + "**";
            embed.addField(" ", name, true);
        }
    }

    List<ActionRow> createActionRows() {


        List<Button> row1 = new ArrayList<>();
        List<Button> row2 = new ArrayList<>();
        List<Button> row3 = new ArrayList<>();
        List<Button> row4 = new ArrayList<>();
        List<Button> row5 = new ArrayList<>();

        Button btnReset = Button.of(
                ButtonStyle.SECONDARY,
                BTN_RESET_ID_PREFIX + GeaStringifier.serialize(gea),
                "Reset");

        Button btnEvidenceQuantity = Button.of(
                ButtonStyle.SECONDARY,
                BTN_ID_EVIDENCE_QUANTITY,
                "Evidence: " + gea.getEvidenceQuantity());

        row1.add(createEvidenceButton(Evidence.EMF));
        row1.add(createEvidenceButton(Evidence.DOTS));
        row2.add(createEvidenceButton(Evidence.FINGERPRINTS));
        row2.add(createEvidenceButton(Evidence.ORB));
        row3.add(createEvidenceButton(Evidence.WRITING));
        row3.add(createEvidenceButton(Evidence.SPIRITBOX));
        row4.add(createEvidenceButton(Evidence.FREEZING));
        row4.add(createSpeedButton());
        row4.add(btnEvidenceQuantity);
        row5.add(btnShare);
        if (gea.isShared()) row5.add(btnLeave);
        else row5.add(btnJoin);
        row5.add(btnReset);



        return List.of(ActionRow.of(row1),
                ActionRow.of(row2),
                ActionRow.of(row3),
                ActionRow.of(row4),
                ActionRow.of(row5));
    }

    private Button createEvidenceButton(Evidence evidence) {
        ButtonStyle style;
        if (gea.getTrueEvidences().contains(evidence)) {
            style = ButtonStyle.SUCCESS;
        }

        else if (gea.getFalseEvidences().contains(evidence)) {
            style = ButtonStyle.DANGER;
        }

        else {
            style = ButtonStyle.PRIMARY;
        }

        return Button.of(style,
                ID_PREFIX + evidence.getName(),
                evidence.getName(),
                evidence.getEmoji());
    }

    private Button createSpeedButton() {
        if (gea.getSpeed() == Ghost.Speed.NORMAL) {
            return Button.of(
                    ButtonStyle.PRIMARY,
                    BTN_ID_SPEED,
                    "Speed: normal");
        }
        else if (gea.getSpeed() == Ghost.Speed.FAST) {
            return Button.of(
                    ButtonStyle.DANGER,
                    BTN_ID_SPEED,
                    "Speed: fast");
        }
        else if (gea.getSpeed() == Ghost.Speed.SLOW) {
            return Button.of(
                    ButtonStyle.SUCCESS,
                    BTN_ID_SPEED,
                    "Speed: slow");
        }
        else {
            return Button.of(
                    ButtonStyle.SECONDARY,
                    BTN_ID_SPEED,
                    "Speed: any");
        }
    }
}

