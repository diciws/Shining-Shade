package at.dici.shade.userapplications.ghostevidenceanalyzer;

import at.dici.shade.core.databaseio.Lang;
import at.dici.shade.utils.Util;
import at.dici.shade.utils.log.Logger;
import at.dici.shade.utils.phasmophobia.Evidence;
import at.dici.shade.utils.phasmophobia.Ghost;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.*;
import java.util.concurrent.*;

public class GEA {

    static final String LOG_PREFIX = "GEA: ";
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledFuture<?> inactiveCheck = executor
            .scheduleWithFixedDelay(createCloseInactiveTask(), 1, 1, TimeUnit.HOURS);
    private static final HashMap<Integer, GEA> sharedSessions = new HashMap<>();
    private static final HashMap<Long, Integer> sessionCodes = new HashMap<>();
    private final ConcurrentHashMap<WrappedUser, Message> sessionMembers = new ConcurrentHashMap<>();

    private ScheduledFuture<?> sendMessagesTask = null;
    private int code;
    private long lastInteraction;
    private final GeaUi ui;
    private final List<Evidence> trueEvidences;
    private final List<Evidence> falseEvidences;
    private Ghost.Speed speed;
    private int evidenceQuantity;

    GEA() {
        trueEvidences = new ArrayList<>();
        falseEvidences = new ArrayList<>();
        reset();
        ui = new GeaUi(this);
    }

    GEA(List<Evidence> trueEvidences, List<Evidence> falseEvidences, Ghost.Speed speed, int evidenceQuantity) {
        this.trueEvidences = trueEvidences;
        this.falseEvidences = falseEvidences;
        this.speed = speed;
        this.evidenceQuantity = evidenceQuantity;
        ui = new GeaUi(this);
    }

    GEA(GEA gea) {
        trueEvidences = new ArrayList<>(gea.getTrueEvidences());
        falseEvidences = new ArrayList<>(gea.getFalseEvidences());
        speed = gea.getSpeed();
        evidenceQuantity = gea.getEvidenceQuantity();
        ui = new GeaUi(this);
    }

    private Runnable createSendMessageTask() {
        return () -> {
            int edits = 0;
            for (Map.Entry<WrappedUser, Message> entry : sessionMembers.entrySet()) {
                if (entry.getKey().refreshMessage) {
                    edits++;
                    entry.getKey().refreshMessage = false;
                    editSingleMessage(entry.getKey(), entry.getValue());
                }
            }
            if (edits != 0) Logger.debug("GEA editing " + edits + " messages.");
        };
    }


    /**
     * Only for debug
     * @return debug info → Hashmap sizes
     */
    static String getSessionInfo() {
        return "sharedSessions: " + sharedSessions.size()
                + " | sessionCodes (User count): " + sessionCodes.size();
    }


    // TODO: Must work with unshare() Method!!!
    static Runnable createCloseInactiveTask() {
        return () -> {
            List<Integer> codes = new ArrayList<>();
            sharedSessions.forEach(
                    (code, gea) -> {
                        long activityThreshold;
                        if (gea.sessionMembers.size() > 1) {
                            activityThreshold = System.currentTimeMillis() - GeaController.maxInactivity;
                        } else {
                            activityThreshold = System.currentTimeMillis() - (GeaController.maxInactivity / 8);
                        }

                        if (gea.lastInteraction < activityThreshold) {
                            codes.add(code);
                        }
                    }
            );
            for (int code : codes) {
                GEA gea = sharedSessions.remove(code);
                if (gea != null) { // should always be true
                    gea.unshare();
                }
            }
        };
    }

    void reset() {
        trueEvidences.clear();
        falseEvidences.clear();
        speed = null;
        evidenceQuantity = 3;
    }
    int share(Message message, Interaction interaction) {
        WrappedUser user = new WrappedUser(interaction);
        if (isShared()) return code;
        sessionMembers.put(user, message);
        int code = generateCode();
        sessionCodes.put(user.getId(), code);
        sharedSessions.put(code, this);
        sendMessagesTask = executor
                .scheduleWithFixedDelay(
                        createSendMessageTask(),
                        GeaController.refreshInterval,
                        GeaController.refreshInterval,
                        TimeUnit.MILLISECONDS);
        Logger.info(LOG_PREFIX + "User " + interaction.getUser().getName() + " sharing: " + code);
        return code;
    }

    void join(Message message, Interaction interaction) {
        WrappedUser user = new WrappedUser(interaction);
        if (sessionMembers.containsKey(user)) {
            // point to new message
            sessionMembers.put(user, message);
            return;
        }
        Integer oldSessionCode = sessionCodes.get(user.getId());
        if (oldSessionCode != null) {
            GEA oldGea = sharedSessions.get(oldSessionCode);
            if (oldGea != null) oldGea.leave(user);
        }
        sessionMembers.put(user, message);
        sessionCodes.put(user.getId(), code);
        editMessages(user.user);
        Logger.info(LOG_PREFIX + "User " + interaction.getUser().getName() + " joined: " + code);
    }

    void leave(WrappedUser user) {
        sessionMembers.remove(user);
        sessionCodes.remove(user.getId());

        editMessages();

    }

    void unshare() {
        sharedSessions.remove(code);
        getSessionMembers().forEach(
                ((wrappedUser, message) -> sessionCodes.remove(wrappedUser.getId()))
        );
        Logger.info(LOG_PREFIX + "Session ended: " + code);
        if (sendMessagesTask != null) {
            sendMessagesTask.cancel(false);
        }
        if (sessionMembers.size() != 0) {
            editMessages();
        }
    }

    boolean isShared() {
        return sharedSessions.containsKey(code);
    }
    static GEA getShared(User user) {
        Integer code = sessionCodes.get(user.getIdLong());
        if (code == null) return null;
        return sharedSessions.get(code);
    }

    boolean isFull() {
        return sessionMembers.size() >= GeaController.maxTeamSize;
    }

    static GEA getByCode(int code) {
        return sharedSessions.get(code);
    }

    public ConcurrentHashMap<WrappedUser, Message> getSessionMembers() {
        return sessionMembers;
    }

    private int generateCode() {
        if (sharedSessions.size() > 10000) {
            Logger.warn(LOG_PREFIX + "Too many GEA sessions! Something is wrong!");
        }
        do {
            code = 10000 + Util.getRandomInt(90000);
        } while (sharedSessions.containsKey(code));
        return code;
    }

    void interacted() {
        lastInteraction = System.currentTimeMillis();
    }

    void editMessages() {
        sessionMembers.forEach(((wrappedUser, message) -> queueMessageEdit(wrappedUser)));
    }


    void editMessages(User excludedUser) {
        sessionMembers.forEach((user, message) -> {
            if (excludedUser.getIdLong() != user.getId()) {
                queueMessageEdit(user);
            }
        });
    }

    void queueMessageEdit(WrappedUser user) {
        user.refreshMessage = true;
    }
    void editSingleMessage(WrappedUser user, Message message) {
        message.editMessageEmbeds(ui.createGeaEmbed(user.lang))
                .setComponents(ui.createActionRows())
                .queue();
    }

    GeaUi getUi() {
        return ui;
    }

    List<Evidence> getTrueEvidences() {
        return trueEvidences;
    }

    List<Evidence> getFalseEvidences() {
        return falseEvidences;
    }

    Ghost.Speed getSpeed() {
        return speed;
    }

    int getEvidenceQuantity() {
        return evidenceQuantity;
    }

    void setEvidence(Evidence evidence) {
        if (evidence == null) return;
        if (trueEvidences.contains(evidence)) {
            trueEvidences.remove(evidence);
            falseEvidences.add(evidence);
        } else if (falseEvidences.contains(evidence)) {
            falseEvidences.remove(evidence);
        } else {
            trueEvidences.add(evidence);
        }
    }

    void setNextSpeed() {
        if (speed == null) speed = Ghost.Speed.NORMAL;
        else if (speed == Ghost.Speed.NORMAL) speed = Ghost.Speed.FAST;
        else if (speed == Ghost.Speed.FAST) speed = Ghost.Speed.SLOW;
        else speed = null;
    }

    void setNextEvidenceQuantity() {
        if (evidenceQuantity > 0) evidenceQuantity--;
        else evidenceQuantity = 3;
    }
    boolean isGhostCrossedOff(Ghost ghost) {
        if (speed != null && !ghost.canHaveSpeed(speed)) {
            return true;
        }

        if (trueEvidences == null || falseEvidences == null) { // shouldn't happen
            return false;
        }

        if (trueEvidences.size() > (evidenceQuantity)) {
            if (ghost != Ghost.MIMIC) return true;
            else {
                if (trueEvidences.size() > (evidenceQuantity + 1)) return true;
                if (!trueEvidences.contains(Evidence.ORB)) return true;
            }
        }

        for (Evidence e : trueEvidences) {
            if (!ghost.getEvidences().contains(e)) return true;
        }

        if (falseEvidences.contains(ghost.getGuaranteedEvidence())
                && evidenceQuantity != 0) return true;


        if (evidenceQuantity == 0) {
            if (!trueEvidences.isEmpty()) {
                if (!trueEvidences.contains(Evidence.ORB)) return true;
            }

            if (falseEvidences.contains(Evidence.ORB)
                    && ghost == Ghost.MIMIC) return true;


        } else if (evidenceQuantity == 1) {
            for (Evidence e : trueEvidences) {
                if (!ghost.hasEvidence(e)) return true;
                else if (ghost.getGuaranteedEvidence() != null) {
                    if (ghost.getGuaranteedEvidence() != e) {
                        if (ghost != Ghost.MIMIC) return true;

                    }
                }
            }
            if (new HashSet<>(falseEvidences).containsAll(ghost.getEvidences())) return true;


        } else if (evidenceQuantity == 2) {
            int ctrMissing = 0;
            for (Evidence e : falseEvidences) {
                if (ghost.hasEvidence(e)) ctrMissing++;
            }
            if (ctrMissing >= 2) return true;

            int ctrMatching = 0;
            for (Evidence e : trueEvidences) {
                if (ghost.hasEvidence(e)) ctrMatching++;
            }
            if (ctrMatching == ghost.getEvidences().size() - 1
                    && ghost.getGuaranteedEvidence() != null
                    && !trueEvidences.contains(ghost.getGuaranteedEvidence())) {
                return true;
            }

        } else if (evidenceQuantity == 3) {
            for (Evidence e : trueEvidences) {
                if (!ghost.hasEvidence(e)) return true;
            }
            for (Evidence e : falseEvidences) {
                if (ghost.hasEvidence(e)) return true;
            }
        } else return false; // should never happen

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof GEA gea) {
            return code == gea.code;
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    static class WrappedUser {
        final User user;
        Lang lang;
        boolean refreshMessage = false;
        WrappedUser(Interaction interaction) {
            this.user = interaction.getUser();
            lang = new Lang(interaction);
        }

        public long getId() {
            return user.getIdLong();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o instanceof WrappedUser u) {
                return getId() == u.getId();
            }
            else return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(user.getId());
        }
    }
}