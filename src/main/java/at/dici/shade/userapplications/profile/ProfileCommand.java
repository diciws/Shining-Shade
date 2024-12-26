package at.dici.shade.userapplications.profile;

import at.dici.shade.commandSys.SlashCommand;
import at.dici.shade.core.cache.SteamVerifyCache;
import at.dici.shade.core.cache.UserCache;
import at.dici.shade.core.databaseio.Rank;
import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.utils.Resources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.awt.*;

public class ProfileCommand extends SlashCommand {
    public static final String COMMAND_NAME = "profile";
    public static final String LANG_PREFIX = "commands.profile.";

    @Override
    public SlashCommandData getData() {
        return Commands.slash("profile","Shows your profile")
                .setDescriptionLocalization(DiscordLocale.GERMAN, "Öffnet dein Profil");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Lang lang = new Lang(LANG_PREFIX, event.getInteraction());
        ReplyCallbackAction reply = event.replyEmbeds(createEmbed(event.getUser(), lang));

        if (Rank.isAnyRankPresent(event.getUser())) {
            reply.addActionRow(ProfileButtonListener.btnNextRank.withLabel(lang.getText("button_label_switch_rank")));
        }

        reply.queue();
    }

    static MessageEmbed createEmbed(User user, Lang lang){
        Rank rank = Rank.getActiveRank(user);
        return createEmbed(user, lang, rank);
    }

    static MessageEmbed createEmbed(User user, Lang lang, Rank rank) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (rank == null) embedBuilder.setColor(Resources.color);
        else embedBuilder.setColor(Color.decode(rank.getColor()));
        embedBuilder.setTitle(":pencil: " + lang.getText("profile_title"));
        embedBuilder.setDescription(lang.getText("desc").replace("[UserAsMentioned]", user.getAsMention()) + ": ");

        embedBuilder.addField("\uD83E\uDEAA " + lang.getText("username") + ": ", Rank.getFormatted(rank, user.getName(), true, true), true);
        embedBuilder.addField(":pencil2: " + lang.getText("points") + ": ", "``` " + UserCache.getPoints(user.getId()) + "```", true);

        if (SteamVerifyCache.getInstance().isExisting(user.getId())) {
            int hours = SteamVerifyCache.getPhasmoGameMin(user.getId()) / 60;
            if (SteamVerifyCache.getPhasmoGame(user.getId()).equals("Yes")) {
                embedBuilder.addField(":ghost: Phasmo hours (beta):", "``` " + hours + " ```", true);
            }

//            if (SteamVerifyCache.getPhasmoGame(user.getId()).equals("Yes")) {
//                embedBuilder.addField(":white_check_mark: (Beta-Test) Phasmo: ", "``` yes ```", true);
//            } else {
//                embedBuilder.addField(":x: (Beta-Test) Phasmo: ", "``` no ```", true);
//            }
//
//            int hours = SteamVerifyCache.getPhasmoGameMin(user.getId()) / 60;
//
//            embedBuilder.addField(":ghost: (Beta-Test) Phasmo hours: ", "``` " + hours + " ```", true);

        }

        return embedBuilder.build();
    }
}
