package MuseScoreWriter.AbstractStaff.AbstractStaffChordReader;

import MuseScoreWriter.AbstractStaff.AbstractStaff;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Note.Chord;
import MuseScoreWriter.MuseScore.Note.Note;

import java.util.ArrayList;

public class AbstractStaffChordReader<T> {
    private AbstractStaff<T, Note> abstractStaff;
    private int readPosition;
    private int startPosition;
    private int endPosition;

    public void setAbstractStaff(AbstractStaff<T, Note> abstractStaff, int startPosition, int endPosition) {
        this.abstractStaff = abstractStaff;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.readPosition = startPosition;
    }

    // unit should evenly divide maxDuration
    public Chord readChord(Fraction maxDuration, Fraction unit) {
        Fraction groupRemaining = new Fraction(maxDuration).divide(unit);
        int maxReadLength = Integer.min(abstractStaff.getLength() - readPosition, groupRemaining.quotient());

        int len = 1;

        // Read the notes into a chord
        ArrayList<Note> notes = null;
        for (T noteName : abstractStaff.getNoteNames()) {
            if (abstractStaff.hasNoteAtPosition(noteName, readPosition)) {
                if (notes == null)
                    notes = new ArrayList<Note>();
                notes.add(abstractStaff.getNoteAtPosition(noteName, readPosition));
            }
        }

        // Read empty notes
        while (readPosition+len < endPosition &&
                len < maxReadLength &&
                !abstractStaff.hasNoteAtPosition(readPosition+len)) {
            len++;
        }

        readPosition += len;

        return new Chord(notes, new Fraction(unit).multiply(len).simplify());
    }

    public boolean isFinished() { return readPosition == endPosition; }

    public void reset() { this.readPosition = this.startPosition; }
}
