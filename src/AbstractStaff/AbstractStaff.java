package AbstractStaff;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import Util.ArrayListUtil;

public class AbstractStaff<T,S> {
    private String name;
    private HashMap<T,ArrayList<S>> noteToPlacements;
    private int length;

    public AbstractStaff(String name) {
        this.name = name;
        noteToPlacements = new HashMap<T, ArrayList<S>>();
    }

    public String getName() { return name; }

    public int Length() {
        return length;
    }

    public AbstractStaff<T,S> setNoteAtPosition(T noteName, int position, S value) {
        if (position+1>length)
            increaseToCapacity(position+1);
        if (!noteToPlacements.containsKey(noteName)) {
            ArrayList<S> notePlacements = new ArrayList<S>(length);
            noteToPlacements.put(noteName, notePlacements);
            for (int i = 0; i < length; i++)
                notePlacements.add(null);
        }
        noteToPlacements.get(noteName).set(position, value);
        return this;
    }

    public final AbstractStaff<T,S> increaseToCapacity(int capacity) {
        for (ArrayList<S> placements : noteToPlacements.values()) {
            placements.ensureCapacity(capacity);
        }
        this.length = capacity;
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
}
