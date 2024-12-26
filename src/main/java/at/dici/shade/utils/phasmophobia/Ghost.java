package at.dici.shade.utils.phasmophobia;

import java.util.List;

public enum Ghost {
    SPIRIT("Spirit", List.of( Evidence.EMF, Evidence.SPIRITBOX, Evidence.WRITING ), null, List.of(Speed.NORMAL)),
    WRAITH("Wraith", List.of( Evidence.EMF, Evidence.SPIRITBOX, Evidence.DOTS ), null, List.of(Speed.NORMAL)),
    PHANTOM("Phantom", List.of( Evidence.FINGERPRINTS, Evidence.SPIRITBOX, Evidence.DOTS ), null, List.of(Speed.NORMAL)),
    POLTERGEIST("Poltergeist", List.of( Evidence.FINGERPRINTS, Evidence.SPIRITBOX, Evidence.WRITING ), null, List.of(Speed.NORMAL)),
    BANSHEE("Banshee", List.of( Evidence.FINGERPRINTS, Evidence.ORB, Evidence.DOTS ), null, List.of(Speed.NORMAL)),
    JINN("Jinn", List.of( Evidence.FINGERPRINTS, Evidence.FREEZING, Evidence.EMF ), null, List.of(Speed.NORMAL, Speed.FAST)),
    MARE("Mare", List.of( Evidence.WRITING, Evidence.SPIRITBOX, Evidence.ORB ), null, List.of(Speed.NORMAL)),
    REVENANT("Revenant", List.of( Evidence.FREEZING, Evidence.WRITING, Evidence.ORB ), null, List.of(Speed.FAST, Speed.SLOW)),
    SHADE("Shade", List.of( Evidence.FREEZING, Evidence.EMF, Evidence.WRITING ), null, List.of(Speed.NORMAL)),
    DEMON("Demon", List.of( Evidence.FINGERPRINTS, Evidence.FREEZING, Evidence.WRITING ), null, List.of(Speed.NORMAL)),
    YUREI("Yurei", List.of( Evidence.FREEZING, Evidence.ORB, Evidence.DOTS ), null, List.of(Speed.NORMAL)),
    ONI("Oni", List.of( Evidence.FREEZING, Evidence.EMF, Evidence.DOTS ), null, List.of(Speed.NORMAL)),
    YOKAI("Yokai", List.of( Evidence.ORB, Evidence.SPIRITBOX, Evidence.DOTS ), null, List.of(Speed.NORMAL)),
    HANTU("Hantu", List.of( Evidence.FINGERPRINTS, Evidence.ORB, Evidence.FREEZING ), Evidence.FREEZING, List.of(Speed.NORMAL, Speed.FAST, Speed.SLOW)),
    GORYO("Goryo", List.of( Evidence.FINGERPRINTS, Evidence.EMF, Evidence.DOTS ), Evidence.DOTS, List.of(Speed.NORMAL)),
    MYLING("Myling", List.of( Evidence.FINGERPRINTS, Evidence.EMF, Evidence.WRITING ), null, List.of(Speed.NORMAL)),
    ONRYO("Onryo", List.of( Evidence.FREEZING, Evidence.ORB, Evidence.SPIRITBOX ), null, List.of(Speed.NORMAL)),
    TWINS("The Twins", List.of( Evidence.EMF, Evidence.SPIRITBOX, Evidence.FREEZING ), null, List.of(Speed.NORMAL, Speed.FAST, Speed.SLOW)),
    RAIJU("Raiju", List.of( Evidence.ORB, Evidence.EMF, Evidence.DOTS ), null, List.of(Speed.NORMAL, Speed.FAST)),
    OBAKE("Obake", List.of( Evidence.FINGERPRINTS, Evidence.ORB, Evidence.EMF ), Evidence.FINGERPRINTS, List.of(Speed.NORMAL)),
    MIMIC("The Mimic", List.of( Evidence.FINGERPRINTS, Evidence.SPIRITBOX, Evidence.FREEZING, Evidence.ORB ), Evidence.ORB, List.of(Speed.NORMAL, Speed.FAST, Speed.SLOW)),
    MOROI("Moroi", List.of( Evidence.WRITING, Evidence.SPIRITBOX, Evidence.FREEZING ), Evidence.SPIRITBOX, List.of(Speed.NORMAL, Speed.FAST, Speed.SLOW)),
    DEOGEN("Deogen", List.of( Evidence.WRITING, Evidence.SPIRITBOX, Evidence.DOTS ), Evidence.SPIRITBOX, List.of(Speed.FAST, Speed.SLOW)),
    THAYE("Thaye", List.of( Evidence.ORB, Evidence.WRITING, Evidence.DOTS ), null, List.of(Speed.NORMAL, Speed.FAST, Speed.SLOW));

    private final String name;
    private final List<Evidence> evidences;
    private final Evidence guaranteedEvidence;
    private final List<Speed> speeds;


    Ghost(String name, List<Evidence> evidences, Evidence guaranteedEvidence, List<Speed> speeds) {
        this.name = name;
        this.evidences = evidences;
        this.guaranteedEvidence = guaranteedEvidence;
        this.speeds = speeds;
    }

    public static Ghost getByName(String name) {
        for (Ghost ghost : values()) {
            if (ghost.getName().equals(name)) return ghost;
        }
        return null;
    }
    public String getName() {
        return name;
    }

    /**
     * Creates a new List for each call!
     * @return The list of evidences.
     */
    public List<Evidence> getEvidences() {
        return List.copyOf(evidences);
    }

    public Evidence getGuaranteedEvidence() {
        return guaranteedEvidence;
    }

    public boolean hasEvidence(Evidence evidence){
        return evidences.contains(evidence);
    }
    public boolean canHaveSpeed(Speed speed) {
        return speeds.contains(speed);
    }
    public enum Speed{
        SLOW, NORMAL, FAST
    }
}
