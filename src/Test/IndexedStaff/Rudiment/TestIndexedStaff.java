package Test.IndexedStaff.Rudiment;

import MuseScoreWriter.IndexedStaff.IndexedStaff;
import MuseScoreWriter.IndexedStaff.Rudiment.IndexedStaffRudimentCreator;

public class TestIndexedStaff {
    public static void main(String[] args) {
        IndexedStaffRudimentCreator isrc = IndexedStaffRudimentCreator.getInstance();

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
            IndexedStaff<Integer,?> rudiment = isrc.create(rudimentName);
            System.out.println(IndexedStaffRudimentCreator.toString(rudiment));
        }

        String[] rudimentLinearPatterns = {
                "abxabxab",
                "axxaxxax",
                "xxxxxxxx",
                "abxcdxef"
        };

        for (String linearPattern : rudimentLinearPatterns) {
            IndexedStaff<Integer,Boolean> rudiment = IndexedStaffRudimentCreator.fromLinearPatternString(linearPattern, linearPattern);
            isrc.toString(rudiment);
        }
    }
}
