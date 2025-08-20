package Test.TimeLine.Rudiment;

import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.TimeLine.Rudiment.TimeLineRandomizedRudimentCreator;
import MuseScoreWriter.TimeLine.Rudiment.TimeLineRudimentCreator;
import MuseScoreWriter.TimeLine.TimeLine;
import MuseScoreWriter.Util.GlobalRandom;
import MuseScoreWriter.Util.ListRandom;
import MuseScoreWriter.Util.RandomProportionChooser;

import java.util.*;

public class TestTimeLineRandomizedRudimentCreator {
    public static void main(String[] args) {
        TimeLineRandomizedRudimentCreator tlrrc = new TimeLineRandomizedRudimentCreator();

        String[] rudimentNames = { "FiveStroke", "Herta", "Rest" };
        TimeLineRudimentCreator tlrc = TimeLineRudimentCreator.getInstance();
        List<TimeLine<Collection<Integer>>> rudiments = new ArrayList<>();
        for (String rudimentName : rudimentNames) {
            rudiments.add(tlrc.create(rudimentName));
        }
        for (TimeLine<Collection<Integer>> tl : rudiments) {
            System.out.println(tl);
        }

        List<String> handNotes = Arrays.asList("Snare", "HighTom", "MidTom", "LowTom");
        RandomProportionChooser<Integer> numDrumsChooser = new RandomProportionChooser<>();
        numDrumsChooser.setProportion(1, 1);
        numDrumsChooser.setProportion(1, 2);
        numDrumsChooser.setProportion(1, 3);
        numDrumsChooser.setProportion(1, 4);

        int numRandomDrumChoices = 5;
        int numRandomRudimentsPerDrumChoice = 10;

        for (int i = 0; i < numRandomDrumChoices; i++) {
            int numDrumsChosen = numDrumsChooser.getItem();
            List<String> drumsChosen = ListRandom.randomList(handNotes, numDrumsChosen);
            tlrrc.setPossibleNotes(Limb.RightArm, drumsChosen);
            tlrrc.setPossibleNotes(Limb.LeftArm, drumsChosen);
            String drumsChosenStr = String.join(", ", drumsChosen);
            System.out.println(drumsChosenStr);
            for (int j = 0; j < numRandomRudimentsPerDrumChoice; j++) {
                TimeLine<Collection<Integer>> rudiment = GlobalRandom.nextElement(rudiments);
                TimeLine<Map<Limb, Note>> randomizedRudiment = tlrrc.create(rudiment, true);
                System.out.println(rudiment);
                System.out.println(randomizedRudiment);
            }
        }
    }
}
