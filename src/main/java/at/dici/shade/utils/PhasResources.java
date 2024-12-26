package at.dici.shade.utils;

import java.util.HashMap;

public class PhasResources {

    public static String[] challengesDebuff = {
            "challenges.nosanity",
            "challenges.nohiding",
            "challenges.fastghost",
            "challenges.goslow",
            "challenges.nofoto",
            "challenges.lockedin",
            "challenges.noelectriclight",
            "challenges.lessevidence",
            "challenges.profipictures",
            "challenges.oldscool",
            "challenges.nostealth",
            "challenges.nowkey",
            "challenges.sitdown",
            "challenges.staythere",
            "challenges.bronzestatue",
            "challenges.silverstatue",
            "challenges.goldstatue"
    };

    public static String[] items = {
            "Photo Camera",
            "Video Camera",
            "Tripod",
            "D.O.T.S Projector",
            "EMF Reader",
            "Spirit Box",
            "Flashlight",
            "UV Light",
            "Sound Sensor",
            "Parabolic Microphone",
            "Incense",
            "Firelight",
            "Igniter",
            "Sanity Medication",
            "Motion Sensor",
            "Crucifix",
            "Salt",
            "Thermometer"
    };


    public static HashMap<Integer,MapData> maps;
    static {
        maps = new HashMap<>();
        maps.put(MapData.Id.TANGLE, new MapData("6 Tanglewood Drive","https://phasmo.karotte.org/maps/6-tanglewood-drive/"));
        maps.put(MapData.Id.WILLOW, new MapData("13 Willow Street","https://phasmo.karotte.org/maps/13-willow-street/"));
        maps.put(MapData.Id.RIDGEVIEW, new MapData("10 Ridgeview Court","https://phasmo.karotte.org/maps/10-ridgeview-court/"));
        maps.put(MapData.Id.EDGEFIELD, new MapData("42 Edgefield Road","https://phasmo.karotte.org/maps/42-edgefield-road/"));
        maps.put(MapData.Id.GRAFTON, new MapData("Grafton Farmhouse","https://phasmo.karotte.org/maps/grafton-farmhouse/"));
        maps.put(MapData.Id.BLEASDALE, new MapData("Bleasdale Farmhouse","https://phasmo.karotte.org/maps/bleasdale-farmhouse/"));
        maps.put(MapData.Id.WOODWIND, new MapData("Camp Woodwind","https://phasmo.karotte.org/maps/camp-woodwind/"));
        maps.put(MapData.Id.SUNNYMEADOWS_RESTRICTED, new MapData("Sunny Meadows Mental Institution (Restricted)","https://phasmo.karotte.org/maps/sunny-meadows-mental-institution/"));
        maps.put(MapData.Id.BROWNSTONE_HIGHSCHOOL, new MapData("Brownstone High School","https://phasmo.karotte.org/maps/brownstone-high-school/"));
        maps.put(MapData.Id.MAPLE_LODGE, new MapData("Maple Lodge Campsite","https://phasmo.karotte.org/maps/maple-lodge-campsite/"));
        maps.put(MapData.Id.PRISON, new MapData("Prison","https://phasmo.karotte.org/maps/prison/"));
        maps.put(MapData.Id.SUNNYMEADOWS, new MapData("Sunny Meadows Mental Institution","https://phasmo.karotte.org/maps/sunny-meadows-mental-institution/"));
        maps.put(MapData.Id.POINTHOPE, new MapData("Point Hope","https://phasmo.karotte.org/maps/point-hope/"));

    }

    public static class MapData{

        public final String name;
        public final String link;

        public MapData(String name, String link){
            this.name = name;
            this.link = link;
        }

        public static int[] smallIds = {Id.TANGLE,Id.WILLOW, Id.RIDGEVIEW, Id.EDGEFIELD,Id.GRAFTON,Id.BLEASDALE,Id.WOODWIND,Id.SUNNYMEADOWS_RESTRICTED,Id.POINTHOPE};
        public static int[] largeIds = {Id.BROWNSTONE_HIGHSCHOOL, Id.MAPLE_LODGE,Id.PRISON,Id.SUNNYMEADOWS};
        public static class Id{
            public static int TANGLE = 0;
            public static int WILLOW = 1;
            public static int RIDGEVIEW = 2;
            public static int EDGEFIELD = 3;
            public static int GRAFTON = 4;
            public static int BLEASDALE = 5;
            public static int WOODWIND = 6;
            public static int SUNNYMEADOWS_RESTRICTED = 7;
            public static int BROWNSTONE_HIGHSCHOOL = 8;
            public static int MAPLE_LODGE = 9;
            public static int PRISON = 10;
            public static int SUNNYMEADOWS = 11;
            public static int POINTHOPE = 12;
        }
    }

    public static String[] statusFacts = {
            // Max length = 128 !!
            "The Hantu will never turn on the fuse box.",
            "The Jinn will never directly turn off the power. It can still turn off the breaker indirectly by turning on too many lights!",
            "All ghosts can completely close doors without eventing. It's just more common for a Yurei as it accompanies its ability",
            "Banshees select their target randomly. There is no way to tell or influence it before entering the house!",
            "Mares can not turn on lights.",
            "The Mare has a higher chance to do light breaking ghost events.",
            "The Banshee will sing more often in ghost events.",
            "Only the shade can appear in shadow form in a event forced by a cursed item.",
            "The Oni can't do the mist ghost event (air ball).",
            "Crucifixes have a higher range on Demons",
            "The best source of game info is #ghost-huntin-resources on the official Discord!"
    };
}
