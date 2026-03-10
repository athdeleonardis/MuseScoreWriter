package MuseScoreWriter.TimeLine;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.CustomMath.Integer;

import java.util.ArrayList;
import java.util.Collection;

public class TupletFinder {
    public static <T> int findTuplet(Collection<TimeLineReader<T>.TimeLineData> timeLineData, Fraction duration) {
        Collection<Fraction> durationsNormalized = new ArrayList<>();
        for (TimeLineReader<T>.TimeLineData data : timeLineData) {
            durationsNormalized.add(new Fraction(data.duration).divide(duration).simplify());
        }
        System.out.println(durationsNormalized);
        int tuplet = 1;
        for (Fraction interval : durationsNormalized) {
            tuplet = Integer.lcm(tuplet, interval.getDenominator());
        }
        return tuplet;
    }
}
