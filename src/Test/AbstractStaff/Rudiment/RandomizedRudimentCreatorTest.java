package Test.AbstractStaff.Rudiment;

import MuseScoreWriter.AbstractStaff.AbstractStaff;
import MuseScoreWriter.AbstractStaff.Rudiment.AbstractRudimentCreator;
import MuseScoreWriter.AbstractStaff.Rudiment.RandomizedRudimentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.Util.GlobalRandom;
import MuseScoreWriter.Util.ListRandom;
import MuseScoreWriter.Util.RandomProportionChooser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomizedRudimentCreatorTest {
    public static void main(String[] args) {
        RandomizedRudimentCreator rrc = new RandomizedRudimentCreator();

        // Rudiments
        String[] rudimentNames = { "FiveStroke", "Herta", "Rest" };
        AbstractRudimentCreator arc = AbstractRudimentCreator.getInstance();
        ArrayList<AbstractStaff<Integer,Boolean>> rudiments = new ArrayList<>();
        for (String rudimentName : rudimentNames)
            rudiments.add(arc.create(rudimentName));

        // Notes
        List<String> handNotes = Arrays.asList("Snare", "HighTom", "MidTom", "LowTom");
        RandomProportionChooser<Integer> numDrumsChooser = new RandomProportionChooser<Integer>()
                .setProportion(1, 1)
                .setProportion(2, 1)
                .setProportion(3,1)
                .setProportion(4,1);

        // Limbs
        List<Limb> limbPossibilities = Arrays.asList(Limb.RightArm, Limb.LeftArm);
        rrc.setPossibleLimbs(limbPossibilities);

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
                AbstractStaff<Integer,Boolean> rudiment = GlobalRandom.nextElement(rudiments);
                AbstractStaff<Limb,Note> concreteRudiment = rrc.create(rudiment, true);
                System.out.println(concreteRudiment);
                System.out.println(concreteRudiment.noteString());
            }
        }
    }
}
