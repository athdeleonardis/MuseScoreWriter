package Test.MuseScore.Document;

import MuseScoreWriter.AbstractStaff.AbstractStaff;
import MuseScoreWriter.AbstractStaff.AbstractStaffReader.AbstractStaffChordReader;
import MuseScoreWriter.AbstractStaff.Rudiment.AbstractRudimentCreator;
import MuseScoreWriter.AbstractStaff.Rudiment.RandomizedRudimentCreator;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Document.MeasureContext;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentAppender;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Chord;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.Util.FractionStack;
import MuseScoreWriter.Util.RandomProportionChooser;

import java.util.Arrays;

public class MeasureContextTest {
    public static void main(String[] args) {
        int numRudiments = 64;

        MuseScoreDocumentCreator msdc = new MuseScoreDocumentCreator("MeasureContextTest", "subtitle", "composer");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocumentCreator(msdc);
        MeasureContext measureContext = new MeasureContext();
        measureContext.setTimeSignature(new Fraction(4,4));
        measureContext.setGroupSize(new Fraction(1,4));

        FractionStack fractionStack = new FractionStack();
        measureContext.setDocumentAppender(msda);
        measureContext.setFractionStack(fractionStack);

        AbstractStaff<Integer,Boolean> abstractRudiment = AbstractRudimentCreator.getInstance().create("Paradiddle");
        RandomizedRudimentCreator rrc = new RandomizedRudimentCreator()
                .setPossibleLimbs(Arrays.asList(Limb.LeftArm, Limb.RightArm))
                .setPossibleNotes(Limb.LeftArm, Arrays.asList("Snare"))
                .setPossibleNotes(Limb.RightArm, Arrays.asList("Snare"));

        AbstractStaffChordReader<Limb> chordReader = new AbstractStaffChordReader<>();

        RandomProportionChooser<Integer> tupletChooser = new RandomProportionChooser<Integer>()
                .setProportion(3,1)
                .setProportion(4,1)
                .setProportion(5,1);

        AbstractStaff<Limb, Note> rudiment;
        Fraction unit = new Fraction(1,16);
        while (numRudiments-- > 0) {
            rudiment = rrc.create(abstractRudiment, true);
            chordReader.setAbstractStaff(rudiment, 0, rudiment.getLength());

            while (!chordReader.isFinished()) {
                measureContext.checkContext();

                if (measureContext.measureEnded()) {
                    measureContext.newMeasure();
                }

                if (measureContext.groupEnded()) {
                    measureContext.newGroup();
                    int tuplet = tupletChooser.getItem();
                    System.out.println(tuplet);
                    if (tuplet != 4)
                        measureContext.newTuplet(tuplet, fractionStack.peek());
                    unit = new Fraction(1,4*tuplet);
                }

                //System.out.println(fractionStack);

                Chord chord = chordReader.readChord(fractionStack.peek(), unit);
                //System.out.println("Chord duration: " + chord.duration);
                fractionStack.subtract(chord.duration);
                msda.addNotes(chord.notes, chord.duration, true);
            }
        }

        measureContext.checkContext();

        msdc.getDocument().compile("music/MeasureContextTest.mscx");
    }
}
