package at.dici.shade.utils.phasmophobia;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum Evidence {
    EMF(1, "EMF Level 5", Emoji.fromCustom("EMF",1036193647294689320L,false)),
    DOTS(2, "D.O.T.S.", Emoji.fromCustom("DOTS",1034098733656309820L,false)),
    FINGERPRINTS(3, "Fingerprints", Emoji.fromCustom("Finger",1034126276748902551L,false)),
    ORB(4, "Ghost Orb", Emoji.fromCustom("Orb",1033395909989507072L,false)),
    WRITING(5, "Ghost Writing", Emoji.fromCustom("Buch",1034512744449318986L,false)),
    SPIRITBOX(6, "Spirit Box", Emoji.fromCustom("Spritbox",1034068039991300146L,false)),
    FREEZING(7, "Freezing", Emoji.fromCustom("Freezing",1034120015412215929L,false));
    private final int id;
    private final String name;
    private final Emoji emoji;

    Evidence(int id, String name, Emoji emoji) {
        this.name = name;
        this.emoji = emoji;
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public static Evidence getById(int id){
        for(Evidence e : values()){
            if (e.getId() == id) return e;
        }
        return null;
    }

    public static Evidence getByName(String name){
        for(Evidence e : values()){
            if (e.getName().equals(name)) return e;
        }
        return null;
    }

}
