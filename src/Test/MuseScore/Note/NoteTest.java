package Test.MuseScore.Note;

import MuseScore.Note.Note;
import MuseScore.Note.NoteCreator;
import XML.XMLObject;

public class NoteTest {
    public static void main(String[] args) {
        String[] noteNames = { "Snare", "Hihat", "Kick" };
        NoteCreator nc = NoteCreator.getInstance();
        XMLObject xmlFile = new XMLObject("Notes");
        for (String noteName : noteNames) {
            Note note = nc.create(noteName);
            System.out.println(note.name);
            xmlFile.addChild(note.xmlObject);
        }
        xmlFile.compile("music/NoteTest.mxl");
    }
}
