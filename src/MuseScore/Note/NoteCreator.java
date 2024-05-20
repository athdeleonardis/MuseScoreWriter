package MuseScore.Note;

import XML.XMLObject;

public class NoteCreator {
    private static NoteCreator instance;

    private NoteCreator() {

    }

    public static NoteCreator getInstance() {
        if (instance == null)
            instance = new NoteCreator();
        return instance;
    }
    public Note create(String name) {
        Note note = null;
        switch (name.toLowerCase()) {
            case "hihat":
                note = new Note("Hihat", 79, 15);
                note.xmlObject
                        .addChild("head", "cross")
                        .addChild("dead", "1");
                break;
            case "snare":
                note = new Note("Snare", 72, 14);
                break;
            case "kick":
                note = new Note("Kick", 65, 13);
                break;
            case "hightom":
                note = new Note("HighTom", 76, 18);
                break;
            case "midtom":
                note = new Note("MidTom", 74, 16);
                break;
            case "lowtom":
                note = new Note("LowTom", 69, 17);
                break;
            case "hihatpedal":
            case "hihatclose":
                note = new Note("HihatClose", 65, 13);
                note.xmlObject
                        .addChild("head", "cross")
                        .addChild("dead", "1");
                break;
        }
        return note;
    }
}
