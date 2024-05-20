package Test.AbstractStaff.Rudiment;

import AbstractStaff.Rudiment.AbstractRudimentCreator;

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
            arc.logRudiment(arc.create(rudimentName));
        }
    }
}
