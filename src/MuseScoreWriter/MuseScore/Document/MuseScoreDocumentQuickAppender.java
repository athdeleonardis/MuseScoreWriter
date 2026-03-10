package MuseScoreWriter.MuseScore.Document;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.TimeLine.TimeLine;
import MuseScoreWriter.TimeLine.TimeLineReader;
import MuseScoreWriter.Util.FractionStack;

import java.util.Map;

public class MuseScoreDocumentQuickAppender {
    public static void AppendAll(MuseScoreDocumentAppender msda, TimeLine<Map<Limb, Note>> staff, Fraction timeSignature, Fraction groupSize, boolean addLimbText) {
        FractionStack fractionStack = new FractionStack();

        TimeLineReader<Map<Limb, Note>> timeLineReader = new TimeLineReader<>();
        timeLineReader.setTimeLine(staff);

        MeasureContext measureContext = new MeasureContext();
        measureContext.setDocumentAppender(msda);
        measureContext.setFractionStack(fractionStack);
        measureContext.setTimeSignature(timeSignature);

        while (!timeLineReader.isFinished()) {
            measureContext.checkContext();

            if (measureContext.measureEnded()) {
                measureContext.newMeasure();
            }
            if (measureContext.groupEnded()) {
                Fraction currentGroupSize = Fraction.min(groupSize, fractionStack.peek());
                measureContext.setGroupSize(currentGroupSize);
                measureContext.newMeasure();
            }

            measureContext.readTimeLine(timeLineReader, new Fraction(1, 1), addLimbText);
        }

        // Fill remaining bar with rests
    }
}
