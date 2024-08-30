package AbstractStaff.AbstractStaffReader;

import AbstractStaff.AbstractStaff;
import CustomMath.Fraction;
import java.lang.Math;

// Used to read an abstract staff up to some maximum Fraction group size.
// Keeps track of the remaining measure as a Fraction, as well as the remaining group size.
// Uses a Fraction unitSize to give context to the size of each position in the abstract staff being read.
public class NoteGroupReader<T,S> {
    private AbstractStaff<T,S> rudiment;
    int rudimentRemaining;
    private AbstractStaffReader<T,S> rudimentReader;
    private Fraction maxGroupSize;
    private Fraction groupRemaining;
    private Fraction timeSignature;
    private Fraction measureRemaining;

    public NoteGroupReader() {
        rudimentRemaining = 0;
        groupRemaining = new Fraction(0, 1);
        measureRemaining = new Fraction(0, 1);
        rudimentReader = new AbstractStaffReader();
    }

    public NoteGroupReader setRudiment(AbstractStaff<T,S> rudiment, int startPosition, int endPosition) {
        this.rudimentReader.setAbstractStaff(rudiment, startPosition, endPosition);
        this.rudimentRemaining = endPosition - startPosition;
        return this;
    }

    public NoteGroupReader setTimeSignature(Fraction timeSignature) {
        this.timeSignature = new Fraction(timeSignature);
        return this;
    }

    public NoteGroupReader setGroupSize(Fraction groupSize) {
        this.maxGroupSize = new Fraction(groupSize);
        return this;
    }

    // Reads a chunk up to the remaining group size or remaining rudiment size
    // Each position in the rudiment is treated as if it were unitSize
    // There might be issues if the unit size is greter than the amount of rudiment or group remaining, untested
    public AbstractStaffChunk<S> readChunk(Fraction unitSize) {
        if (measureRemaining.isZero())
            measureRemaining = new Fraction(timeSignature).simplify();

        if (groupRemaining.isZero())
            groupRemaining = new Fraction(maxGroupSize.min(measureRemaining));

        int maxReadSize = Math.min(rudimentRemaining, (int)(new Fraction(groupRemaining).divide(unitSize).getValue()));
        AbstractStaffChunk<S> chunk = rudimentReader.readNoteValues(maxReadSize);
        Fraction readSize = new Fraction(unitSize).multiply(chunk.length).simplify();

        rudimentRemaining -= chunk.length;
        measureRemaining.subtract(readSize).simplify();
        groupRemaining.subtract(readSize).simplify();

        return chunk;
    }

    public boolean rudimentIsFinished() {
        return rudimentRemaining == 0;
    }
    public boolean measureIsFinished() { return measureRemaining.isZero(); }
    public boolean groupIsFinished() { return groupRemaining.isZero(); }
}
