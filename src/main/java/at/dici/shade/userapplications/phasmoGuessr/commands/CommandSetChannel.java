package at.dici.shade.userapplications.phasmoGuessr.commands;

import at.dici.shade.core.databaseio.GuildIo;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.userapplications.phasmoGuessr.PgSettings;
import at.dici.shade.userapplications.phasmoGuessr.PgUi;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class CommandSetChannel {
    public static final String COMMAND_NAME = "pgsetchannel";
    private static final String LANG_PREFIX = "userapplications.phasmoguessr.commands.CommandSetChannel.";
    public static SlashCommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, "Set the channel which will be used for PhasmoGuessr")
                .setGuildOnly(true)
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Legt den Channel fest, der für PhasmoGuessr verwendet wird")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    public static void perform(SlashCommandInteractionEvent event){
        Lang lang = new Lang(LANG_PREFIX, event.getInteraction());
        PgUi ui = new PgUi(lang);

        MessageEmbed embed = ui.getEmbedSetChannel(event.getJDA());


        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();

        try {
            event.getChannel().sendMessageEmbeds(embed)
                    .addActionRow(Button.success(PgSettings.BUTTON_ID_START, "Start"))
                    .queue(
                            m -> {
                                GuildIo.setPhasmoGuessrChannel(event.getChannel().getIdLong(), event.getGuild());
                                hook.editOriginal(ui.getTextSetChannelSuccess()).queue();
                                Logger.log(LogLevel.INFORMATION, "PhasmoGuessr Channel set!" +
                                        " G: " + event.getGuild().getName() +
                                        " Ch: " + event.getChannel().getName());
                            },
                            t -> {
                                Logger.log(LogLevel.INFORMATION, "PhasmoGuessr Channel set failed!" +
                                        " G: " + event.getGuild().getName() +
                                        " Ch: " + event.getChannel().getName() +
                                        " Error Message: " + t.getMessage());
                                hook.editOriginal(ui.getTextSetChannelError()).queue();
                            }
                    );
        } catch (Exception e) {
            hook.editOriginal(ui.getTextSetChannelError()).queue();
        }
    }
}
