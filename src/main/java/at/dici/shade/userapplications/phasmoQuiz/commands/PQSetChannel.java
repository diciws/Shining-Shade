package at.dici.shade.userapplications.phasmoQuiz.commands;

import at.dici.shade.core.databaseio.GuildIo;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class PQSetChannel {
    public static final String COMMAND_NAME = "pqsetchannel";
    private static final String LANG_PREFIX = "userapplications.phasmoquiz.commands.pqsetchannel.";
    public static SlashCommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, "Set the channel which will be used for PhasmoQuiz")
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Legt den Channel fest, der für PhasmoQuiz verwendet wird")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    public static void perform(SlashCommandInteractionEvent event){
        Lang lang = new Lang(LANG_PREFIX, event.getInteraction());

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("PhasmoQuiz")
                .setDescription(lang.getText("welcome_text"))
                .build();


        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();

        try {
            event.getChannel().sendMessageEmbeds(embed)
                    .addActionRow(Button.success("pq_start_Button", "Start"))
                    .queue(
                            m -> {
                                GuildIo.setPhasmoQuizChannel(event.getChannel().getIdLong(), event.getGuild());
                                hook.editOriginal(lang.getText("reply_success")).queue();
                                Logger.log(LogLevel.INFORMATION, "PhasmoQuiz Channel set!" +
                                        " G: " + event.getGuild().getName() +
                                        " Ch: " + event.getChannel().getName());
                            },
                            t -> {
                                Logger.log(LogLevel.DEBUG, "(PQ BUG) .queue error");
                                hook.editOriginal(lang.getText("reply_error")).queue();
                                Logger.log(LogLevel.INFORMATION, "PhasmoQuiz Channel set failed!" +
                                        " G: " + event.getGuild().getName() +
                                        " Ch: " + event.getChannel().getName() +
                                        " Error Message: " + t.getMessage());
                            }
                    );
        } catch (Exception e) {
            hook.editOriginal(lang.getText("reply_error")).queue();
        }
    }
}
