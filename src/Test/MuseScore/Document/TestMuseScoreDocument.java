package Test.MuseScore.Document;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocument;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentAppender;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;

import java.util.ArrayList;

public class TestMuseScoreDocument {
    public static void main(String[] args) {
        MuseScoreDocument msd = MuseScoreDocumentCreator.create("MuseScoreDocumentAppender Test", "Subtitle", "Composer");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocument(msd);

        NoteCreator nc = NoteCreator.getInstance();
        Note kickNote = nc.create("Kick");
        kickNote.limb = Limb.RightLeg;
        Note snareNote = nc.create("Snare");
        snareNote.limb = Limb.LeftArm;
        Note hihatNote = nc.create("Hihat");
        hihatNote.limb = Limb.RightArm;

        ArrayList<Note> kick_hihat = new ArrayList<>();
        kick_hihat.add(kickNote);
        kick_hihat.add(hihatNote);
        ArrayList<Note> hihat = new ArrayList<>();
        hihat.add(hihatNote);
        ArrayList<Note> snare_hihat = new ArrayList<>();
        snare_hihat.add(snareNote);
        snare_hihat.add(hihatNote);

        boolean isFirstMeasure = true;
        Fraction noteDuration = new Fraction(1,4);
        for (int i = 0; i < 8; i++) {
            if (i % 4 == 0)
                msda.newMeasure();
            if (isFirstMeasure) {
                msda.addTimeSignature(new Fraction(4, 4));
                isFirstMeasure = false;
            }
            switch (i) {
                case 0,4,5 -> msda.addNotes(kick_hihat, noteDuration, true);
                case 2,6 -> msda.addNotes(snare_hihat, noteDuration, true);
                default -> msda.addNotes(hihat, noteDuration, true);
            }
        }

        msd.getDocumentXML().compile("music/MuseScoreDocumentTest.mscx");
    }
}
