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

import java.util.Arrays;
import java.util.List;

import static Example.Arguments.ArgumentReader.*;

// read args from file: -f fileName
// set document title: -t title
// set tuplet proportion: -tu proportion t1
// set rudiment proportion: -r proportion rudimentName
// set limb note: -l limb n1,n2,...,nN
// set number of rudiments: -n numRudiments

public class TupletWorksheet {
    private static class ArgResult {
        public String title;
        public int numRudiments;
        public RandomProportionChooser<Integer> tupletChooser;
        public RandomProportionChooser<IndexedStaff<Integer,?>> rudimentChooser;
        public RandomizedRudimentCreator rrc;

        public ArgResult() {
            this.tupletChooser = new RandomProportionChooser<>();
            this.rudimentChooser = new RandomProportionChooser<>();
            this.rrc = new RandomizedRudimentCreator();
        }
    }

    private static class ArgChecker implements ArgumentResultChecker<ArgResult> {
        @Override
        public void checkArgs(ArgResult argResult) {
            if (argResult.title == null)
                error("Document title not provided.");
            if (argResult.tupletChooser.isEmpty())
                error("No tuplets provided.");
            if (argResult.rudimentChooser.isEmpty())
                error("No rudiments provided.");
            if (!argResult.rrc.hasAtLeastTwoLimbs())
                error("At least two limbs need to be provided.");
        }
    }

    public static class ArgUpdater implements ArgumentResultUpdater<ArgResult> {
        @Override
        public void updateFromArgs(String arg, ArgResult argResult, ArgumentReader<ArgResult> argumentReader) {
            switch (arg) {
                case "-t" -> argResult.title = argumentReader.nextArg();
                case "-n" -> argResult.numRudiments = Integer.parseInt(argumentReader.nextArg());
                case "-tu" -> {
                    float proportion = Float.parseFloat(argumentReader.nextArg());
                    int tuplet = Integer.parseInt(argumentReader.nextArg());
                    argResult.tupletChooser.setProportion(proportion, tuplet);
                }
                case "-r" -> {
                    float proportion = Float.parseFloat(argumentReader.nextArg());
                    IndexedStaff<Integer, ?> rudiment = IndexedStaffRudimentCreator.getInstance().create(argumentReader.nextArg());
                    argResult.rudimentChooser.setProportion(proportion, rudiment);
                }
                case "-l" -> {
                    Limb limb = Limb.parseLimb(argumentReader.nextArg());
                    List<String> noteNames = Arrays.asList(argumentReader.nextArg().split(","));
                    argResult.rrc.setPossibleNotes(limb, noteNames);
                }
                default -> error("Unable to parse argument '" + arg + "'.");
            }
        }
    }

    public static void main(String[] args) {
        ArgResult argResult = new ArgResult();
        new ArgumentReader<>(Arrays.asList(args), argResult, new ArgUpdater(), new ArgChecker()).readAllArgs();

        MuseScoreDocument msd = MuseScoreDocumentCreator.create(argResult.title, "Tuplet Worksheet", "Andrew De Leonardis");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocument(msd);

        FractionStack fractionStack = new FractionStack();
        MeasureContext measureContext = new MeasureContext();
        measureContext.setFractionStack(fractionStack);
        measureContext.setTimeSignature(new Fraction(4,4));
        measureContext.setGroupSize(new Fraction(1,4));
        measureContext.setDocumentAppender(msda);

        int numRudimentsRemaining = argResult.numRudiments;
        int currentTuplet = 0;
        Fraction currentUnit = null;
        IndexedStaffChordReader<Limb> chordReader = new IndexedStaffChordReader<>();
        while (numRudimentsRemaining > 0) {
            IndexedStaff<Limb, Note> rudiment = argResult.rrc.create(argResult.rudimentChooser.getItem(), true);
            chordReader.setAbstractStaff(rudiment, 0, rudiment.getLength());

            while (!chordReader.isFinished()) {
                measureContext.checkContext();
                if (measureContext.measureEnded())
                    measureContext.newMeasure();
                if (measureContext.groupEnded()) {
                    measureContext.newGroup();
                    currentTuplet = argResult.tupletChooser.getItem();
                    currentUnit = new Fraction(1, 4*currentTuplet);
                    System.out.println("Current tuplet: " + currentTuplet);
                    if (currentTuplet != 4)
                        measureContext.newTuplet(currentTuplet, fractionStack.peek());
                }
                measureContext.readChord(chordReader, currentUnit, true);
            }
            numRudimentsRemaining--;
        }

        msd.getDocumentXML().compile("music/Tuplet Worksheet - " + argResult.title + ".mscx");
    }
}
