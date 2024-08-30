package MuseScoreWriter.AbstractStaff;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import MuseScoreWriter.Util.ArrayListUtil;

public class AbstractStaff<T,S> {
    private String name;
    private HashMap<T,ArrayList<S>> noteToPlacements;
    private int length;

    public AbstractStaff(String name) {
        this.name = name;
        noteToPlacements = new HashMap<T, ArrayList<S>>();
    }

    public String getName() { return name; }

    public int getLength() {
        return length;
    }

    public AbstractStaff<T,S> setNoteAtPosition(T noteName, int position, S value) {
        if (position+1>length)
            increaseToLength(position+1);
        if (!noteToPlacements.containsKey(noteName)) {
            ArrayList<S> notePlacements = new ArrayList<S>(length);
            noteToPlacements.put(noteName, notePlacements);
            for (int i = 0; i < length; i++)
                notePlacements.add(null);
        }
        noteToPlacements.get(noteName).set(position, value);
        return this;
    }

    public AbstractStaff<T,S> addNotes(AbstractStaff<T,S> notes, int position, int step, boolean includeNull) {
        return addNotes(notes, position, 0, notes.getLength(), step, includeNull);
    }

    public AbstractStaff<T,S> addNotes(AbstractStaff<T,S> notes, int position, int startIndex, int endIndex, int step, boolean includeNull) {
        increaseToLength(position + step * (endIndex - startIndex));
        // Loop through provided notes
        for (int i = startIndex; i < endIndex; i++) {
            for (T staffLine : notes.getNoteNames()) {
                S note = notes.getNoteAtPosition(staffLine, i);
                if (includeNull || note != null)
                    setNoteAtPosition(staffLine, position, note);
            }
            position += step;
        }
        return this;
    }

    public final AbstractStaff<T,S> increaseToLength(int newLength) {
        if (length < newLength) {
            for (ArrayList<S> placements : noteToPlacements.values()) {
                placements.ensureCapacity(newLength);
                while (placements.size() < newLength)
                    placements.add(null);
            }
            this.length = newLength;
        }
        return this;
    }

    public AbstractStaff<T,S> invert(int count) {
        for (ArrayList<S> placements: noteToPlacements.values()) {
            ArrayListUtil.rotateArrayList(placements, count);
        }
        return this;
    }

    public Collection<T> getNoteNames() {
        return noteToPlacements.keySet();
    }

    public boolean hasNoteAtPosition(T noteName, int position) {
        return noteToPlacements.get(noteName).get(position) != null;
    }

    public boolean hasNoteAtPosition(int position) {
        for (T noteName : getNoteNames()) {
            if (hasNoteAtPosition(noteName, position))
                return true;
        }
        return false;
    }

    public S getNoteAtPosition(T noteName, int position) {
        return noteToPlacements.get(noteName).get(position);
    }

    public String toString() {
        return this.name;
    }

    public String noteString() {
        StringBuilder str = new StringBuilder("{ ");
        for (int i = 0; i < length; i++) {
            str.append("{ ");
            for (T noteName : getNoteNames()) {
                S note = getNoteAtPosition(noteName, i);
                if (note != null) {
                    str.append(note.toString());
                    str.append(", ");
                }
            }
            str.append("}, ");
        }
        str.append("}");
        return str.toString();
    }
}
