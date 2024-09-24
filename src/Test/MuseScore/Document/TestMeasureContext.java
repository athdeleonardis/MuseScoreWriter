package Test.MuseScore.Document;

import MuseScoreWriter.IndexedStaff.IndexedStaff;
import MuseScoreWriter.IndexedStaff.IndexedStaffChordReader;
import MuseScoreWriter.IndexedStaff.Rudiment.IndexedStaffRudimentCreator;
import MuseScoreWriter.IndexedStaff.Rudiment.RandomizedRudimentCreator;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Document.MeasureContext;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocument;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentAppender;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Chord;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.Util.FractionStack;
import MuseScoreWriter.Util.RandomProportionChooser;

import java.util.Arrays;

public class TestMeasureContext {
    public static void main(String[] args) {
        int numRudiments = 64;

        MuseScoreDocument msd = MuseScoreDocumentCreator.create("MeasureContextTest", "subtitle", "composer");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocument(msd);
        MeasureContext measureContext = new MeasureContext();
        measureContext.setTimeSignature(new Fraction(4,4));
        measureContext.setGroupSize(new Fraction(1,4));

        FractionStack fractionStack = new FractionStack();
        measureContext.setDocumentAppender(msda);
        measureContext.setFractionStack(fractionStack);

        IndexedStaff<Integer,?> abstractRudiment = IndexedStaffRudimentCreator.getInstance().create("Paradiddle");
        RandomizedRudimentCreator rrc = new RandomizedRudimentCreator()
                .setPossibleNotes(Limb.LeftArm, Arrays.asList("Snare"))
                .setPossibleNotes(Limb.RightArm, Arrays.asList("Snare"));

        IndexedStaffChordReader<Limb> chordReader = new IndexedStaffChordReader<>();

        RandomProportionChooser<Integer> tupletChooser = new RandomProportionChooser<Integer>()
                .setProportion(1, 3)
                .setProportion(1, 4)
                .setProportion(1, 5);

        IndexedStaff<Limb, Note> rudiment;
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

        msd.getDocumentXML().compile("music/MeasureContextTest.mscx");
    }
}
