package MuseScoreWriter.IndexedStaff;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Note.Chord;
import MuseScoreWriter.MuseScore.Note.Note;

import java.util.*;

public class IndexedStaffChordReader<T extends Comparable<T>> {
    private IndexedStaff<T, Note> indexedStaff;
    private int readPosition;
    private int startPosition;
    private int endPosition;

    public void setAbstractStaff(IndexedStaff<T, Note> indexedStaff, int startPosition, int endPosition) {
        this.indexedStaff = indexedStaff;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.readPosition = startPosition;
    }

    // unit should evenly divide maxDuration
    public Chord readChord(Fraction maxDuration, Fraction unit) {
        Fraction groupRemaining = new Fraction(maxDuration).divide(unit);
        int maxReadLength = Integer.min(indexedStaff.getLength() - readPosition, groupRemaining.quotient());
        int len = 1;

        List<Note> notes = null;
        TreeMap<T,Note> notesMap = indexedStaff.getNotes(readPosition);
        if (!notesMap.isEmpty()) {
            notes = new ArrayList<>();
            notes.addAll(notesMap.values());
        }

        // Read empty notes
        while (readPosition+len < endPosition &&
                len < maxReadLength &&
                !indexedStaff.hasNote(readPosition+len)) {
            len++;
        }
        readPosition += len;

        return new Chord(notes, new Fraction(unit).multiply(len).simplify());
    }

    public boolean isFinished() { return readPosition == endPosition; }

    public void reset() { this.readPosition = this.startPosition; }
}
