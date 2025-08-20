package Test.TimeLine;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.TimeLine.TimeLine;
import MuseScoreWriter.TimeLine.TimeLineInserter;
import MuseScoreWriter.TimeLine.TimeLineReader;
import MuseScoreWriter.Util.GlobalRandom;

import java.util.TreeMap;

public class TestTimeLine {
    public static void main(String[] args) {
        int numTests = 10;
        for (int i = 0; i < numTests; i++) {
            TimeLine<TreeMap<Integer,Integer>> timeLine = new TimeLine<>(""+i);
            int numTimeLineEntries = GlobalRandom.nextPositiveInt(10);
            int timeLineDuration = GlobalRandom.nextPositiveInt(10);
            for (int j = 0; j < numTimeLineEntries; j++) {
                int subdivision = GlobalRandom.nextPositiveInt(10);
                Fraction time = new Fraction(GlobalRandom.nextPositiveInt(timeLineDuration * subdivision), subdivision);
                //TimeLineInserter.insertAbstractStaff(timeLine, );
            }
        }
    }
}
