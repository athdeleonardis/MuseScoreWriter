package Example;

import MuseScoreWriter.AbstractStaff.AbstractStaff;
import MuseScoreWriter.AbstractStaff.AbstractStaffChordReader.AbstractStaffChordReader;
import MuseScoreWriter.AbstractStaff.Rudiment.AbstractRudimentCreator;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Document.MeasureContext;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentAppender;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Chord;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.Util.FractionStack;
import MuseScoreWriter.Util.IntCombinator;
import Example.Arguments.ArgFileReader;

import java.util.*;

// Read args from file: -f fileName
// Set doc title: -t title
// Set time signature: -ts n/d
// Set group size: -g n/d
// Set unit: -u n/d
// Add ostinato: -o limb note1,note2,...,noteN ostinatoPatternStr

public class OstinatoCombinationsWorksheet {
    private static class ArgResult {
        public String title;
        public Fraction timeSignature;
        public Fraction groupSize;
        public Fraction unit;
        public Map<Limb, List<AbstractStaff<Limb, Note>>> limbOstinatos;

        public ArgResult() {
            this.timeSignature = new Fraction(4,4);
            this.groupSize = new Fraction(1,4);
            this.unit = new Fraction(1,16);
            this.limbOstinatos = new TreeMap<>();
        }
    }

    public static void main(String[] args) {
        ArgResult argResult = new ArgResult();
        readArgs(Arrays.asList(args), argResult);
        checkArgs(argResult);

        // Construct combinator
        List<Limb> limbs = argResult.limbOstinatos.keySet().stream().toList();
        List<Integer> numOstinatosPerLimb = new ArrayList<>();
        for (Limb limb : limbs)
            numOstinatosPerLimb.add(argResult.limbOstinatos.get(limb).size());
        IntCombinator intCombinator = new IntCombinator(numOstinatosPerLimb);

        AbstractStaff<Limb,Note> staff = new AbstractStaff<>("Staff");
        while (!intCombinator.isFinished()) {
            int[] ostinatoChoices = intCombinator.getValues();
            int staffPosition = staff.getLength();
            for (int i = 0; i < limbs.size(); i++) {
                Limb limb = limbs.get(i);
                AbstractStaff<Limb,Note> ostinato = argResult.limbOstinatos.get(limb).get(ostinatoChoices[i]);
                staff.addNotes(ostinato, staffPosition, 1, false);
            }
            intCombinator.next();
        }

        MuseScoreDocumentCreator msdc = new MuseScoreDocumentCreator(argResult.title, "Ostinato Combinations Worksheet", "Andrew De Leonardis");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocumentCreator(msdc);

        FractionStack fractionStack = new FractionStack();
        MeasureContext measureContext = new MeasureContext();
        measureContext.setFractionStack(fractionStack);
        measureContext.setDocumentAppender(msda);
        measureContext.setTimeSignature(argResult.timeSignature);
        measureContext.setGroupSize(new Fraction(1,4));

        AbstractStaffChordReader<Limb> chordReader = new AbstractStaffChordReader<>();
        chordReader.setAbstractStaff(staff, 0, staff.getLength());
        while (!chordReader.isFinished()) {
            measureContext.checkContext();

            if (measureContext.measureEnded())
                measureContext.newMeasure();
            if (measureContext.groupEnded())
                measureContext.newGroup();

            Fraction maxLength = fractionStack.peek();
            Chord chord = chordReader.readChord(maxLength, argResult.unit);
            fractionStack.subtract(chord.duration);
            msda.addNotes(chord.notes, chord.duration, true);
        }

        msdc.getDocument().compile("music/" + argResult.title + ".mscx");
    }

    private static void readArgs(List<String> args, ArgResult argResult) {
        for (String arg : args)
            System.out.println(arg);
        int index = 0;
        while (index < args.size()) {
            String arg = args.get(index++);
            switch (arg) {
                case "-f": {
                    String fileName = args.get(index++);
                    List<String> fileArgs = ArgFileReader.read(fileName);
                    readArgs(fileArgs, argResult);
                    break;
                }
                case "-t": {
                    argResult.title = args.get(index++);
                    break;
                }
                case "-ts": {
                    String tsString = args.get(index++);
                    argResult.timeSignature = Fraction.parseFraction(tsString);
                    break;
                }
                case "-g": {
                    String groupString = args.get(index++);
                    argResult.groupSize = Fraction.parseFraction(groupString);
                    break;
                }
                case "-u": {
                    String unitString = args.get(index++);
                    argResult.unit = Fraction.parseFraction(unitString);
                    break;
                }
                case "-o": {
                    String limbName = args.get(index++);
                    List<String> noteNames = Arrays.asList(args.get(index++).split(","));
                    String pattern = args.get(index++);

                    Limb limb = Limb.parseLimb(limbName);
                    if (limb == null)
                        error("Failed to parse limb '" + limbName + "'.");

                    AbstractStaff<Limb,Note> ostinato = AbstractRudimentCreator.ostinatoFromLinearPatternString(pattern, limb, pattern, noteNames);
                    addOstinato(limb, argResult.limbOstinatos, ostinato);
                    break;
                }
                default: {
                    error("Failed to parse argument '" + arg + "'.");
                }
            }
        }
    }

    private static void checkArgs(ArgResult argResult) {
        if (argResult.title == null)
            error("Title not provided.");
    }

    private static void addOstinato(Limb limb, Map<Limb,List<AbstractStaff<Limb,Note>>> map, AbstractStaff<Limb,Note> ostinato) {
        if (!map.containsKey(limb))
            map.put(limb, new ArrayList<>());
        map.get(limb).add(ostinato);
    }

    private static void error(String message) {
        System.out.println(message);
        System.exit(1);
    }
}
