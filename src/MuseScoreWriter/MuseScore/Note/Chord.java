package MuseScoreWriter.MuseScore.Note;

import MuseScoreWriter.CustomMath.Fraction;

import java.util.List;

public class Chord {
    public List<Note> notes;
    public Fraction duration;

    public Chord(List<Note> notes, Fraction duration) {
        this.notes = notes;
        this.duration = duration;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("Chord: { ");
        if (notes == null)
            str.append("Rest, ");
        else {
            str.append("{ ");
            for (Note note : notes) {
                str.append(note);
                str.append(", ");
            }
            str.append("}, ");
        }
        str.append(duration);
        str.append(" }");
        return str.toString();
    }
}
