package MuseScoreWriter.MuseScore.Document;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.XML.XMLObject;

public class MuseScoreDocumentCreator {
    private MuseScoreDocumentCreator() {}

    //
    // Public Methods
    //

    public static MuseScoreDocument create(String title, String subtitle, String composer) {
        XMLObject document = createDocument();
        XMLObject staff = createStaff(title, subtitle, composer);
        XMLObject score = createScore(title, subtitle, composer);
        score.addChild(staff);
        document.addChild(score);
        return new MuseScoreDocument(document, staff);
    }

    public static XMLObject createClef() {
        return new XMLObject("Clef")
                .addChild("concertClefType", "PERC")
                .addChild("transposingClefType", "PERC")
                .addChild("isHeader", "1");
    }

    public static XMLObject createKeySig() {
        return new XMLObject("KeySig")
                .addChild("concertKey", "0");
    }

    public static XMLObject createTimeSig(int numerator, int denominator) {
        return new XMLObject("TimeSig")
                .addChild("sigN", ""+numerator)
                .addChild("sigD", ""+denominator);
    }

    public static String getDurationType(int durationType) {
        switch (durationType) {
            case 1: return "whole";
            case 2: return "half";
            case 4: return "quarter";
            case 8: return "eighth";
            case 16: return "16th";
            case 32: return "32nd";
            case 64: return "64th";
            default: return null;
        }
    }

    // Okay for non-nested tuplets
    public static XMLObject createTupletStart(int numNotesInTuple, Fraction duration) {
        duration = new Fraction(duration).simplify();
        System.out.println("Tuple duration: "+ duration);
        Fraction tupleUnitDuration = new Fraction(duration).divide(numNotesInTuple).simplify(); // Length of a single unit note in the tuple
        System.out.println("Tuple unit duration: " + tupleUnitDuration);

        int baseNote = nextHigherDurationType(tupleUnitDuration);
        System.out.println("Base note int: " + baseNote);
        int normalNotes = duration.multiply(baseNote).simplify().quotient(); // Should evenly divide
        int actualNotes = numNotesInTuple;

        return createTupletStart(normalNotes, actualNotes, baseNote);
    }

    public static XMLObject createTupletStart(int normalNotes, int actualNotes, int baseNote) {
        boolean requiresRatio = getDurationType(normalNotes) == null;

        XMLObject tupleStart = new XMLObject("Tuplet")
                .addChild("normalNotes", ""+normalNotes)
                .addChild("actualNotes", ""+actualNotes)
                .addChild("baseNote", getDurationType(baseNote));

        tupleStart.addChild(
                new XMLObject("Number")
                        .addChild("style", "tuplet")
                        .addChild("text", ""+actualNotes + ((requiresRatio) ? ":"+normalNotes : ""))
        );

        tupleStart.addChild("direction", "up");

        if (requiresRatio)
            tupleStart.addChild("numberType", "1");

        tupleStart.addChild("bracketType", "1");

        return tupleStart;
    }

    public static XMLObject createTupletEnd() {
        return new XMLObject("endTuplet");
    }

    // E.g. Triplets over a crotchet, 1/12, visually become quavers, 1/8
    // Quintuplets over a crotchet, 1/20, visually become semi-quavers, 1/16
    // Quintuplets over a dotted quaver, 3/16/5 = 3/80 (~1/26.6), visually become semi-quavers, 1/16
    public static int nextHigherDurationType(Fraction duration) {
        int flooredReciprocal = new Fraction(duration).reciprocate().quotient();
        int durationType = MuseScoreWriter.CustomMath.Integer.pow2(MuseScoreWriter.CustomMath.Integer.log2(flooredReciprocal));
        return durationType;
    }

    //
    // Private Methods
    //

    private static XMLObject createHeader(String title, String subtitle, String composer) {
        XMLObject titleObject = new XMLObject("Text")
                .addChild("style", "title")
                .addChild("text", title);

        XMLObject subtitleObject = new XMLObject("Text")
                .addChild("style", "subtitle")
                .addChild("text", subtitle);

        XMLObject composerObject = new XMLObject("Text")
                .addChild("style", "composer")
                .addChild("text", composer);

        return new XMLObject("VBox")
                .addChild("height", "10")
                .addChild(titleObject)
                .addChild(subtitleObject)
                .addChild(composerObject);
    }

    private static XMLObject createPianoPart() {
        XMLObject staffType = new XMLObject("StaffType")
                .addTag("group", "pitched")
                .addChild("name", "stdNormal");

        XMLObject staff = new XMLObject("Staff")
                .addTag("id", "1")
                .addChild(staffType)
                .addChild(new XMLObject("bracket").addTag("type", "1").addTag("span", "2").addTag("col", "2").addTag("visible", "1"));

        XMLObject clef = new XMLObject("clef", "F");

        XMLObject channel = new XMLObject("Channel")
                .addChild(new XMLObject("program").addTag("value", "0"))
                .addChild("synti", "Fluid");

        XMLObject instrument = new XMLObject("Instrument")
                .addTag("id", "piano")
                .addChild("longName", "Piano")
                .addChild("shortName", "Pno.")
                .addChild("minPitchP", "21")
                .addChild("maxPitchP", "108")
                .addChild("minPitchA", "21")
                .addChild("maxPitchA", "108")
                .addChild("instrumentId", "keyboard.piano")
                .addChild(clef)
                .addChild(channel);

        return new XMLObject("Part")
                .addTag("id", "1")
                .addChild(staff)
                .addChild("trackName", "Piano")
                .addChild(instrument);
    }

    private static void addMetaData(XMLObject score, String name, String value) {
        score.addChild(new XMLObject("metaTag", value).addTag("name", name));
    }

    private static XMLObject createScore(String title, String subtitle, String composer) {
        XMLObject score = new XMLObject("Score")
                .addChild("Division", "480")
                .addChild("showInvisible", "1")
                .addChild("showUnprintable", "1")
                .addChild("showFrames", "1")
                .addChild("showMargins", "0")
                .addChild("open", "1");
        addMetaData(score, "arranger", "");
        addMetaData(score, "composer", composer);
        addMetaData(score, "copyright", "");
        addMetaData(score, "creationDate", "");
        addMetaData(score, "lyricist", "");
        addMetaData(score, "movementNumber", "");
        addMetaData(score, "movementTitle", "");
        addMetaData(score, "platform", "");
        addMetaData(score, "poet", "");
        addMetaData(score, "source", "");
        addMetaData(score, "sourceRevisionId", "");
        addMetaData(score, "subtitle", subtitle);
        addMetaData(score, "translator", "");
        addMetaData(score, "workNumber", "");
        addMetaData(score, "workTitle", title);
        score.addChild(createPianoPart());
        return score;
    }

    private static XMLObject createDocument() {
        return new XMLObject("museScore")
                .addTag("version", "4.10")
                .addChild(new XMLObject("programVersion", "4.1.1"))
                .addChild(new XMLObject("programRevision", "e4d1ddf"));
    }

    private static XMLObject createStaff(String title, String subtitle, String composer) {
        return new XMLObject("Staff")
                .addTag("id", "1")
                .addChild(createHeader(title, subtitle, composer));
    }
}
