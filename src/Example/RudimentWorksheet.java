package Example;

import Example.Arguments.ArgumentReader;
import Example.Arguments.ArgumentResultChecker;
import Example.Arguments.ArgumentResultUpdater;
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
        public List<IndexedStaff<Integer,?>> rudiments;
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
                case "-t" -> argResult.title = argumentReader.nextArg();
                case "-ts" -> {
                    String timeSigStr = argumentReader.nextArg();
                    argResult.timeSignature = Fraction.parseFraction(timeSigStr);
                }
                case "-n" -> {
                    String rudimentCountStr = argumentReader.nextArg();
                    argResult.numRudiments = Integer.parseInt(rudimentCountStr);
                }
                case "-r" -> {
                    String rudimentName = argumentReader.nextArg();
                    String rudimentSubdivisionStr = argumentReader.nextArg();
                    String rudimentProportionStr = argumentReader.nextArg();
                    IndexedStaff<Integer, ?> rudiment = IndexedStaffRudimentCreator.getInstance().create(rudimentName);
                    if (rudiment == null)
                        ArgumentReader.error("Couldn't parse rudiment: " + rudimentName);
                    int rudimentSubdivision = Integer.parseInt(rudimentSubdivisionStr);
                    int rudimentProportion = Integer.parseInt(rudimentProportionStr);
                    int rudimentIndex = argResult.rudiments.size();
                    argResult.rudimentChooser.setProportion(rudimentProportion, rudimentIndex);
                    argResult.rudiments.add(rudiment);
                    argResult.rudimentSubdivisions.add(rudimentSubdivision);
                }
                case "-l" -> {
                    String limbName = argumentReader.nextArg();
                    String[] notes = argumentReader.nextArg().split(",");
                    Limb limb = Limb.parseLimb(limbName);
                    if (limb == null)
                        ArgumentReader.error("Couldn't parse limb '" + limbName + "'.");
                    argResult.randomizedRudimentCreator.setPossibleNotes(limb, Arrays.asList(notes));
                }
                default -> ArgumentReader.error("Couldn't parse argument: " + arg);
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

        IndexedStaffChordReader<Limb> chordReader = new IndexedStaffChordReader<>();
        for (int i = 0; i < argResult.numRudiments; i++) {
            int rudimentIndex = argResult.rudimentChooser.getItem();
            IndexedStaff<Integer,?> rudimentChoice = argResult.rudiments.get(rudimentIndex);
            IndexedStaff<Limb,Note> rudiment = argResult.randomizedRudimentCreator.create(rudimentChoice, true);
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
