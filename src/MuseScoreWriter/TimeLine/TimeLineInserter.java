package MuseScoreWriter.TimeLine;

import MuseScoreWriter.IndexedStaff.IndexedStaff;
import MuseScoreWriter.CustomMath.Fraction;

import java.util.Map;
import java.util.TreeMap;

public class TimeLineInserter {
    public static <S extends Comparable<S>,T> void insertAbstractStaff(TimeLine<Map<S,T>> timeLine, IndexedStaff<S,T> indexedStaff, Fraction startTime, Fraction unit) {
        for (int index = 0; index < indexedStaff.getLength(); index++) {
            Map<S,T> indexedStaffNotes = indexedStaff.getNotes(index);

            Fraction time = new Fraction(unit).multiply(index).add(startTime).simplify();
            Map<S,T> timeLineNotes = timeLine.getEntry(time);
            if (timeLineNotes == null) {
                timeLineNotes = new TreeMap<>();
                timeLine.insert(time, timeLineNotes);
            }
            timeLineNotes.putAll(indexedStaffNotes);
        }
    }

    public static <S extends Comparable<S>,T> void insertTimeLine(TimeLine<Map<S,T>> timeLineTo, TimeLine<Map<S,T>> timeLineFrom, Fraction startTime, Fraction unit) {
        for (Map.Entry<Fraction,Map<S,T>> timeLineFromEntry : timeLineFrom) {
            Fraction fromTime = timeLineFromEntry.getKey();
            Fraction toTime = new Fraction(unit).multiply(fromTime).add(startTime).simplify();

            Map<S,T> timeLineFromNotes = timeLineFromEntry.getValue();
            Map<S,T> timeLineToNotes = timeLineTo.getEntry(toTime);
            if (timeLineToNotes == null) {
                timeLineToNotes = new TreeMap<>();
                timeLineTo.insert(toTime, timeLineToNotes);
            }
            timeLineToNotes.putAll(timeLineFromNotes);
        }
    }
}
