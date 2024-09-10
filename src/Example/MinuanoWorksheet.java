package Example;

import MuseScoreWriter.AbstractStaff.AbstractStaff;
import MuseScoreWriter.AbstractStaff.AbstractStaffChordReader.AbstractStaffChordReader;
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
import MuseScoreWriter.Util.StringRandom;

import java.util.Arrays;

public class MinuanoWorksheet {
    public static void main(String[] args) {
        //
        // Setup probabilities and patterns
        //

        // Limb notes
        RandomizedRudimentCreator rightHand = new RandomizedRudimentCreator()
                .setPossibleLimbs(Arrays.asList(Limb.RightArm))
                .setPossibleNotes(Limb.RightArm, Arrays.asList("Ride"));
        RandomizedRudimentCreator leftHand = new RandomizedRudimentCreator()
                .setPossibleLimbs(Arrays.asList(Limb.LeftArm))
                .setPossibleNotes(Limb.LeftArm, Arrays.asList("CrossStick"));
        RandomizedRudimentCreator rightLeg = new RandomizedRudimentCreator()
                .setPossibleLimbs(Arrays.asList(Limb.RightLeg))
                .setPossibleNotes(Limb.RightLeg, Arrays.asList("Kick"));
        RandomizedRudimentCreator leftLeg = new RandomizedRudimentCreator()
                .setPossibleLimbs(Arrays.asList(Limb.LeftLeg))
                .setPossibleNotes(Limb.LeftLeg, Arrays.asList("HihatPedal"));

        // Limb patterns
        RandomProportionChooser<AbstractStaff<Limb, Note>> rightHandPatternChooser = new RandomProportionChooser<>();
        rightHandPatternChooser
                .setProportion(6, rightHand.create(AbstractRudimentCreator.fromLinearPatternString("", "OxOOOxOxOOOx"), false))
                .setProportion(2, rightHand.create(AbstractRudimentCreator.fromLinearPatternString("", "OxOOOxOOOxOO"), false))
                .setProportion(2, rightHand.create(AbstractRudimentCreator.fromLinearPatternString("", "OxOOOxOOOxOO"), false))
                .setProportion(2, rightHand.create(AbstractRudimentCreator.fromLinearPatternString("", "OOOxOOOxOOOx"), false))
                .setProportion(2, rightHand.create(AbstractRudimentCreator.fromLinearPatternString("", "OxOOOxOOOxOO"), false))
                .setProportion(1,null);


        RandomProportionChooser<AbstractStaff<Limb,Note>> leftHandPatternChooser = new RandomProportionChooser<>();
        leftHandPatternChooser
                .setProportion(6, leftHand.create(AbstractRudimentCreator.fromLinearPatternString("", "OxxOxxOxxOxx"), false))
                .setProportion(2, leftHand.create(AbstractRudimentCreator.fromLinearPatternString("", "OxxOxxOxOxOx"), false))
                .setProportion(2, leftHand.create(AbstractRudimentCreator.fromLinearPatternString("", "xOxxOxxOxxOx"), false))
                .setProportion(2, leftHand.create(AbstractRudimentCreator.fromLinearPatternString("", "OxxOxxOxOxOx"), false))
                .setProportion(1, null);

        RandomProportionChooser<AbstractStaff<Limb,Note>> leftLegPatternChooser = new RandomProportionChooser<>();
        leftLegPatternChooser
                .setProportion(12, leftLeg.create(AbstractRudimentCreator.fromLinearPatternString("", "OxxxOxxxOxxx"), false))
                .setProportion(4,  leftLeg.create(AbstractRudimentCreator.fromLinearPatternString("", "xxOxxxOxxxOx"), false))
                .setProportion(4,  leftLeg.create(AbstractRudimentCreator.fromLinearPatternString("", "xxOxxxxxOxxx"), false))
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
        AbstractStaff<Limb,Note> staff = new AbstractStaff<Limb,Note>("Staff").increaseToLength(12*numBars);
        for (int i = 0; i < numBars; i++) {
            addLine(i*12, staff, rightHand, rightHandPatternChooser, rightHandRandom);
            addLine(i*12, staff, leftHand, leftHandPatternChooser, leftHandRandom);
            addLine(i*12, staff, rightLeg, null, rightLegRandom);
            addLine(i*12, staff, leftLeg, leftLegPatternChooser, leftLegRandom);
        }

        //
        // Write staff to file
        //

        MuseScoreDocumentCreator msdc = new MuseScoreDocumentCreator("Minuano Worksheet", "Combinations", "Andrew De Leonardis");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocumentCreator(msdc);

        FractionStack fractionStack = new FractionStack();
        MeasureContext measureContext = new MeasureContext();
        measureContext.setFractionStack(fractionStack);
        measureContext.setDocumentAppender(msda);
        measureContext.setTimeSignature(new Fraction(6,8));
        measureContext.setGroupSize(new Fraction(3,16));

        AbstractStaffChordReader<Limb> chordReader = new AbstractStaffChordReader<>();
        chordReader.setAbstractStaff(staff, 0, staff.getLength());
        Fraction unit = new Fraction(1,16);

        while (!chordReader.isFinished()) {
            measureContext.checkContext();
            if (measureContext.measureEnded())
                measureContext.newMeasure();
            if (measureContext.groupEnded())
                measureContext.newGroup();
            Fraction maxRead = fractionStack.peek();
            System.out.println(fractionStack);
            Chord chord = chordReader.readChord(maxRead, unit);
            fractionStack.subtract(chord.duration);
            msda.addNotes(chord.notes, chord.duration, false);
        }

        msdc.getDocument().compile("music/Minuano Worksheet.mscx");
    }

    public static void addLine(
            int position,
            AbstractStaff<Limb,Note> staff,
            RandomizedRudimentCreator rrc,
            RandomProportionChooser<AbstractStaff<Limb,Note>> chooser,
            RandomProportionChooser<Character> randomLine
    ) {
        AbstractStaff<Limb,Note> abstractStaff = null;
        if (chooser != null)
            abstractStaff = chooser.getItem();
        if (abstractStaff == null) {
            String randomPattern = StringRandom.fromRandomProportionChooser(12, randomLine);
            AbstractStaff<Integer,Boolean> abstracterStaff = AbstractRudimentCreator.fromLinearPatternString(randomPattern, randomPattern);
            abstractStaff = rrc.create(abstracterStaff, false);
        }
        staff.addNotes(abstractStaff, position, 1, true);
    }
}
