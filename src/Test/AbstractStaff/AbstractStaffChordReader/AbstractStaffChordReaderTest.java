package Test.AbstractStaff.AbstractStaffChordReader;

import MuseScoreWriter.AbstractStaff.AbstractStaff;
import MuseScoreWriter.AbstractStaff.AbstractStaffChordReader.AbstractStaffChordReader;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Chord;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;

public class AbstractStaffChordReaderTest {
    public static void main(String[] args) {
        AbstractStaffChordReader<Integer> chordReader = new AbstractStaffChordReader<>();

        NoteCreator nc = NoteCreator.getInstance();
        Note kick = nc.create("Kick");
        kick.limb = Limb.RightLeg;
        Note snare = nc.create("Snare");
        snare.limb = Limb.LeftArm;
        Note hihat = nc.create("Hihat");
        hihat.limb = Limb.RightArm;
        Note hihatPedal = nc.create("HihatPedal");
        hihatPedal.limb = Limb.LeftLeg;

        AbstractStaff<Integer, Note> abstractStaff = new AbstractStaff<Integer,Note>("BreakBeat")
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

        Fraction maxDuration = new Fraction(1,4);
        Fraction unitSize = new Fraction(1,16);

        int numRepeats = 4;

        int index = numRepeats;
        chordReader.setAbstractStaff(abstractStaff, 0, abstractStaff.getLength());
        while (index > 0) {
            while (!chordReader.isFinished()) {
                Chord chord = chordReader.readChord(maxDuration, unitSize);
                System.out.println(chord);
            }
            chordReader.reset();
            index--;
        }
    }
}
