package MuseScoreWriter.IndexedStaff;
import java.util.*;

import MuseScoreWriter.Util.ArrayListUtil;

/**
 * A non-null ArrayList of non-null TreeMaps
 * All tree map entries cannot have null value
 * @param <T> The datatype that names lines of the staff
 * @param <S> The datatype
 */
public class IndexedStaff<T extends Comparable<T>,S> implements Iterable<TreeMap<T,S>> {
    private final String name;
    private final ArrayList<TreeMap<T,S>> staff;

    public IndexedStaff(String name) {
        this.name = name;
        this.staff = new ArrayList<>();
    }

    //
    // Getters
    //

    public String getName() { return name; }

    public int getLength() { return staff.size(); }

    //
    // Public methods
    //

    public TreeMap<T,S> getNotes(int index) {
        return staff.get(index);
    }

    public IndexedStaff<T,S> setNote(T line, int index, S note) {
        if (index+1 > getLength())
            increaseToLength(index+1);
        TreeMap<T,S> notes = staff.get(index);

        if (note == null)
            notes.remove(line);
        else
            notes.put(line, note);

        return this;
    }

    private void addNotes(TreeMap<T,S> notes, int index, boolean includeNull) {
        if (includeNull) {
            TreeMap<T,S> newNotes = new TreeMap<>();
            newNotes.putAll(notes);
            staff.set(index, newNotes);
        }
        else
            staff.get(index).putAll(notes);
    }

    public IndexedStaff<T,S> addNotes(IndexedStaff<T,S> notes, int position, int step, boolean includeNull) {
        return addNotes(notes, position, 0, notes.getLength(), step, includeNull);
    }

    public IndexedStaff<T,S> addNotes(IndexedStaff<T,S> otherStaff, int index, int startIndex, int endIndex, int step, boolean includeNull) {
        int remaining = endIndex - startIndex;
        increaseToLength(index + step * remaining);
        // Loop through provided notes
        for (ListIterator<TreeMap<T,S>> iterator = otherStaff.staff.listIterator(startIndex); remaining > 0 && iterator.hasNext(); ) {
            TreeMap<T,S> notes = iterator.next();
            addNotes(notes, index, includeNull);
            index += step;
            remaining--;
        }
        return this;
    }

    public IndexedStaff<T,S> increaseToLength(int newLength) {
        if (getLength() < newLength) {
            staff.ensureCapacity(newLength);
            while (staff.size() < newLength)
                staff.add(new TreeMap<>());
        }
        return this;
    }

    public IndexedStaff<T,S> invert(int count) {
        ArrayListUtil.rotateArrayList(staff, count);
        return this;
    }

    public boolean hasNote(T line, int index) {
        return staff.get(index).containsKey(line);
    }

    public boolean hasNote(int index) {
        return !staff.get(index).isEmpty();
    }

    public S getNote(T line, int index) {
        return staff.get(index).get(line);
    }

    public String toString() {
        return this.name;
    }

    public String noteString() {
        StringBuilder str = new StringBuilder("{ ");
        for (Map<T,S> notes : staff) {
            str.append("{ ");
            for (S note : notes.values()) {
                str.append(note.toString());
                str.append(", ");
            }
            str.append("}, ");
        }
        str.append("}");
        return str.toString();
    }

    @Override
    public Iterator<TreeMap<T, S>> iterator() {
        return staff.listIterator();
    }

    //
    // Private Methods
    //
}
