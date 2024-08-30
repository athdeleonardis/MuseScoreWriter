package Test.AbstractStaff.AbstractStaffReader;

import MuseScoreWriter.AbstractStaff.AbstractStaff;
import MuseScoreWriter.AbstractStaff.AbstractStaffReader.AbstractStaffChunk;
import MuseScoreWriter.AbstractStaff.AbstractStaffReader.NoteGroupReader;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentAppender;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;

public class AbstractStaffReaderTest {
    public static void main(String[] args) {
        MuseScoreDocumentCreator msdc = new MuseScoreDocumentCreator("AbstractStaffReaderTest", "Subtitle", "Composer");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocumentCreator(msdc);
        NoteGroupReader ngr = new NoteGroupReader();

        NoteCreator nc = NoteCreator.getInstance();
        Note kick = nc.create("Kick");
        kick.limb = Limb.RightLeg;
        Note snare = nc.create("Snare");
        snare.limb = Limb.LeftArm;
        Note hihat = nc.create("Hihat");
        hihat.limb = Limb.RightArm;
        Note hihatPedal = nc.create("HihatPedal");
        hihatPedal.limb = Limb.LeftLeg;

        AbstractStaff<Integer, Note> abstractStaff = new AbstractStaff("BreakBeat")
                .increaseToLength(16)
                .setNoteAtPosition(0, 0, kick)
                .setNoteAtPosition(2, 0, hihat)
                .setNoteAtPosition(0, 2, kick)
                .setNoteAtPosition(2, 2, hihat)
                .setNoteAtPosition(1, 4, snare)
                .setNoteAtPosition(2, 4, hihat)
                .setNoteAtPosition(2, 6, hihat)
                .setNoteAtPosition(1, 7, snare)
                .setNoteAtPosition(2, 8, hihat)
                .setNoteAtPosition(1, 9, snare)
                .setNoteAtPosition(0, 10, kick)
                .setNoteAtPosition(2, 10, hihat)
                .setNoteAtPosition(2, 12, hihat)
                .setNoteAtPosition(1, 12, snare)
                .setNoteAtPosition(2, 14, hihat);

        AbstractStaff<Integer, Note> footRhythm = new AbstractStaff("FootRhythm")
                .increaseToLength(8)
                .setNoteAtPosition(0, 0, kick)
                .setNoteAtPosition(3, 2, hihatPedal)
                .setNoteAtPosition(0, 3, kick)
                .setNoteAtPosition(0, 6, kick)
                .setNoteAtPosition(3, 6, hihatPedal);

        Fraction timeSignature = new Fraction(15,16);
        Fraction maxGroupSize = new Fraction(1,4);
        Fraction unitSize = new Fraction(1,16);

        ngr.setGroupSize(maxGroupSize);
        ngr.setTimeSignature(timeSignature);

        int numRepeats = 4;

        int index = numRepeats;
        while (index > 0) {
            ngr.setRudiment(abstractStaff, 0, abstractStaff.getLength());
            while (!ngr.rudimentIsFinished()) {
                if (ngr.measureIsFinished()) {
                    msda.newMeasure();


                }
                AbstractStaffChunk<Note> chunk = ngr.readChunk(unitSize);
                Fraction notesDuration = new Fraction(unitSize).multiply(chunk.length);
                msda.addNotes(chunk.notes, notesDuration, true);
                System.out.println(chunk.toString());
            }
            index--;
        }

        for (int i = 0; i < 2; i++) {
            abstractStaff.addNotes(footRhythm, i*8,1,true);
        }

        index = numRepeats;
        while (index > 0) {
            ngr.setRudiment(abstractStaff, 0, abstractStaff.getLength());
            while (!ngr.rudimentIsFinished()) {
                AbstractStaffChunk<Note> chunk = ngr.readChunk(unitSize);
                Fraction notesDuration = new Fraction(unitSize).multiply(chunk.length);
                msda.addNotes(chunk.notes, notesDuration, true);
                System.out.println(chunk.toString());
            }
            index--;
        }

        msda.getDocumentCreator().getDocument().compile("music/AbstractStaffReaderTest.mscx");
    }
}
