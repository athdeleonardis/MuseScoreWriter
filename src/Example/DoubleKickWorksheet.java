package Example;

import Example.Arguments.ArgumentReader;
import Example.Arguments.ArgumentResultChecker;
import Example.Arguments.ArgumentResultUpdater;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Document.*;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;
import MuseScoreWriter.TimeLine.Rudiment.TimeLineSimilarPatternCreator;
import MuseScoreWriter.TimeLine.TimeLine;
import MuseScoreWriter.TimeLine.TimeLineInserter;
import MuseScoreWriter.Util.RandomProportionChooser;

import java.util.*;

public class DoubleKickWorksheet {
    private static class ArgResult {
        public String title;
        public int numBars;
        public Fraction unit;
        public String rightHandPattern;
        public String leftHandPattern;
        public float rightHandPatternProbability;
        public float leftHandPatternProbability;
        public boolean canStartLeft;
        public List<Integer> kickLengths;
        public List<Float> kickLengthProportions;

        public ArgResult() {
            numBars = 16;
            unit = new Fraction(1, 16);
            rightHandPattern = "OxxxOxxxOxxxOxxx";
            leftHandPattern  = "xxxxxxxxOxxxxxxx";
            rightHandPatternProbability = 0.9f;
            leftHandPatternProbability = 0.9f;
            canStartLeft = false;
            kickLengths = new ArrayList<>();
            kickLengthProportions = new ArrayList<>();
        }
    }

    private static class ArgUpdater implements ArgumentResultUpdater<ArgResult> {
        @Override
        public void updateFromArgs(String arg, ArgResult argResult, ArgumentReader<ArgResult> argumentReader) {
            switch (arg) {
                case "-t" -> argResult.title = argumentReader.nextArg();
                case "-n" -> argResult.numBars = Integer.parseInt(argumentReader.nextArg());
                case "-u" -> argResult.unit = Fraction.parseFraction(argumentReader.nextArg());
                case "-rhp" -> argResult.rightHandPattern = argumentReader.nextArg();
                case "-lhp" -> argResult.leftHandPattern = argumentReader.nextArg();
                case "-rhpp" -> argResult.rightHandPatternProbability = Float.parseFloat(argumentReader.nextArg());
                case "-lhpp" -> argResult.leftHandPatternProbability = Float.parseFloat(argumentReader.nextArg());
                case "-l" -> argResult.canStartLeft = true;
                case "-kl" -> {
                    String[] kickLengthStrings = argumentReader.nextArg().split(",");
                    for (String kickLengthProportion : kickLengthStrings) {
                        String[] kickLength_proportion = kickLengthProportion.split(":");
                        int kickLength = Integer.parseInt(kickLength_proportion[0]);
                        float proportion = (kickLength_proportion.length < 2) ? 1 : Float.parseFloat(kickLength_proportion[1]);
                        argResult.kickLengths.add(kickLength);
                        argResult.kickLengthProportions.add(proportion);
                    }
                }
            }
        }
    }

    private static class ArgChecker implements ArgumentResultChecker<ArgResult> {
        @Override
        public void checkArgs(ArgResult argResult) {
            if (argResult.title == null) {
                ArgumentReader.error("Title not provided.");
            }
            if (argResult.kickLengths.isEmpty()) {
                ArgumentReader.error("No double kick lengths provided");
            }
            if (argResult.leftHandPattern.length() != argResult.rightHandPattern.length()) {
                ArgumentReader.error("Left and right hand pattern lengths don't match.");
            }
        }
    }

    private static class Data {
        public final String title;
        public final int numBars;
        public final int patternLength;
        public final Fraction unit;
        public final Fraction barLength;
        public final Fraction timeSignature;
        private final boolean canStartLeft;
        private final RandomProportionChooser<TimeLine<Map<Limb,Note>>> kickChooser;
        private final TimeLineSimilarPatternCreator leftHandPatternCreator;
        private final TimeLineSimilarPatternCreator rightHandPatternCreator;

        public Data(ArgResult argResult) {
            this.title = argResult.title.replace('_', ' ');
            this.numBars = argResult.numBars;
            this.patternLength = argResult.leftHandPattern.length();
            this.unit = argResult.unit;
            this.barLength = new Fraction(unit).multiply(patternLength);
            this.timeSignature = new Fraction(this.patternLength, 16).simplify();
            canStartLeft = argResult.canStartLeft;
            kickChooser = new RandomProportionChooser<>();

            this.leftHandPatternCreator = new TimeLineSimilarPatternCreator(
                    argResult.leftHandPattern,
                    NoteCreator.getInstance().create("Snare"),
                    Limb.LeftArm,
                    argResult.unit
            );
            this.rightHandPatternCreator = new TimeLineSimilarPatternCreator(
                    argResult.rightHandPattern,
                    NoteCreator.getInstance().create("Ride"),
                    Limb.RightArm,
                    argResult.unit
            );

            initializeKickChooser(argResult);
        }

        public TimeLine<Map<Limb, Note>> getHandPattern() {
            TimeLine<Map<Limb, Note>> leftHandPattern = leftHandPatternCreator.createPattern();
            TimeLine<Map<Limb, Note>> rightHandPattern = rightHandPatternCreator.createPattern();
            TimeLineInserter.insertTimeLine(leftHandPattern, rightHandPattern, Fraction.zero(), new Fraction(1, 1));
            return leftHandPattern;
        }

        public TimeLine<Map<Limb, Note>> getFootPattern() {
            return kickChooser.getItem();
        }

        private void initializeKickChooser(ArgResult argResult) {
            int numKickLengths = argResult.kickLengths.size();
            for (int index = 0; index < numKickLengths; index++) {
                int kickLength = argResult.kickLengths.get(index);
                float proportion = argResult.kickLengthProportions.get(index);
                kickChooser.setProportion(proportion, createKickPattern(kickLength, false));
                if (canStartLeft) {
                    kickChooser.setProportion(proportion, createKickPattern(kickLength, true));
                }
            }
        }

        private TimeLine<Map<Limb, Note>> createKickPattern(int length, boolean startLeft) {
            TimeLine<Map<Limb, Note>> pattern = new TimeLine<>("Kick_Pattern-Length_" + length);
            pattern.setDuration(new Fraction(this.unit).multiply(length + 1).simplify());
            boolean isLeft = startLeft;
            for (int index = 0; index < length; index++) {
                String noteName = (isLeft) ? "KickLeft" : "Kick";
                Note note = NoteCreator.getInstance().create(noteName);
                Limb limb = (isLeft) ? Limb.LeftLeg : Limb.RightLeg;
                Fraction notePosition = new Fraction(this.unit).multiply(index);
                TimeLineInserter.insert(pattern, notePosition, limb, note);
                isLeft = !isLeft;
            }
            return pattern;
        }
    }

    public static void main(String[] args) {
        Data data;
        {
            ArgResult argResult = new ArgResult();
            new ArgumentReader<>(Arrays.asList(args), argResult, new ArgUpdater(), new ArgChecker()).readAllArgs();
            data = new Data(argResult);
        }

        TimeLine<Map<Limb, Note>> staff = new TimeLine<>("Staff");
        addHands(data, staff);
        addLegs(data, staff);
        outputToFile(data, staff);
    }

    private static void addHands(Data data, TimeLine<Map<Limb, Note>> staff) {
        int numBars = data.numBars;
        Fraction barsDuration = new Fraction(data.barLength).multiply(numBars).simplify();
        staff.setDuration(barsDuration);

        for (int bar = 0; bar < numBars; bar++) {
            TimeLine<Map<Limb, Note>> handPattern = data.getHandPattern();
            Fraction position = new Fraction(data.barLength).multiply(bar).simplify();
            TimeLineInserter.insertTimeLine(staff, handPattern, position, new Fraction(1, 1));
        }
    }

    private static void addLegs(Data data, TimeLine<Map<Limb, Note>> staff) {
        Fraction durationToReach = staff.getDuration();
        Fraction duration = Fraction.zero();
        Fraction barLength = new Fraction(data.unit).multiply(data.patternLength).simplify();

        while (durationToReach.greaterThan(duration)) {
            TimeLine<Map<Limb, Note>> kickPattern = data.getFootPattern();
            TimeLineInserter.insertTimeLine(staff, kickPattern, duration, new Fraction(1,1));
            duration.add(kickPattern.getDuration()).simplify();
        }

        float numBarsFloat = new Fraction(duration).divide(barLength).getValue();
        int numBars = (int)Math.ceil(numBarsFloat);
        Fraction newDuration = new Fraction(barLength).multiply(numBars);
        staff.setDuration(newDuration);
    }

    private static void outputToFile(Data data, TimeLine<Map<Limb, Note>> staff) {
        MuseScoreDocument msd = MuseScoreDocumentCreator.create(data.title, "Double Kick Worksheet", "Andrew De Leonardis");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocument(msd);
        MuseScoreDocumentQuickAppender.AppendAll(msda, staff, data.timeSignature, new Fraction(1, 4), false);
        msd.getDocumentXML().compile("music/Double Kick Worksheet - " + data.title + ".mscx");
    }
}
