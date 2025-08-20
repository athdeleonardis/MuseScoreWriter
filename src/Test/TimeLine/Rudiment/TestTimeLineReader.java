package Test.TimeLine.Rudiment;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.TimeLine.TimeLine;
import MuseScoreWriter.TimeLine.TimeLineReader;
import MuseScoreWriter.Util.RandomProportionChooser;

import java.util.ArrayList;
import java.util.List;

public class TestTimeLineReader {
    public static void main(String[] args) {
        int numTests = 10;
        int numInsertionsPerTest = 10;

        RandomProportionChooser<Integer> numerators = new RandomProportionChooser<>();
        numerators.setProportion(1, 1);
        numerators.setProportion(1, 2);
        numerators.setProportion(1, 3);
        numerators.setProportion(1, 4);
        numerators.setProportion(1, 5);

        RandomProportionChooser<Integer> denominators = new RandomProportionChooser<>();
        denominators.setProportion(1, 1);
        denominators.setProportion(1, 2);
        denominators.setProportion(1, 3);
        denominators.setProportion(1, 4);
        denominators.setProportion(1, 5);

        TimeLineReader<Integer> timeLineReader = new TimeLineReader<>();

        for (int i = 0; i < numTests; i++) {
            TimeLine<Integer> timeLine = new TimeLine<>("Test " + i);
            Fraction totalDuration = new Fraction(0, 1);
            List<TimeLineReader<Integer>.TimeLineData> expectedData = new ArrayList<>();
            for (int j = 0; j < numInsertionsPerTest; j++) {
                int numerator = numerators.getItem();
                int denominator = denominators.getItem();
                Fraction position = new Fraction(totalDuration);
                Fraction duration = new Fraction(numerator, denominator).simplify();
                totalDuration.add(duration).simplify();
                timeLine.insert(position, j);
                expectedData.add(timeLineReader.new TimeLineData(position, duration, j));
            }
            timeLine.setDuration(totalDuration);

            timeLineReader.setTimeLine(timeLine);
            List<TimeLineReader<Integer>.TimeLineData> dataRead = new ArrayList<>();
            if (!verify(timeLineReader, expectedData, dataRead)) {
                System.out.println("Time line doesn't match expected reading:");
                System.out.println(timeLine);
                System.out.println(expectedData);
                System.out.println(dataRead);
            }
        }
    }

    public static boolean verify(TimeLineReader<Integer> reader, List<TimeLineReader<Integer>.TimeLineData> dataList, List<TimeLineReader<Integer>.TimeLineData> dataRead) {
        while (!reader.isFinished()) {
            TimeLineReader<Integer>.TimeLineData data = reader.read(new Fraction(5,1));
            dataRead.add(data);
            TimeLineReader<Integer>.TimeLineData expectedData = dataList.get(0);
            if (!data.position.equals(expectedData.position) || !data.duration.equals(expectedData.duration) || data.entry != expectedData.entry)
                return false;
            dataList.remove(0);
        }
        if (!dataList.isEmpty())
            return false;
        return true;
    }
}
