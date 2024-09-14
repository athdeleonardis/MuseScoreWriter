package Test.MuseScore.Document;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocument;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentAppender;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;

import java.util.ArrayList;

public class MuseScoreDocumentTest {
    public static void main(String[] args) {
        MuseScoreDocument msd = MuseScoreDocumentCreator.create("MuseScoreDocumentAppender Test", "Subtitle", "Composer");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocument(msd);
        msda.addTimeSignature(new Fraction(15,16));

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

        msd.getDocumentXML().compile("music/MuseScoreDocumentTest.mscx");
    }
}
