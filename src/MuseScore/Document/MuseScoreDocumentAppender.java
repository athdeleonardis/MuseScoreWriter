package MuseScore.Document;

import CustomMath.Fraction;
import MuseScore.Limb;
import MuseScore.Note.Note;
import XML.XMLObject;

import java.util.ArrayList;
import java.util.Collections;

public class MuseScoreDocumentAppender {
    private MuseScoreDocumentCreator documentCreator;
    private Fraction timeSignature;
    private boolean timeSigAdded;
    private boolean clefAdded;

    private XMLObject currentVoice;
    // There should be up to two voicings usually, only going to do one for now
    private Fraction measureDurationRemaining;

    public MuseScoreDocumentAppender(String title, String subtitle, String composer) {
        // System.out.println("Creating MuseScoreDocumentAppender");
        timeSignature = new Fraction(4, 4);
        this.documentCreator = new MuseScoreDocumentCreator(title, subtitle, composer);
        this.measureDurationRemaining = new Fraction(0, 1);
    }

    //
    // Getters
    //

    public MuseScoreDocumentCreator getDocumentCreator() {
        return this.documentCreator;
    }

    public Fraction getMeasureDurationRemaining() {
        return this.measureDurationRemaining;
    }

    public boolean measureIsFinished() {
        return currentVoice == null;
    }

    //
    // Public Methods
    //

    public MuseScoreDocumentAppender setTimeSignature(int numerator, int denominator) {
        Fraction newTimeSignature = new Fraction(numerator, denominator);
        if (!newTimeSignature.exactlyEquals(timeSignature)) {
            timeSignature = newTimeSignature;
            timeSigAdded = false;
        }
        return this;
    }

    public MuseScoreDocumentAppender addStaffText(String text, boolean isBelow) {
        if (currentVoice == null)
            newMeasure();

        XMLObject staffText = new XMLObject("StaffText");
        currentVoice.addChild(staffText);
        if (isBelow)
            staffText.addChild("placement", "below");
        staffText.addChild("text", text);
        return this;
    }

    // notes == null adds a rest
    public MuseScoreDocumentAppender addNotes(ArrayList<XMLObject> notes, Fraction duration) {
        // System.out.println("Adding notes");
        // System.out.println("Is rest: " + (notes==null));
        // System.out.println("Attempted duration: " + duration.getNumerator() + "/" + duration.getDenominator());
        if (currentVoice == null)
            newMeasure();
        // System.out.println("Measure Duration Remaining: " + measureDurationRemaining.getNumerator() + "/" + measureDurationRemaining.getDenominator());

        Fraction newMeasureRemaining = new Fraction(measureDurationRemaining).subtract(duration).simplify();

        Fraction overflow = null;
        if (newMeasureRemaining.isNegative()) {
            // Note goes over the bar,
            // Fill the rest of the bar with current note
            duration = measureDurationRemaining;
            overflow = newMeasureRemaining.negate();
            measureDurationRemaining = new Fraction(0, 1);
        }
        else
            measureDurationRemaining = newMeasureRemaining;
        // System.out.println("New duration: " + duration.getNumerator() + "/" + duration.getDenominator());
        // System.out.println("Measure Duration Remaining: " + measureDurationRemaining.getNumerator() + "/" + measureDurationRemaining.getDenominator());

        addNotesPrivate(notes, duration);

        if (measureDurationRemaining.isZero())
            currentVoice = null;

        // Fill overflow with rest
        if (overflow != null)
            addNotes(null, overflow);

        // System.out.println("");

        return this;
    }

    public MuseScoreDocumentAppender addNotes(ArrayList<Note> notes, Fraction duration, boolean addLimbText) {
        if (notes != null) {
            if (addLimbText) {
                Collections.sort(notes);
                for (Note note : notes) {
                    Limb limb = note.limb;
                    if (limb != null && limb.isArm())
                        addStaffText(note.limb.toString(), false);
                }
                for (int i = notes.size()-1; i > -1; i--) {
                    Note note = notes.get(i);
                    if (note.limb == null || note.limb.isArm())
                        continue;
                    addStaffText(note.limb.toString(), true);
                }
            }
            ArrayList<XMLObject> xmlNotes = new ArrayList(notes.size());
            for (Note note : notes)
                xmlNotes.add(note.xmlObject);
            addNotes(xmlNotes, duration);
        }
        else
            addNotes(null, duration);
        return this;
    }

    // Private Methods

    public void newMeasure() {
        XMLObject currentMeasure = new XMLObject("Measure");
        documentCreator.getStaff().addChild(currentMeasure);
        currentVoice = new XMLObject("voice");
        currentMeasure.addChild(currentVoice);

        if (!clefAdded) {
            addClef();
        }

        if (!timeSigAdded)
            addTimeSignature();

        measureDurationRemaining = new Fraction(timeSignature).simplify();
    }

    private void addTimeSignature() {
        currentVoice.addChild(documentCreator.createTimeSig(timeSignature.getNumerator(), timeSignature.getDenominator()));
        timeSigAdded = true;
    }

    // assumes the duration is simplified
    // for now, assumes the denominator must be even, i.e. whole half quarter eigth 16th
    private void addNoteDuration(XMLObject note, Fraction duration) {
        int durationType = duration.getDenominator();
        int numDots = 0;
        if (duration.getNumerator() > 1) {
            numDots = (duration.getNumerator() - 1) / 2;
            durationType = durationType / 2;
        }

        if (numDots != 0)
            note.addChild("dots", ""+numDots);
        note.addChild("durationType", MuseScoreDocumentCreator.getDurationType(durationType));
    }

    private void addClef() {
        currentVoice.addChild(documentCreator.createClef());
        currentVoice.addChild(documentCreator.createKeySig());
        clefAdded = true;
    }

    // Assumed the duration has been modified to fit the current measure
    // notes == null is a rest
    private void addNotesPrivate(ArrayList<XMLObject> notes, Fraction duration) {
        XMLObject entry = (notes == null) ? new XMLObject("Rest") : new XMLObject("Chord");
        addNoteDuration(entry, duration);
        if (notes != null) {
            entry.addChild("StemDirection", "up");
            for (XMLObject note : notes)
                entry.addChild(note);
        }
        currentVoice.addChild(entry);
    }
}
