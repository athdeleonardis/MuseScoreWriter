package Test.IndexedStaff.Rudiment;

import MuseScoreWriter.IndexedStaff.IndexedStaff;
import MuseScoreWriter.IndexedStaff.Rudiment.IndexedStaffRudimentCreator;
import MuseScoreWriter.IndexedStaff.Rudiment.RandomizedRudimentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.Util.GlobalRandom;
import MuseScoreWriter.Util.ListRandom;
import MuseScoreWriter.Util.RandomProportionChooser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestRandomizedRudimentCreator {
    public static void main(String[] args) {
        RandomizedRudimentCreator rrc = new RandomizedRudimentCreator();

        // Rudiments
        String[] rudimentNames = { "FiveStroke", "Herta", "Rest" };
        IndexedStaffRudimentCreator isrc = IndexedStaffRudimentCreator.getInstance();
        ArrayList<IndexedStaff<Integer,?>> rudiments = new ArrayList<>();
        for (String rudimentName : rudimentNames)
            rudiments.add(isrc.create(rudimentName));

        // Notes
        List<String> handNotes = Arrays.asList("Snare", "HighTom", "MidTom", "LowTom");
        RandomProportionChooser<Integer> numDrumsChooser = new RandomProportionChooser<Integer>()
                .setProportion(1, 1)
                .setProportion(1, 2)
                .setProportion(1, 3)
                .setProportion(1, 4);

        // Limbs

        int numRandomDrumChoices = 5;
        int numRandomRudimentsPerDrumChoice = 10;

        for (int i = 0; i < numRandomDrumChoices; i++) {
            int numDrumsChosen = numDrumsChooser.getItem();
            List<String> drumsChosen = ListRandom.randomList(handNotes, numDrumsChosen);
            rrc.setPossibleNotes(Limb.RightArm, drumsChosen);
            rrc.setPossibleNotes(Limb.LeftArm, drumsChosen);
            String drumsChosenStr = "Drums chosen: " + String.join(", ", drumsChosen);
            System.out.println(drumsChosenStr);
            for (int j = 0; j < numRandomRudimentsPerDrumChoice; j++) {
                IndexedStaff<Integer,?> rudiment = GlobalRandom.nextElement(rudiments);
                IndexedStaff<Limb,Note> concreteRudiment = rrc.create(rudiment, true);
                System.out.println(concreteRudiment);
                System.out.println(concreteRudiment.noteString());
            }
        }
    }
}
