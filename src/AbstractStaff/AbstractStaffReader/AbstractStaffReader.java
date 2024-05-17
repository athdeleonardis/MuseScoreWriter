package AbstractStaff.AbstractStaffReader;

import AbstractStaff.AbstractStaff;

import java.util.ArrayList;

// Used to read an abstract staff's notes/rests up to a maximum length.
public class AbstractStaffReader<S,T> {
    private AbstractStaff<S,T> abstractStaff;
    private int startPosition;
    private int position;
    private int endPosition;

    public AbstractStaffReader<S,T> setAbstractStaff(AbstractStaff<S,T> abstractStaff, int startPosition, int endPosition) {
        this.abstractStaff = abstractStaff;
        this.startPosition = startPosition;
        this.position = startPosition;
        this.endPosition = endPosition;
        return this;
    }

    //
    // Public Methods
    //

    // Reads an AbstractStaffChunk starting from 'this.position' and ending at either the next
    // note or at maxLength.
    public AbstractStaffChunk<T> readNoteValues(int maxLength) {
        AbstractStaffChunk<T> chunk = new AbstractStaffChunk<T>();
        int len = 1;

        // Read the notes into a chord
        ArrayList<T> notes = null;
        for (S noteName : abstractStaff.getNoteNames()) {
            if (abstractStaff.hasNoteAtPosition(noteName, position)) {
                if (notes == null)
                    notes = new ArrayList<T>();
                notes.add(abstractStaff.getNoteAtPosition(noteName, position));
            }
        }
        chunk.notes = notes;

        // Read empty notes
        while (position+len < endPosition && len < maxLength && !abstractStaff.hasNoteAtPosition(position+len)) {
            len++;
        }

        position += len;
        chunk.length = len;

        return chunk;
    }

    public AbstractStaffReader reset() {
        this.position = startPosition;
        return this;
    }

    public boolean isFinished() {
        return position >= endPosition;
    }
}
