package MuseScoreWriter.IndexedStaff.Rudiment;

import MuseScoreWriter.IndexedStaff.IndexedStaff;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IndexedStaffRudimentCreator {
    private static IndexedStaffRudimentCreator instance;

    private IndexedStaffRudimentCreator() { }

    public static IndexedStaffRudimentCreator getInstance() {
        if (instance == null)
            instance = new IndexedStaffRudimentCreator();
        return instance;
    }

    public IndexedStaff<Integer,?> create(String name) {
        switch (name.toLowerCase()) {
            case "rest":
                return new IndexedStaff<Integer,Boolean>("Rest").setNote(0,0,null);
            case "singlestroke":
                return new IndexedStaff<Integer,Boolean>("SingleStroke").setNote(0, 0, true);
            case "doublestroke":
                return new IndexedStaff<Integer,Boolean>("DoubleStroke")
                        .increaseToLength(2)
                        .setNote(0, 0, true)
                        .setNote(0, 1, true);
            case "herta":
                return new IndexedStaff<Integer,Boolean>("Herta")
                        .increaseToLength(6)
                        .setNote(0, 0, true)
                        .setNote(1, 1, true)
                        .setNote(2, 2, true)
                        .setNote(3, 4, true);
            case "paradiddle":
                return new IndexedStaff<Integer,Boolean>("Paradiddle")
                        .increaseToLength(4)
                        .setNote(0, 0, true)
                        .setNote(1, 1, true)
                        .setNote(2, 2, true)
                        .setNote(2, 3, true);
            case "fivestroke":
                return new IndexedStaff<Integer,Boolean>("FiveStroke")
                        .increaseToLength(5)
                        .setNote(0, 0, true)
                        .setNote(0, 1, true)
                        .setNote(1, 2, true)
                        .setNote(1, 3, true)
                        .setNote(2, 4, true);
            case "doubleparadiddle":
                return new IndexedStaff<Integer,Boolean>("DoubleParadiddle")
                        .increaseToLength(6)
                        .setNote(0, 0, true)
                        .setNote(1, 1, true)
                        .setNote(2, 2, true)
                        .setNote(2, 3, true)
                        .setNote(3, 4, true)
                        .setNote(3, 5, true);
            default:
                return null;
        }
    }

    // Unique characters are given unique numbers
    // Character x represents a rest
    public static IndexedStaff<Integer,Boolean> fromLinearPatternString(String name, String pattern) {
        IndexedStaff<Integer,Boolean> indexedStaff = new IndexedStaff<Integer,Boolean>(name).increaseToLength(pattern.length());
        Map<Character,Integer> characterMap = new TreeMap<>();
        int currentInt = 0;
        int index = -1;
        for (Character c : pattern.toCharArray()) {
            index++;
            if (c == 'x')
                continue;
            if (!characterMap.containsKey(c))
                characterMap.put(c, currentInt++);
            int intMappedTo = characterMap.get(c);
            indexedStaff.setNote(intMappedTo, index, true);
        }
        return indexedStaff;
    }

    public static IndexedStaff<Limb, Note> ostinatoFromLinearPatternString(String name, Limb limb, String pattern, List<String> noteNames) {
        IndexedStaff<Limb,Note> ostinato = new IndexedStaff<Limb,Note>(name).increaseToLength(pattern.length());
        Map<Character,String> characterMap = new TreeMap<>();
        int currentInt = 0;
        int index = -1;
        for (Character c : pattern.toCharArray()) {
            index++;
            if (c == 'x')
                continue;
            if (!characterMap.containsKey(c))
                characterMap.put(c, noteNames.get(currentInt++));
            String noteMappedTo = characterMap.get(c);
            ostinato.setNote(limb, index, NoteCreator.getInstance().create(noteMappedTo));
        }
        return ostinato;
    }

    public static <T extends Comparable<T>> String toString(IndexedStaff<T,?> rudiment) {
        StringBuilder str = new StringBuilder("Abstract Rudiment: { name: " + rudiment.getName() + ", notes: { ");
        for (TreeMap<T,?> notes : rudiment) {
            for (T limb : notes.keySet()) {
                str.append(limb);
            }
            if (notes.isEmpty())
                str.append("_");
            str.append(",");
        }
        str.append(" } }");
        return str.toString();
    }
}
