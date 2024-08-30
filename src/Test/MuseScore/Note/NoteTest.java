package Test.MuseScore.Note;

import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;
import MuseScoreWriter.XML.XMLObject;

public class NoteTest {
    public static void main(String[] args) {
        String[] noteNames = { "Snare", "Hihat", "Kick", "HighTom", "MidTom", "LowTom", "HihatClose" };
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
