package Test.MuseScore.Document;

import CustomMath.Fraction;
import MuseScore.Document.MuseScoreDocumentAppender;
import MuseScore.Document.MuseScoreDocumentCreator;
import MuseScore.Limb;
import MuseScore.Note.Note;
import MuseScore.Note.NoteCreator;

import java.util.ArrayList;

public class MuseScoreDocumentTest {
    public static void main(String[] args) {
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender("MuseScoreDocumentAppender Test", "Subtitle", "Composer");
        msda.setTimeSignature(15, 16);

        NoteCreator nc = NoteCreator.getInstance();
        Note kickNote = nc.create("Kick");
        kickNote.limb = Limb.RightLeg;
        Note snareNote = nc.create("Snare");
        snareNote.limb = Limb.LeftArm;
        Note hihatNote = nc.create("Hihat");
        hihatNote.limb = Limb.RightArm;

        ArrayList<Note> kick_hihat = new ArrayList();
        kick_hihat.add(kickNote);
        kick_hihat.add(hihatNote);
        ArrayList<Note> hihat = new ArrayList();
        hihat.add(hihatNote);
        ArrayList<Note> snare_hihat = new ArrayList();
        snare_hihat.add(snareNote);
        snare_hihat.add(hihatNote);

        Fraction noteDuration = new Fraction(1,4);

        for (int i = 0; i < 8; i++) {
            msda.addNotes(kick_hihat, noteDuration, true);
            if (i % 2 == 0)
                msda.addNotes(hihat, noteDuration, true);
            else
                msda.addNotes(kick_hihat, noteDuration, true);
            msda.addNotes(snare_hihat, noteDuration, true);
            msda.addNotes(hihat, noteDuration, true);
        }

        msda.getDocumentCreator().getDocument().compile("music/MuseScoreDocumentTest.mscx");
    }
}
