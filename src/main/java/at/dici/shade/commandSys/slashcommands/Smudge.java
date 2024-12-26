package at.dici.shade.commandSys.slashcommands;

import at.dici.shade.commandSys.SlashCommand;
import at.dici.shade.core.databaseio.Lang;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Smudge extends SlashCommand {
    private static final String LANG_PREFIX = "commands.smudgetimer.";
    private static final HashMap<Long, MsgTasks> timersMap = new HashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static final SlashCommandData data;
    static {
        data = Commands.slash("smudge", "Starts a timer to tell you when each ghost will be able to hunt again.")
                .setDescriptionLocalization(DiscordLocale.GERMAN,"Startet einen Timer und sagt dir, wann welcher Geist wieder hunten kann.");
    }
    @Override
    public SlashCommandData getData() {
        return data;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        Lang lang = new Lang(LANG_PREFIX, event.getInteraction());


        event.reply(lang.getText("reply")).queue();


        long userId = user.getIdLong();
        MsgTasks msgTasks = timersMap.get(userId);
        if (msgTasks == null) {
            timersMap.put(userId, new MsgTasks(event));
        } else {
            msgTasks.restart(event);
        }
    }

    private static class MsgTasks{
        ScheduledFuture<?> demonMsgFuture;
        ScheduledFuture<?> othersMsgFuture;
        ScheduledFuture<?> spiritMsgFuture;
        ScheduledFuture<?> demonTypingFuture;
        ScheduledFuture<?> othersTypingFuture;
        ScheduledFuture<?> spiritTypingFuture;
        ScheduledFuture<?> removeEntryFuture;
        MessageChannel channel;
        User user;
        Lang lang;
        MsgTasks(SlashCommandInteractionEvent event){
            init(event);
        }

        private void init(SlashCommandInteractionEvent event){
            channel = event.getChannel();
            user = event.getUser();
            lang = new Lang(LANG_PREFIX, event.getInteraction());
            schedule();
        }

        private void schedule(){
            demonMsgFuture = channel
                    .sendMessage(user.getAsMention() + " " + lang.getText("after_60s"))
                    .queueAfter(60L, TimeUnit.SECONDS, null, t -> {}); // ignore errors at this point
            othersMsgFuture = channel
                    .sendMessage(user.getAsMention() + " " + lang.getText("after_90s"))
                    .queueAfter(90L, TimeUnit.SECONDS, null, t -> {});
            spiritMsgFuture = channel
                    .sendMessage(user.getAsMention() + " " + lang.getText("after_180s"))
                    .queueAfter(180L, TimeUnit.SECONDS, null, t -> {});
            demonTypingFuture = channel.sendTyping().queueAfter(55L, TimeUnit.SECONDS, null, t -> {});
            othersTypingFuture = channel.sendTyping().queueAfter(85L, TimeUnit.SECONDS, null, t -> {});
            spiritTypingFuture = channel.sendTyping().queueAfter(175L, TimeUnit.SECONDS, null, t -> {});
            removeEntryFuture = scheduler.schedule(() -> {
                timersMap.remove(user.getIdLong());
            }, 190L, TimeUnit.SECONDS);
        }
        void restart(SlashCommandInteractionEvent event){
            cancel();
            init(event);
        }

        void cancel(){
            demonMsgFuture.cancel(false);
            othersMsgFuture.cancel(false);
            spiritMsgFuture.cancel(false);
            demonTypingFuture.cancel(false);
            othersTypingFuture.cancel(false);
            spiritTypingFuture.cancel(false);
            removeEntryFuture.cancel(false);
        }
    }
}
