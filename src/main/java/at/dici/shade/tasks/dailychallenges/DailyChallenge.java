package at.dici.shade.tasks.dailychallenges;


import at.dici.shade.tasks.dailychallenges.cache.DCCache;
import at.dici.shade.utils.Resources;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DailyChallenge {

    public static HashMap<String, Boolean> ClaimedDC = new HashMap<>();
    public static HashMap<String, Boolean> FinishedDC = new HashMap<>();
    public static HashMap<String, Integer> ActiveChallenge = new HashMap<>();

    public static int hours = 11;
    public static int minutes = 59;
    public static int seconds = 59;

    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);

    public void startDailyChallenge( ) {
        /**
         * not using the taskHandle returned here, but it can be used to cancel
         * the task, or check if it's done (for recurring tasks, that's not
         * going to be very useful)
         **/
        scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        getChallengeDB();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.log(LogLevel.FATAL, "Daily Challenge scheduler Issue");
                    }
                }, 0, returnNextChallenge(), TimeUnit.HOURS);
    }

    private void getChallengeDB( ) {
        //clear old maps
        if(!ClaimedDC.isEmpty()) ClaimedDC.clear();
        if(!FinishedDC.isEmpty()) FinishedDC.clear();
        if(!ActiveChallenge.isEmpty()) ActiveChallenge.clear();

        //randomize new number (DailyChallenge)
        int min = 1;
        int max = DCCache.getMaxChallengeSize();
        int randChallenge = min + (int)(Math.random() * ((max - min) + 1));

        ActiveChallenge.put("active", randChallenge);
        DateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        Logger.log(LogLevel.DEBUG, "New daily Challenge - Date: " + formatter.format(new Date()) + " - " + DCCache.getDailyChallengetitle(ActiveChallenge.get("active")));

    }

    public static String returnRemainingDCTime(){
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime endTime = LocalDate.now(zone)
                .plusDays(1)
                .atTime(LocalTime.of(hours, minutes, seconds))
                .atZone(zone);
        ZonedDateTime now = ZonedDateTime.now(zone);
        Duration remainingTime = Duration.between(now, endTime);

        return remainingTime.toHours()+":"+remainingTime.toMinutesPart()+":"+remainingTime.toSecondsPart();
    }

    public static long returnNextChallenge(){
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime endTime = LocalDate.now(zone)
                .plusDays(1)
                .atTime(LocalTime.of(hours, minutes, seconds))
                .atZone(zone);
        ZonedDateTime now = ZonedDateTime.now(zone);
        Duration remainingTime = Duration.between(now, endTime);

        return remainingTime.toHours();
    }

    public static void getFinishedDCtext(SlashCommandInteractionEvent event){

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Daily Challenge: ");
        embed.setDescription(":tada::tada: Herzlichen Glückwunsch du hast die Challenge: \n\n" +
                "**"+DCCache.getDailyChallengetitle(ActiveChallenge.get("active"))+"**"+
                "\n\n" +
                "" +
                " erfolgreich abgeschlossen! Hier 200 Punkte! ✨");
        embed.setFooter("Nächste Daily Challenge in "+returnRemainingDCTime()+" verfügbar!");
        embed.setColor(Resources.color);

        event.getInteraction().getMessageChannel().sendMessageEmbeds(embed.build()).queue();

    }

}