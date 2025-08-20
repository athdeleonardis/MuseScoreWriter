package Test.TimeLine.Rudiment;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.TimeLine.Rudiment.TimeLineRudimentCreator;
import MuseScoreWriter.TimeLine.TimeLine;
import MuseScoreWriter.TimeLine.TimeLineInserter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestTimeLineRudimentCreator {
    public static void main(String[] args) {
        TimeLineRudimentCreator tlrc = TimeLineRudimentCreator.getInstance();

        String[] rudimentNames = {
                "Rest",
                "SingleStroke",
                "DoubleStroke",
                "Herta",
                "Paradiddle",
                "FiveStroke",
                "DoubleParadiddle",
                "Paradiddlediddle"
        };

        System.out.println("Rudiments:");
        for (String rudimentName : rudimentNames) {
            TimeLine<Collection<Integer>> rudiment = tlrc.create(rudimentName);
            System.out.println(rudiment.toString());
        }

        String[] rudimentLinearPatterns = {
                "abxabxab",
                "axxaxxax",
                "xxxxxxxx",
                "abxcdxef"
        };

        System.out.println("Linear Patterns:");
        for (String linearPattern : rudimentLinearPatterns) {
            TimeLine<Collection<Integer>> rudiment = TimeLineRudimentCreator.fromLinearPatternString(linearPattern, linearPattern);
            System.out.println(rudiment);
        }

        Limb[] ostinatoLimbs = {
                Limb.LeftArm,
                Limb.LeftLeg,
                Limb.RightArm,
                Limb.RightLeg
        };


        String[] ostinatoLinearPatterns = {
                "abxabxab",
                "axxaaxxa",
                "axaxaaxa",
                "axxaaxax",
        };

        List<List<String>> ostinatoNotes = List.of(
                List.of("Snare", "HighTom"),
                List.of("HihatPedal"),
                List.of("Ride"),
                List.of("Kick")
        );

        System.out.println("Ostinato Patterns:");
        TimeLine<Map<Limb, Note>> ostinato = new TimeLine<>("Ostinato");
        ostinato.setDuration(new Fraction(1,1));
        for (int i = 0; i < 4; i++) {
            TimeLine<Map<Limb,Note>> ostinatoLine = TimeLineRudimentCreator.ostinatoFromLinearPatternString(
                    ostinatoLinearPatterns[i],
                    ostinatoLimbs[i],
                    ostinatoLinearPatterns[i],
                    ostinatoNotes.get(i)
            );
            System.out.println(ostinatoLine);
            TimeLineInserter.insertTimeLine(ostinato, ostinatoLine, Fraction.zero(), new Fraction(1,8));
        }
        System.out.println(ostinato);
    }
}
