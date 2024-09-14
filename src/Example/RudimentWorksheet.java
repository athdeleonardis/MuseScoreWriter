package Example;

import Example.Arguments.ArgumentReader;
import Example.Arguments.ArgumentResultChecker;
import Example.Arguments.ArgumentResultUpdater;
import MuseScoreWriter.AbstractStaff.AbstractStaff;
import MuseScoreWriter.AbstractStaff.AbstractStaffChordReader.AbstractStaffChordReader;
import MuseScoreWriter.AbstractStaff.Rudiment.AbstractRudimentCreator;
import MuseScoreWriter.AbstractStaff.Rudiment.RandomizedRudimentCreator;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Set doc title: -t title
// Set number of rudiments: -n num
// Set time signature: -ts n/d
// Add rudiment: -r name subdivision proportion
// Set limb notes: -l name note1,note2,...,noteN

public class RudimentWorksheet {
    private static class ArgResult {
        public String title;
        public int numRudiments;
        public Fraction timeSignature;
        public RandomProportionChooser<Integer> rudimentChooser;
        public List<AbstractStaff<Integer,Boolean>> rudiments;
        public List<Integer> rudimentSubdivisions;
        public RandomizedRudimentCreator randomizedRudimentCreator;

        public ArgResult() {
            numRudiments = 64;
            timeSignature = new Fraction(4,4);
            rudimentChooser = new RandomProportionChooser<>();
            rudiments = new ArrayList<>();
            rudimentSubdivisions = new ArrayList<>();
            randomizedRudimentCreator = new RandomizedRudimentCreator();
        }
    }

    private static class ArgChecker implements ArgumentResultChecker<ArgResult> {
        @Override
        public void checkArgs(ArgResult argResult) {
            if (argResult.title == null)
                ArgumentReader.error("Title not provided.");
            if (argResult.rudiments.isEmpty())
                ArgumentReader.error("No rudiments provided.");
        }
    }

    private static class ArgUpdater implements ArgumentResultUpdater<ArgResult> {
        @Override
        public void updateFromArgs(String arg, ArgResult argResult, ArgumentReader<ArgResult> argumentReader) {
            switch (arg) {
                case "-t": {
                    String title = argumentReader.nextArg();
                    argResult.title = title;
                    break;
                }
                case "-ts": {
                    String timeSigStr = argumentReader.nextArg();
                    argResult.timeSignature = Fraction.parseFraction(timeSigStr);
                    break;
                }
                case "-n": {
                    String rudimentCountStr = argumentReader.nextArg();
                    argResult.numRudiments = Integer.parseInt(rudimentCountStr);
                    break;
                }
                case "-r": {
                    String rudimentName = argumentReader.nextArg();
                    String rudimentSubdivisionStr = argumentReader.nextArg();
                    String rudimentProportionStr = argumentReader.nextArg();
                    AbstractStaff<Integer,Boolean> rudiment = AbstractRudimentCreator.getInstance().create(rudimentName);
                    if (rudiment == null)
                        ArgumentReader.error("Couldn't parse rudiment: " + rudimentName);
                    int rudimentSubdivision = Integer.parseInt(rudimentSubdivisionStr);
                    int rudimentProportion = Integer.parseInt(rudimentProportionStr);
                    int rudimentIndex = argResult.rudiments.size();
                    argResult.rudimentChooser.setProportion(rudimentProportion, rudimentIndex);
                    argResult.rudiments.add(rudiment);
                    argResult.rudimentSubdivisions.add(rudimentSubdivision);
                    break;
                }
                case "-l": {
                    String limbName = argumentReader.nextArg();
                    String[] notes = argumentReader.nextArg().split(",");
                    Limb limb = Limb.parseLimb(limbName);
                    if (limb == null)
                        ArgumentReader.error("Couldn't parse limb '" + limbName + "'.");
                    argResult.randomizedRudimentCreator.setPossibleNotes(limb, Arrays.asList(notes));
                    break;
                }
                default: {
                    ArgumentReader.error("Couldn't parse argument: " + arg);
                }
            }
        }
    }

    public static void main(String[] args) {
        ArgResult argResult = new ArgResult();
        new ArgumentReader<>(Arrays.asList(args), argResult, new ArgUpdater(), new ArgChecker()).readAllArgs();

        MuseScoreDocument msd = MuseScoreDocumentCreator.create(argResult.title, "Rudiment Worksheet", "Andrew De Leonardis");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocument(msd);

        FractionStack fractionStack = new FractionStack();
        MeasureContext measureContext = new MeasureContext();
        measureContext.setDocumentAppender(msda);
        measureContext.setFractionStack(fractionStack);
        measureContext.setTimeSignature(argResult.timeSignature);
        measureContext.setGroupSize(new Fraction(1,4));

        AbstractStaffChordReader<Limb> chordReader = new AbstractStaffChordReader<>();
        for (int i = 0; i < argResult.numRudiments; i++) {
            int rudimentIndex = argResult.rudimentChooser.getItem();
            AbstractStaff<Integer,Boolean> rudimentChoice = argResult.rudiments.get(rudimentIndex);
            AbstractStaff<Limb,Note> rudiment = argResult.randomizedRudimentCreator.create(rudimentChoice, true);
            chordReader.setAbstractStaff(rudiment, 0, rudiment.getLength());
            Fraction readUnit = new Fraction(1,argResult.rudimentSubdivisions.get(rudimentIndex));

            while (!chordReader.isFinished()) {
                measureContext.checkContext();
                if (measureContext.measureEnded())
                    measureContext.newMeasure();
                if (measureContext.groupEnded()) {
                    Fraction groupSize = new Fraction(Fraction.min(new Fraction(1,4), fractionStack.peek()));
                    measureContext.setGroupSize(groupSize);
                    measureContext.newGroup();
                }
                measureContext.readChord(chordReader, readUnit, true);
            }
        }

        msd.getDocumentXML().compile("music/Rudiment Worksheet - " + argResult.title + ".mscx");
    }
}
