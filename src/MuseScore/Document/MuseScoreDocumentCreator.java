package MuseScore.Document;

import XML.XMLObject;

public class MuseScoreDocumentCreator {
    private String title;
    private String subtitle;
    private String composer;
    private XMLObject document;
    private XMLObject staff;

    public MuseScoreDocumentCreator(String title, String subtitle, String composer) {
        this.title = title;
        this.subtitle = subtitle;
        this.composer = composer;

        create();
    }

    //
    // Private Methods
    //

    private XMLObject createHeader() {
        XMLObject title = new XMLObject("Text")
                .addChild("style", "title")
                .addChild("text", this.title);

        XMLObject subtitle = new XMLObject("Text")
                .addChild("style", "subtitle")
                .addChild("text", this.subtitle);

        XMLObject composer = new XMLObject("Text")
                .addChild("style", "composer")
                .addChild("text", this.composer);

        return new XMLObject("VBox")
                .addChild("height", "10")
                .addChild(title)
                .addChild(subtitle)
                .addChild(composer);
    }

    private XMLObject createPianoPart() {
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

    private void addMetaData(XMLObject score, String name, String value) {
        score.addChild(new XMLObject("metaTag", value).addTag("name", name));
    }

    private void create() {
        this.document = new XMLObject("museScore")
                .addTag("version", "4.10")
                .addChild(new XMLObject("programVersion", "4.1.1"))
                .addChild(new XMLObject("programRevision", "e4d1ddf"));

        this.staff = new XMLObject("Staff")
                .addTag("id", "1")
                .addChild(createHeader());

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
        score
                .addChild(createPianoPart())
                .addChild(staff);

        document.addChild(score);
    }

    //
    // Getters
    //

    public XMLObject getDocument() {
        return this.document;
    }

    public XMLObject getStaff() {
        return this.staff;
    }

    //
    // Public Methods
    //

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
            case 1: return "";
            case 2: return "";
            case 4: return "quarter";
            case 8: return "eighth";
            case 16: return "16th";
            default: return null;
        }
    }
}
