package MuseScoreWriter.TimeLine.Rudiment;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.TimeLine.TimeLine;
import MuseScoreWriter.TimeLine.TimeLineInserter;
import MuseScoreWriter.Util.RandomProportionChooser;

import java.util.List;
import java.util.Map;

public class TimeLineSimilarPatternCreator {
    private Note note;
    private Limb limb;
    private final RandomProportionChooser<Fraction> notePositionChooser;
    private final int numHits;
    private final Fraction patternDuration;
    private int numPatternsCreated;

    public TimeLineSimilarPatternCreator(String pattern, Note note, Limb limb, Fraction unit) {
        this.note = note;
        this.limb = limb;
        this.numPatternsCreated = 0;

        int patternLength = pattern.length();
        this.numHits = countHits(pattern);
        this.patternDuration = new Fraction(unit).multiply(patternLength).simplify();

        this.notePositionChooser = createNotePositionChooser(pattern, unit, this.numHits, patternLength);
    }

    public TimeLine<Map<Limb, Note>> createPattern() {
        List<Fraction> notePositions = notePositionChooser.getItems(numHits);
        TimeLine<Map<Limb, Note>> pattern = new TimeLine<>("HandPatternCreator-Pattern-" + numPatternsCreated);
        numPatternsCreated++;

        pattern.setDuration(this.patternDuration);
        for (Fraction notePosition : notePositions) {
            TimeLineInserter.insert(pattern, notePosition, this.limb, this.note);
        }

        return pattern;
    }

    private static int countHits(String pattern) {
        int numHits = 0;
        for (char c : pattern.toCharArray()) {
            if (Character.isUpperCase(c)) {
                numHits++;
            }
        }
        return numHits;
    }

    private static RandomProportionChooser<Fraction> createNotePositionChooser(String pattern, Fraction unit, int numHits, int patternLength) {
        RandomProportionChooser<Fraction> notePositionChooser = new RandomProportionChooser<>();

        if (numHits == 0) {
            return notePositionChooser;
        }

        float hitProportion = 1;
        float nonHitProportion = 0.05f;

        for (int index = 0; index < patternLength; index++) {
            char c = pattern.charAt(index);
            boolean isHit = Character.isUpperCase(c);
            Fraction notePosition = new Fraction(unit).multiply(index);

            if (isHit) {
                notePositionChooser.setProportion(hitProportion, notePosition);
                continue;
            }

            notePositionChooser.setProportion(nonHitProportion, notePosition);
        }

        return notePositionChooser;
    }

    // Bisection method
    private float calculateProportion(int numChosen, float probability) {
        float proportion = 0;
        for (int i = 0; i < 20; i++) {
            // calculate (1 + x/1)(1 + x/2)...(1 + x/a)
            // calculate 1/(x+1)+1/(x+2)+...+1/(x+a)
            float product = 1;
            float sum = 0;
            for (int j = 1; j < numChosen + 1; j++) {
                product *= (1 + proportion / j);
                sum += 1 / (proportion + j);
            }
            // calculate new proportion
            proportion = proportion + (1 - product * probability) / sum;
        }
        return proportion;
    }
}
