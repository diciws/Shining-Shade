package at.dici.shade.userapplications.controlpanel;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;


public class CpUiElements {
    final static String ID_PREFIX = "controlpanel_";
    public final static String ID_MENU_USER_RANKS = ID_PREFIX + "menu_user_ranks";

    // MAIN PAGE BUTTONS
    final static Button btnReloadControlPanel = Button.of(
            ButtonStyle.SUCCESS,
            ID_PREFIX + "reload_panel",
            "Panel",
            Emoji.fromFormatted("\uD83D\uDD04")
    );

    final static Button btnCommandPrompt = Button.of(
            ButtonStyle.DANGER,
            ID_PREFIX + "command_prompt",
            "Cmd Prompt",
            Emoji.fromFormatted("\uD83D\uDCE5")
    );

    final static Button btnReloadLangCache = Button.of(
            ButtonStyle.DANGER,
            ID_PREFIX + "reload_lang_cache",
            "Lang DB",
            Emoji.fromFormatted("\uD83D\uDD04")
    );
    final static Button btnReloadUserCache = Button.of(
            ButtonStyle.DANGER,
            ID_PREFIX + "reload_user_cache",
            "User DB",
            Emoji.fromFormatted("\uD83D\uDD04")
    );
    final static Button btnReloadPgCache = Button.of(
            ButtonStyle.DANGER,
            ID_PREFIX + "reload_pg_cache",
            "PG DB",
            Emoji.fromFormatted("\uD83D\uDD04")
    );
    final static Button btnRestartTwitchBot = Button.of(
            ButtonStyle.PRIMARY,
            ID_PREFIX + "restart_twitch_bot",
            "Restart TTV",
            Emoji.fromFormatted("\uD83D\uDD04")
    );
    final static Button btnTwitchBotCapacityTest = Button.of(
            ButtonStyle.PRIMARY,
            ID_PREFIX + "twitch_bot_capacity_test",
            "TTV P-Test",
            Emoji.fromFormatted("\uD83D\uDDA8")
    ); // ⚠
    final static Button btnEditUser = Button.of(
            ButtonStyle.SUCCESS,
            ID_PREFIX + "edit_user",
            "Edit User",
            Emoji.fromFormatted("➕")
    );
    final static Button btnReloadCommands = Button.of(
            ButtonStyle.PRIMARY,
            ID_PREFIX + "reload_commands",
            "Commands",
            Emoji.fromFormatted("\uD83D\uDD04")
    );
    final static Button btnDeleteCommand = Button.of(
            ButtonStyle.DANGER,
            ID_PREFIX + "delete_command",
            "Command",
            Emoji.fromFormatted("➖")
    );
    final static Button btnPgSave = Button.of(
            ButtonStyle.DANGER,
            ID_PREFIX + "pg_save",
            "PG Save",
            Emoji.fromFormatted("\uD83D\uDCE5")
    );
    final static Button btnPgLoad = Button.of(
            ButtonStyle.DANGER,
            ID_PREFIX + "pg_load",
            "PG Load",
            Emoji.fromFormatted("\uD83D\uDCE4")
    );
    final static Button btnPrintGuilds = Button.of(
            ButtonStyle.SUCCESS,
            ID_PREFIX + "print_guilds",
            "Print Guilds",
            Emoji.fromFormatted("\uD83D\uDDA8")
    );
    final static Button btnPrintTtvChannels = Button.of(
            ButtonStyle.SUCCESS,
            ID_PREFIX + "print_ttv_channels",
            "Print TTV Ch",
            Emoji.fromFormatted("\uD83D\uDDA8")
    );

    // SECONDARY BUTTONS
    final static Button btnEditUserPoints = Button.of(
            ButtonStyle.SUCCESS,
            ID_PREFIX + "edit_user_points",
            "Points",
            Emoji.fromFormatted("\uD83D\uDCCA")
    );


    // MODALS
    final static TextInput inputCommandPromptModule = TextInput
            .create(ID_PREFIX + "input_command_prompt_module", "Module", TextInputStyle.SHORT)
            .setMinLength(1)
            .setRequired(true)
            .build();
    final static TextInput inputCommandPromptName = TextInput
            .create(ID_PREFIX + "input_command_prompt_name", "Command", TextInputStyle.SHORT)
            .setMinLength(1)
            .setRequired(true)
            .build();
    final static TextInput inputCommandPromptValue = TextInput
            .create(ID_PREFIX + "input_command_prompt_value", "Value", TextInputStyle.SHORT)
            .setMinLength(1)
            .setRequired(true)
            .build();

    final static Modal modalCommandPrompt = Modal
            .create(ID_PREFIX + "modal_command_prompt", "Command Prompt")
            .addActionRow(inputCommandPromptModule)
            .addActionRow(inputCommandPromptName)
            .addActionRow(inputCommandPromptValue)
            .build();

    final static TextInput inputUserId = TextInput
            .create(ID_PREFIX + "input_user_id", "User-ID", TextInputStyle.SHORT)
            .setMinLength(10)
            .setRequired(true)
            .build();
    final static Modal modalInputUserId = Modal
            .create(ID_PREFIX + "modal_input_userid", "Gib die ID des Users ein")
            .addActionRow(inputUserId)
            .build();

    final static TextInput inputSetUserPoints = TextInput
            .create(ID_PREFIX + "input_set_user_points", "Set points", TextInputStyle.SHORT)
            .setMinLength(1)
            .setMaxLength(9)
            .setRequired(false)
            .build();
    final static TextInput inputAddUserPoints = TextInput
            .create(ID_PREFIX + "input_add_user_points", "Add points", TextInputStyle.SHORT)
            .setMinLength(1)
            .setMaxLength(9)
            .setRequired(false)
            .build();

    final static Modal modalEditUserPoints = Modal
            .create(ID_PREFIX + "modal_input_userid_edit_points", "Gib die ID des Users ein")
            .addActionRow(inputSetUserPoints)
            .addActionRow(inputAddUserPoints)
            .build();


}
