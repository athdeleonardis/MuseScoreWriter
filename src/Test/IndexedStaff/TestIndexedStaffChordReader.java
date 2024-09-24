package Test.IndexedStaff;

import MuseScoreWriter.IndexedStaff.IndexedStaff;
import MuseScoreWriter.IndexedStaff.IndexedStaffChordReader;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Chord;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;

public class TestIndexedStaffChordReader {
    public static void main(String[] args) {
        IndexedStaffChordReader<Integer> chordReader = new IndexedStaffChordReader<>();

        NoteCreator nc = NoteCreator.getInstance();
        Note kick = nc.create("Kick");
        kick.limb = Limb.RightLeg;
        Note snare = nc.create("Snare");
        snare.limb = Limb.LeftArm;
        Note hihat = nc.create("Hihat");
        hihat.limb = Limb.RightArm;
        Note hihatPedal = nc.create("HihatPedal");
        hihatPedal.limb = Limb.LeftLeg;

        IndexedStaff<Integer, Note> indexedStaff = new IndexedStaff<Integer,Note>("BreakBeat")
                .increaseToLength(16)
                .setNote(0, 0, kick)
                .setNote(2, 0, hihat)
                .setNote(0, 2, kick)
                .setNote(2, 2, hihat)
                .setNote(1, 4, snare)
                .setNote(2, 4, hihat)
                .setNote(2, 6, hihat)
                .setNote(1, 7, snare)
                .setNote(2, 8, hihat)
                .setNote(1, 9, snare)
                .setNote(0, 10, kick)
                .setNote(2, 10, hihat)
                .setNote(2, 12, hihat)
                .setNote(1, 12, snare)
                .setNote(2, 14, hihat);

        Fraction maxDuration = new Fraction(1,4);
        Fraction unitSize = new Fraction(1,16);

        int numRepeats = 4;

        int index = numRepeats;
        chordReader.setAbstractStaff(indexedStaff, 0, indexedStaff.getLength());
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
