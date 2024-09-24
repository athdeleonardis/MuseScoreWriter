package Example;

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
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.Util.FractionStack;
import MuseScoreWriter.Util.RandomProportionChooser;
import MuseScoreWriter.Util.StringRandom;

import java.util.Arrays;

public class MinuanoWorksheet {
    public static void main(String[] args) {
        //
        // Setup probabilities and patterns
        //

        // Limb notes
        RandomizedRudimentCreator rightHand = new RandomizedRudimentCreator()
                .setPossibleNotes(Limb.RightArm, Arrays.asList("Ride"));
        RandomizedRudimentCreator leftHand = new RandomizedRudimentCreator()
                .setPossibleNotes(Limb.LeftArm, Arrays.asList("CrossStick"));
        RandomizedRudimentCreator rightLeg = new RandomizedRudimentCreator()
                .setPossibleNotes(Limb.RightLeg, Arrays.asList("Kick"));
        RandomizedRudimentCreator leftLeg = new RandomizedRudimentCreator()
                .setPossibleNotes(Limb.LeftLeg, Arrays.asList("HihatPedal"));

        // Limb patterns
        RandomProportionChooser<IndexedStaff<Limb, Note>> rightHandPatternChooser = new RandomProportionChooser<>();
        rightHandPatternChooser
                .setProportion(6, rightHand.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "OxOOOxOxOOOx"), false))
                .setProportion(2, rightHand.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "OxOOOxOOOxOO"), false))
                .setProportion(2, rightHand.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "OxOOOxOOOxOO"), false))
                .setProportion(2, rightHand.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "OOOxOOOxOOOx"), false))
                .setProportion(2, rightHand.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "OxOOOxOOOxOO"), false))
                .setProportion(1,null);


        RandomProportionChooser<IndexedStaff<Limb,Note>> leftHandPatternChooser = new RandomProportionChooser<>();
        leftHandPatternChooser
                .setProportion(6, leftHand.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "OxxOxxOxxOxx"), false))
                .setProportion(2, leftHand.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "OxxOxxOxOxOx"), false))
                .setProportion(2, leftHand.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "xOxxOxxOxxOx"), false))
                .setProportion(2, leftHand.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "OxxOxxOxOxOx"), false))
                .setProportion(1, null);

        RandomProportionChooser<IndexedStaff<Limb,Note>> leftLegPatternChooser = new RandomProportionChooser<>();
        leftLegPatternChooser
                .setProportion(12, leftLeg.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "OxxxOxxxOxxx"), false))
                .setProportion(4,  leftLeg.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "xxOxxxOxxxOx"), false))
                .setProportion(4,  leftLeg.create(IndexedStaffRudimentCreator.fromLinearPatternString("", "xxOxxxxxOxxx"), false))
                .setProportion(1,  null);

        // Limb randomness
        RandomProportionChooser<Character> rightHandRandom = new RandomProportionChooser<>();
        rightHandRandom.setProportion(2, 'O').setProportion(1,'x');
        RandomProportionChooser<Character> leftHandRandom = new RandomProportionChooser<>();
        leftHandRandom.setProportion(1, 'O').setProportion(2,'x');
        RandomProportionChooser<Character> rightLegRandom = new RandomProportionChooser<>();
        rightLegRandom.setProportion(1, 'O').setProportion(3,'x');
        RandomProportionChooser<Character> leftLegRandom = new RandomProportionChooser<>();
        leftLegRandom.setProportion(1, 'O').setProportion(4,'x');

        //
        // Create whole staff
        //

        int numBars = 64;
        IndexedStaff<Limb,Note> staff = new IndexedStaff<Limb,Note>("Staff").increaseToLength(12*numBars);
        for (int i = 0; i < numBars; i++) {
            addLine(i*12, staff, rightHand, rightHandPatternChooser, rightHandRandom);
            addLine(i*12, staff, leftHand, leftHandPatternChooser, leftHandRandom);
            addLine(i*12, staff, rightLeg, null, rightLegRandom);
            addLine(i*12, staff, leftLeg, leftLegPatternChooser, leftLegRandom);
        }

        //
        // Write staff to file
        //

        MuseScoreDocument msd = MuseScoreDocumentCreator.create("Minuano Worksheet", "Combinations", "Andrew De Leonardis");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocument(msd);

        FractionStack fractionStack = new FractionStack();
        MeasureContext measureContext = new MeasureContext();
        measureContext.setFractionStack(fractionStack);
        measureContext.setDocumentAppender(msda);
        measureContext.setTimeSignature(new Fraction(6,8));
        measureContext.setGroupSize(new Fraction(3,16));

        IndexedStaffChordReader<Limb> chordReader = new IndexedStaffChordReader<>();
        chordReader.setAbstractStaff(staff, 0, staff.getLength());
        Fraction unit = new Fraction(1,16);

        while (!chordReader.isFinished()) {
            measureContext.checkContext();
            if (measureContext.measureEnded())
                measureContext.newMeasure();
            if (measureContext.groupEnded())
                measureContext.newGroup();
            measureContext.readChord(chordReader, unit, false);
        }

        msd.getDocumentXML().compile("music/Minuano Worksheet.mscx");
    }

    public static void addLine(
            int position,
            IndexedStaff<Limb,Note> staff,
            RandomizedRudimentCreator rrc,
            RandomProportionChooser<IndexedStaff<Limb,Note>> chooser,
            RandomProportionChooser<Character> randomLine
    ) {
        IndexedStaff<Limb,Note> indexedStaff = null;
        if (chooser != null)
            indexedStaff = chooser.getItem();
        if (indexedStaff == null) {
            String randomPattern = StringRandom.fromRandomProportionChooser(12, randomLine);
            IndexedStaff<Integer,Boolean> abstracterStaff = IndexedStaffRudimentCreator.fromLinearPatternString(randomPattern, randomPattern);
            indexedStaff = rrc.create(abstracterStaff, false);
        }
        staff.addNotes(indexedStaff, position, 1, true);
    }
}
