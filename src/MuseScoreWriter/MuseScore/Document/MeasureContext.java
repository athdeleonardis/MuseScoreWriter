package MuseScoreWriter.MuseScore.Document;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.Util.FractionStack;

import java.util.List;

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
}
