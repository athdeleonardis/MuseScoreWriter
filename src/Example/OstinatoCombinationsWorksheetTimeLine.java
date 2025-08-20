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
import MuseScoreWriter.TimeLine.Rudiment.TimeLineRudimentCreator;
import MuseScoreWriter.TimeLine.TimeLine;
import MuseScoreWriter.TimeLine.TimeLineInserter;
import MuseScoreWriter.TimeLine.TimeLineReader;
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

public class OstinatoCombinationsWorksheetTimeLine {
    private static class ArgResult {
        public String title;
        public Fraction timeSignature;
        public Fraction groupSize;
        public Fraction unit;
        public Map<Limb, List<TimeLine<Map<Limb,Note>>>> limbOstinatos;

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

                    TimeLine<Map<Limb, Note>> ostinato = TimeLineRudimentCreator.ostinatoFromLinearPatternString(pattern, limb, pattern, noteNames);
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

        // Construct all combinations of the ostinatos
        TimeLine<Map<Limb,Note>> staff = new TimeLine<>("Staff");
        staff.setDuration(new Fraction(0,1));
        while (!intCombinator.isFinished()) {
            int[] ostinatoChoices = intCombinator.getValues();
            Fraction staffDuration = staff.getDuration();
            for (int i = 0; i < limbs.size(); i++) {
                Limb limb = limbs.get(i);
                TimeLine<Map<Limb,Note>> ostinato = argResult.limbOstinatos.get(limb).get(ostinatoChoices[i]);
                TimeLineInserter.insertTimeLine(staff, ostinato, staffDuration, argResult.unit);
            }
            staffDuration.add(argResult.timeSignature).simplify();
            intCombinator.next();
        }

        // Document to append to
        MuseScoreDocument msd = MuseScoreDocumentCreator.create(argResult.title, "Ostinato Combinations Worksheet", "Andrew De Leonardis");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocument(msd);

        FractionStack fractionStack = new FractionStack();
        MeasureContext measureContext = new MeasureContext();
        measureContext.setFractionStack(fractionStack);
        measureContext.setDocumentAppender(msda);
        measureContext.setTimeSignature(argResult.timeSignature);
        measureContext.setGroupSize(argResult.groupSize);

        TimeLineReader<Map<Limb,Note>> timeLineReader = new TimeLineReader<>();
        timeLineReader.setTimeLine(staff);
        while (!timeLineReader.isFinished()) {
            measureContext.checkContext();

            if (measureContext.measureEnded())
                measureContext.newMeasure();
            if (measureContext.groupEnded())
                measureContext.newGroup();

            Fraction maxLength = fractionStack.peek();
            TimeLineReader<Map<Limb,Note>>.TimeLineData timeLineData = timeLineReader.read(maxLength);
            Fraction duration = timeLineData.duration;
            List<Note> notes = new ArrayList<>(timeLineData.entry.values());
            fractionStack.subtract(duration);
            msda.addNotes(notes, duration, true);
        }

        msd.getDocumentXML().compile("music/Ostinato Combinations Worksheet - " + argResult.title + ".mscx");
    }

    private static void addOstinato(Limb limb, Map<Limb,List<TimeLine<Map<Limb,Note>>>> map, TimeLine<Map<Limb,Note>> ostinato) {
        if (!map.containsKey(limb))
            map.put(limb, new ArrayList<>());
        map.get(limb).add(ostinato);
    }
}
