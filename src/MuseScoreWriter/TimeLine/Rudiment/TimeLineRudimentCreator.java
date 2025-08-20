package MuseScoreWriter.TimeLine.Rudiment;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.IndexedStaff.IndexedStaff;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;
import MuseScoreWriter.TimeLine.TimeLine;

import java.sql.Time;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TimeLineRudimentCreator {
    private static TimeLineRudimentCreator instance;

    public static TimeLineRudimentCreator getInstance() {
        if (instance == null) {
            instance = new TimeLineRudimentCreator();
        }
        return instance;
    }

    public TimeLine<Collection<Integer>> create(String name) {
        switch (name.toLowerCase()) {
            case "rest": {
                TimeLine<Collection<Integer>> timeLine = new TimeLine<>("Rest");
                timeLine.setDuration(new Fraction(1,1));
                timeLine.insert(new Fraction(0,1), List.of());
                return timeLine;
            }
            case "singlestroke": {
                TimeLine<Collection<Integer>> timeLine = new TimeLine<>("SingleStroke");
                timeLine.setDuration(new Fraction(1,1));
                timeLine.insert(new Fraction(0,1), List.of(0));
                return timeLine;
            }
            case "doublestroke": {
                TimeLine<Collection<Integer>> timeLine = new TimeLine<>("DoubleStroke");
                timeLine.setDuration(new Fraction(2,1));
                timeLine.insert(new Fraction(0, 1), List.of(0));
                timeLine.insert(new Fraction(1, 1), List.of(0));
                return timeLine;
            }
            case "herta": {
                TimeLine<Collection<Integer>> timeLine = new TimeLine<>("Herta");
                timeLine.setDuration(new Fraction(6,1));
                timeLine.insert(new Fraction(0,1), List.of(0));
                timeLine.insert(new Fraction(1,1), List.of(1));
                timeLine.insert(new Fraction(2,1), List.of(2));
                timeLine.insert(new Fraction(4,1), List.of(3));
                return timeLine;
            }
            case "paradiddle": {
                TimeLine<Collection<Integer>> timeLine = new TimeLine<>("Paradiddle");
                timeLine.setDuration(new Fraction(4,1));
                timeLine.insert(new Fraction(0,1), List.of(0));
                timeLine.insert(new Fraction(1,1), List.of(1));
                timeLine.insert(new Fraction(2,1), List.of(2));
                timeLine.insert(new Fraction(3,1), List.of(2));
                return timeLine;
            }
            case "fivestroke": {
                TimeLine<Collection<Integer>> timeLine = new TimeLine<>("FiveStroke");
                timeLine.setDuration(new Fraction(5,1));
                timeLine.insert(new Fraction(0,1), List.of(0));
                timeLine.insert(new Fraction(1,1), List.of(0));
                timeLine.insert(new Fraction(2,1), List.of(1));
                timeLine.insert(new Fraction(3,1), List.of(1));
                timeLine.insert(new Fraction(4,1), List.of(2));
                return timeLine;
            }
            case "doubleparadiddle": {
                TimeLine<Collection<Integer>> timeLine = new TimeLine<>("DoubleParadiddle");
                timeLine.setDuration(new Fraction(6,1));
                timeLine.insert(new Fraction(0,1), List.of(0));
                timeLine.insert(new Fraction(1,1), List.of(1));
                timeLine.insert(new Fraction(2,1), List.of(2));
                timeLine.insert(new Fraction(3,1), List.of(3));
                timeLine.insert(new Fraction(4,1), List.of(4));
                timeLine.insert(new Fraction(5,1), List.of(4));
                return timeLine;
            }
            case "paradiddlediddle": {
                TimeLine<Collection<Integer>> timeLine = new TimeLine<>("Paradiddlediddle");
                timeLine.setDuration(new Fraction(6,1));
                timeLine.insert(new Fraction(0,1), List.of(0));
                timeLine.insert(new Fraction(1,1), List.of(1));
                timeLine.insert(new Fraction(2,1), List.of(2));
                timeLine.insert(new Fraction(3,1), List.of(2));
                timeLine.insert(new Fraction(4,1), List.of(3));
                timeLine.insert(new Fraction(5,1), List.of(3));
                return timeLine;
            }
            default: {
                return null;
            }
        }
    }

    public static TimeLine<Collection<Integer>> fromLinearPatternString(String name, String pattern) {
        TimeLine<Collection<Integer>> timeLine = new TimeLine<>(name);
        timeLine.setDuration(new Fraction(pattern.length(), 1));

        // Read each character of the pattern, mapping each new character to a new line of the rudiment
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
            Fraction time = new Fraction(index, 1);
            timeLine.insert(time, List.of(intMappedTo));
        }

        return timeLine;
    }

    public static TimeLine<Map<Limb,Note>> ostinatoFromLinearPatternString(String name, Limb limb, String pattern, List<String> noteNames) {
        TimeLine<Map<Limb,Note>> timeLine = new TimeLine<>(name);
        timeLine.setDuration(new Fraction(pattern.length(), 1));

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
            Note note = NoteCreator.getInstance().create(noteMappedTo);
            Fraction time = new Fraction(index, 1);
            Map<Limb,Note> entry = new TreeMap<>();
            entry.put(limb, note);
            timeLine.insert(time, entry);
        }

        return timeLine;
    }
}
