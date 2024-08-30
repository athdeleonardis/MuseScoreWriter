package MuseScoreWriter.AbstractStaff.AbstractStaffReader;

import java.util.ArrayList;

public class AbstractStaffChunk<T> {
    public ArrayList<T> notes;
    public int length;

    public String toString() {
        String text = "AbstractStaffChunk: {Length: " + length + ", Notes: {";
        if (notes != null)
            for (T note : notes) {
                text += note.toString() + ", ";
            }
        else
            text += "Rest, ";
        text += "}}";
        return text;
    }
}
