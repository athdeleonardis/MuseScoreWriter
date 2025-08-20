package MuseScoreWriter.MuseScore.Document;

import MuseScoreWriter.IndexedStaff.IndexedStaffChordReader;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Note.Chord;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.TimeLine.TimeLineReader;
import MuseScoreWriter.Util.FractionStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MeasureContext {
    private FractionStack fractionStack;
    private MuseScoreDocumentAppender msda;

    private Fraction timeSignature;
    private boolean newTimeSignature;
    private boolean hasClef;
    private Fraction groupSize;

    //
    // Setters
    //

    public void setFractionStack(FractionStack fractionStack) {
        this.fractionStack = fractionStack;
    }

    public void setDocumentAppender(MuseScoreDocumentAppender msda) {
        this.msda = msda;
    }

    public void setTimeSignature(Fraction timeSignature) {
        this.timeSignature = timeSignature;
        this.newTimeSignature = true;
    }

    public void setGroupSize(Fraction groupSize) {
        this.groupSize = new Fraction(groupSize).simplify();
    }

    //
    // Public Methods
    //

    public boolean measureEnded() {
        return fractionStack.size() == 0;
    }

    public boolean groupEnded() {
        return fractionStack.size() < 2; // group is the second element of the stack
    }

    public void checkContext() {
        List<Integer> groupsEnded = fractionStack.popAllZero();
        for (int removed : groupsEnded) {
            switch (removed) {
                case 0:
                case 1:
                    break;
                default:
                    msda.endTuplet();
            }
        }
    }

    public void newMeasure() {
        msda.newMeasure();
        if (!hasClef)
            msda.addClef();
        if (newTimeSignature) {
            msda.addTimeSignature(timeSignature);
            newTimeSignature = false;
        }
        fractionStack.push(new Fraction(timeSignature).simplify());
    }

    public void newGroup() {
        fractionStack.push(Fraction.min(groupSize, fractionStack.peek()));
    }

    public void newTuplet(int numNotesInTuplet, Fraction duration) {
        msda.startTuplet(numNotesInTuplet, duration);
        fractionStack.push(duration);
    }

    public void readChord(IndexedStaffChordReader<?> chordReader, Fraction unit, boolean addLimbText) {
        Chord chord = chordReader.readChord(fractionStack.peek(), unit);
        fractionStack.subtract(chord.duration);
        msda.addNotes(chord.notes, chord.duration, addLimbText);
    }

    public <T extends Comparable<T>> void readTimeLine(TimeLineReader<Map<T, Note>> timeLineReader, Fraction unit, boolean addLimbText) {
        Fraction maxReadLength = new Fraction(fractionStack.peek()).divide(unit).simplify();
        TimeLineReader<Map<T,Note>>.TimeLineData timeLineData = timeLineReader.read(maxReadLength);
        fractionStack.subtract(timeLineData.duration);
        List<Note> notes = timeLineData.entry == null ? null : new ArrayList<>(timeLineData.entry.values());
        Fraction duration = timeLineData.duration;
        msda.addNotes(notes, duration, addLimbText);
    }
}
