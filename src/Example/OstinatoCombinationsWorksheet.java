package Example;

import Example.Arguments.ArgumentReader;
import Example.Arguments.ArgumentResultChecker;
import Example.Arguments.ArgumentResultUpdater;
import MuseScoreWriter.IndexedStaff.IndexedStaff;
import MuseScoreWriter.IndexedStaff.IndexedStaffChordReader;
import MuseScoreWriter.IndexedStaff.Rudiment.IndexedStaffRudimentCreator;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Document.MeasureContext;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocument;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentAppender;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Chord;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.Util.FractionStack;
import MuseScoreWriter.Util.IntCombinator;

import java.util.*;
import java.util.stream.Collectors;

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
        public Map<Limb, List<IndexedStaff<Limb, Note>>> limbOstinatos;

        public ArgResult() {
            this.timeSignature = new Fraction(4,4);
            this.groupSize = new Fraction(1,4);
            this.unit = new Fraction(1,16);
            this.limbOstinatos = new TreeMap<>();
        }
    }

    private static class ArgChecker implements ArgumentResultChecker<ArgResult> {
        @Override
        public void checkArgs(ArgResult argResult) {
            if (argResult.title == null)
                ArgumentReader.error("Title not provided.");
            if (argResult.limbOstinatos.isEmpty())
                ArgumentReader.error("No ostinato provided.");
        }
    }

    private static class ArgUpdater implements ArgumentResultUpdater<ArgResult> {
        @Override
        public void updateFromArgs(String arg, ArgResult argResult, ArgumentReader<ArgResult> argumentReader) {
            switch (arg) {
                case "-t" -> argResult.title = argumentReader.nextArg();
                case "-ts" -> {
                    String tsString = argumentReader.nextArg();
                    argResult.timeSignature = Fraction.parseFraction(tsString);
                }
                case "-g" -> {
                    String groupString = argumentReader.nextArg();
                    argResult.groupSize = Fraction.parseFraction(groupString);
                }
                case "-u" -> {
                    String unitString = argumentReader.nextArg();
                    argResult.unit = Fraction.parseFraction(unitString);
                }
                case "-o" -> {
                    String limbName = argumentReader.nextArg();
                    List<String> noteNames = Arrays.asList(argumentReader.nextArg().split(","));
                    String pattern = argumentReader.nextArg();

                    Limb limb = Limb.parseLimb(limbName);
                    if (limb == null)
                        ArgumentReader.error("Failed to parse limb '" + limbName + "'.");

                    IndexedStaff<Limb, Note> ostinato = IndexedStaffRudimentCreator.ostinatoFromLinearPatternString(pattern, limb, pattern, noteNames);
                    addOstinato(limb, argResult.limbOstinatos, ostinato);
                }
                default -> ArgumentReader.error("Failed to parse argument '" + arg + "'.");
            }
        }
    }

    public static void main(String[] args) {
        ArgResult argResult = new ArgResult();
        new ArgumentReader<>(Arrays.asList(args), argResult, new ArgUpdater(), new ArgChecker()).readAllArgs();

        // Construct combinator
        List<Limb> limbs = argResult.limbOstinatos.keySet().stream().collect(Collectors.toList());
        List<Integer> numOstinatosPerLimb = new ArrayList<>();
        for (Limb limb : limbs)
            numOstinatosPerLimb.add(argResult.limbOstinatos.get(limb).size());
        IntCombinator intCombinator = new IntCombinator(numOstinatosPerLimb);

        IndexedStaff<Limb,Note> staff = new IndexedStaff<>("Staff");
        while (!intCombinator.isFinished()) {
            int[] ostinatoChoices = intCombinator.getValues();
            int staffPosition = staff.getLength();
            for (int i = 0; i < limbs.size(); i++) {
                Limb limb = limbs.get(i);
                IndexedStaff<Limb,Note> ostinato = argResult.limbOstinatos.get(limb).get(ostinatoChoices[i]);
                staff.addNotes(ostinato, staffPosition, 1, false);
            }
            intCombinator.next();
        }

        MuseScoreDocument msd = MuseScoreDocumentCreator.create(argResult.title, "Ostinato Combinations Worksheet", "Andrew De Leonardis");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocument(msd);

        FractionStack fractionStack = new FractionStack();
        MeasureContext measureContext = new MeasureContext();
        measureContext.setFractionStack(fractionStack);
        measureContext.setDocumentAppender(msda);
        measureContext.setTimeSignature(argResult.timeSignature);
        measureContext.setGroupSize(new Fraction(1,4));

        IndexedStaffChordReader<Limb> chordReader = new IndexedStaffChordReader<>();
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

        msd.getDocumentXML().compile("music/Ostinato Combinations Worksheet - " + argResult.title + ".mscx");
    }

    private static void addOstinato(Limb limb, Map<Limb,List<IndexedStaff<Limb,Note>>> map, IndexedStaff<Limb,Note> ostinato) {
        if (!map.containsKey(limb))
            map.put(limb, new ArrayList<>());
        map.get(limb).add(ostinato);
    }
}
