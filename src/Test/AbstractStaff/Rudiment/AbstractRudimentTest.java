package Test.AbstractStaff.Rudiment;

import MuseScoreWriter.AbstractStaff.AbstractStaff;
import MuseScoreWriter.AbstractStaff.Rudiment.AbstractRudimentCreator;

public class AbstractRudimentTest {
    public static void main(String[] args) {
        AbstractRudimentCreator arc = AbstractRudimentCreator.getInstance();

        String[] rudimentNames = {
                "Rest",
                "SingleStroke",
                "DoubleStroke",
                "Herta",
                "Paradiddle",
                "FiveStroke",
                "DoubleParadiddle"
        };

        for (String rudimentName : rudimentNames) {
            AbstractStaff<Integer,Boolean> rudiment = arc.create(rudimentName);
            arc.logRudiment(rudiment);
        }

        String[] rudimentLinearPatterns = {
                "abxabxab",
                "axxaxxax",
                "xxxxxxxx",
                "abxcdxef"
        };

        for (String linearPattern : rudimentLinearPatterns) {
            AbstractStaff<Integer,Boolean> rudiment = AbstractRudimentCreator.fromLinearPatternString(linearPattern, linearPattern);
            arc.logRudiment(rudiment);
        }
    }
}
