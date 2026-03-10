package Example;

import Example.Arguments.ArgumentReader;
import Example.Arguments.ArgumentResultChecker;
import Example.Arguments.ArgumentResultUpdater;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Document.MeasureContext;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocument;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentAppender;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;
import MuseScoreWriter.TimeLine.TimeLine;
import MuseScoreWriter.TimeLine.TimeLineReader;
import MuseScoreWriter.TimeLine.TupletFinder;
import MuseScoreWriter.Util.FractionStack;
import MuseScoreWriter.Util.GlobalRandom;
import MuseScoreWriter.Util.RandomProportionChooser;
import MuseScoreWriter.XML.XMLObject;

import java.util.*;

import static Example.Arguments.ArgumentReader.error;

public class TupletOverlappingWorksheet {
    private static class ArgResult {
        public String title;
        public int numTuplets;
        public Map<Limb, RandomProportionChooser<Integer>> limbTupletChoices;
        public Map<Limb, RandomProportionChooser<Note>> limbNoteChoices;

        public ArgResult() {
            this.numTuplets = 16;
            this.limbTupletChoices = new TreeMap<>();
            this.limbNoteChoices = new TreeMap<>();
        }
    }

    private static class ArgChecker implements ArgumentResultChecker<ArgResult> {
        @Override
        public void checkArgs(ArgResult argResult) {
            if (argResult.title == null) {
                error("Document title not provided.");
            }
            if (argResult.limbTupletChoices.isEmpty()) {
                error("No tuplets provided.");
            }
            if (argResult.limbNoteChoices.isEmpty()) {
                error("No notes chosen.");
            }
        }
    }

    private static class ArgUpdater implements ArgumentResultUpdater<ArgResult> {
        @Override
        public void updateFromArgs(String arg, ArgResult argResult, ArgumentReader<ArgResult> argumentReader) {
            switch (arg) {
                case "-t" -> argResult.title = argumentReader.nextArg();
                case "-n" -> argResult.numTuplets = Integer.parseInt(argumentReader.nextArg());
                case "-no" -> {
                    Limb limb = Limb.parseLimb(argumentReader.nextArg());
                    Note note = NoteCreator.getInstance().create(argumentReader.nextArg());
                    float proportion = Float.parseFloat(argumentReader.nextArg());
                    RandomProportionChooser<Note> noteChoices = argResult.limbNoteChoices.get(limb);
                    if (noteChoices == null) {
                        noteChoices = new RandomProportionChooser<>();
                        argResult.limbNoteChoices.put(limb, noteChoices);
                    }
                    noteChoices.setProportion(proportion, note);
                }
                case "-tu" -> {
                    Limb limb = Limb.parseLimb(argumentReader.nextArg());
                    int tuplet = Integer.parseInt(argumentReader.nextArg());
                    float proportion = Float.parseFloat(argumentReader.nextArg());
                    RandomProportionChooser<Integer> tupletChoices = argResult.limbTupletChoices.get(limb);
                    if (tupletChoices == null) {
                        tupletChoices = new RandomProportionChooser<>();
                        argResult.limbTupletChoices.put(limb, tupletChoices);
                    }
                    tupletChoices.setProportion(proportion, tuplet);
                }
            }
        }
    }

    public static void main(String[] args) {
        ArgResult argResult = new ArgResult();
        new ArgumentReader<>(Arrays.asList(args), argResult, new ArgUpdater(), new ArgChecker()).readAllArgs();
        System.out.println(argResult.limbTupletChoices);

        TimeLine<Map<Limb,Note>> staff = new TimeLine<>("Staff");
        for (int i = 0; i < argResult.numTuplets; i++) {
            Fraction position = staff.getDuration();
            for (Map.Entry<Limb,RandomProportionChooser<Integer>> entry : argResult.limbTupletChoices.entrySet()) {
                Limb limb = entry.getKey();
                int tuplet = entry.getValue().getItem();
                for (int j = 0; j < tuplet; j++) {
                    Note note = argResult.limbNoteChoices.get(limb).getItem();
                    Fraction time = new Fraction(position).add(new Fraction(j,4*tuplet)).simplify();
                    Map<Limb,Note> notes = staff.getEntry(time);
                    if (notes == null) {
                        notes = new TreeMap<>();
                        staff.insert(time, notes);
                    }
                    notes.put(limb, note);
                }
            }
            position.add(new Fraction(1,4)).simplify();
        }
        System.out.println(staff);

        MuseScoreDocument msd = MuseScoreDocumentCreator.create(argResult.title, "Tuplet Overlapping Worksheet", "Andrew De Leonardis");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocument(msd);

        FractionStack fractionStack = new FractionStack();
        MeasureContext measureContext = new MeasureContext();
        measureContext.setFractionStack(fractionStack);
        measureContext.setTimeSignature(new Fraction(4,4));
        measureContext.setGroupSize(new Fraction(1,4));
        measureContext.setDocumentAppender(msda);
        measureContext.newMeasure();
        measureContext.newGroup();

        TimeLineReader<Map<Limb,Note>> tlr = new TimeLineReader<>();
        List<TimeLineReader<Map<Limb,Note>>.TimeLineData> data = new ArrayList<>();
        tlr.setTimeLine(staff);
        List<TimeLineReader<Map<Limb,Note>>.TimeLineData> timeLineDataList = new ArrayList<>();
        while (!tlr.isFinished()) {
            measureContext.checkContext();

            if (measureContext.groupEnded()) {
                dumpData(msda, timeLineDataList);
            }
            if (measureContext.measureEnded()) {
                measureContext.newMeasure();
            }
            if (measureContext.groupEnded()) {
                measureContext.newGroup();
            }
            TimeLineReader<Map<Limb,Note>>.TimeLineData timeLineData = tlr.read(fractionStack.peek());
            timeLineDataList.add(timeLineData);
            fractionStack.subtract(timeLineData.duration);
        }
        dumpData(msda, timeLineDataList);

        msd.getDocumentXML().compile("music/Tuplet Overlapping Worksheet - " + argResult.title + ".mscx");
    }

    private static void dumpData(MuseScoreDocumentAppender msda, List<TimeLineReader<Map<Limb,Note>>.TimeLineData> timeLineDataList) {
        int tuplet = TupletFinder.findTuplet(timeLineDataList, new Fraction(1,4));
        System.out.println(timeLineDataList);

        System.out.println(tuplet);
        int checkIsTuplet = tuplet;
        while (checkIsTuplet % 2 == 0) {
            checkIsTuplet = checkIsTuplet / 2;
        }
        boolean isTuplet = checkIsTuplet != 1 && checkIsTuplet != 0;
        if (isTuplet) {
            msda.startTuplet(tuplet, new Fraction(1,4));
        }
        for (TimeLineReader<Map<Limb,Note>>.TimeLineData timeLineData : timeLineDataList) {
            msda.addNotes(new ArrayList<>(timeLineData.entry.values()), timeLineData.duration, true);
        }
        if (isTuplet) {
            msda.endTuplet();
        }

        timeLineDataList.clear();
    }
}
