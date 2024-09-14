package Example;

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

    public static void main(String[] args) {
        ArgResult argResult = new ArgResult();
        parseArgs(args, argResult);

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

                Chord chord = chordReader.readChord(fractionStack.peek(), readUnit);
                fractionStack.subtract(chord.duration);

                msda.addNotes(chord.notes, chord.duration, true);
            }
        }

        msd.getDocumentXML().compile("music/" + argResult.title + ".mscx");
    }

    public static void parseArgs(String[] args, ArgResult argResult) {
        int index = 0;
        List<Limb> possibleLimbs = new ArrayList<>();
        while (index < args.length) {
            String arg = args[index++];
            switch (arg) {
                case "-t": {
                    String title = args[index++];
                    argResult.title = title;
                    break;
                }
                case "-ts": {
                    String timeSigStr = args[index++];
                    argResult.timeSignature = Fraction.parseFraction(timeSigStr);
                    break;
                }
                case "-n": {
                    String rudimentCountStr = args[index++];
                    argResult.numRudiments = Integer.parseInt(rudimentCountStr);
                    break;
                }
                case "-r": {
                    String rudimentName = args[index++];
                    String rudimentSubdivisionStr = args[index++];
                    String rudimentProportionStr = args[index++];
                    AbstractStaff<Integer,Boolean> rudiment = AbstractRudimentCreator.getInstance().create(rudimentName);
                    if (rudiment == null)
                        error("Couldn't parse rudiment: " + rudimentName);
                    int rudimentSubdivision = Integer.parseInt(rudimentSubdivisionStr);
                    int rudimentProportion = Integer.parseInt(rudimentProportionStr);
                    int rudimentIndex = argResult.rudiments.size();
                    argResult.rudimentChooser.setProportion(rudimentProportion, rudimentIndex);
                    argResult.rudiments.add(rudiment);
                    argResult.rudimentSubdivisions.add(rudimentSubdivision);
                    break;
                }
                case "-l": {
                    String limbName = args[index++];
                    String[] notes = args[index++].split(",");
                    Limb limb = Limb.parseLimb(limbName);
                    if (limb == null)
                        error("Couldn't parse limb '" + limbName + "'.");
                    possibleLimbs.add(limb);
                    argResult.randomizedRudimentCreator.setPossibleNotes(limb, Arrays.asList(notes));
                    break;
                }
                default: {
                    error("Couldn't parse argument: " + arg);
                }
            }
        }
        if (argResult.title == null)
            error("Title not provided.");
    }

    public static void error(String message) {
        System.out.println(message);
        System.exit(1);
    }
}
