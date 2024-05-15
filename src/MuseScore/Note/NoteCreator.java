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
        Note note;
        switch (name.toLowerCase()) {
            case "hihat":
                note = new Note();
                note.name = "Hihat";
                note.xmlObject = new XMLObject("Note")
                        .addChild("pitch", "79")
                        .addChild("tpc", "15")
                        .addChild("head", "cross")
                        .addChild("dead", "1");
                note.pitch = 75;
                return note;
            case "snare":
                note = new Note();
                note.name = "Snare";
                note.xmlObject = new XMLObject("Note")
                        .addChild("pitch", "72")
                        .addChild("tpc", "14");
                note.pitch = 72;
                return note;
            case "kick":
                note = new Note();
                note.name = "Kick";
                note.xmlObject = new XMLObject("Note")
                        .addChild("pitch", "65")
                        .addChild("tpc", "13");
                note.pitch = 65;
                return note;
        }
        return null;
    }
}
