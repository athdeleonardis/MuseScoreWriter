package MuseScoreWriter.MuseScore.Document;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.XML.XMLObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Removed all state based logic and made this class purely for appending
public class MuseScoreDocumentAppender {
    private MuseScoreDocument document;
    private XMLObject currentVoice;

    //
    // Setters
    //

    public MuseScoreDocumentAppender setDocument(MuseScoreDocument document) {
        this.document = document;
        return this;
    }

    //
    // Public Methods
    //

    public void addTimeSignature(Fraction timeSignature) {
        currentVoice.addChild(MuseScoreDocumentCreator.createTimeSig(timeSignature.getNumerator(), timeSignature.getDenominator()));
    }

    public void newMeasure() {
        XMLObject currentMeasure = new XMLObject("Measure");
        document.getStaffXML().addChild(currentMeasure);
        currentVoice = new XMLObject("voice");
        currentMeasure.addChild(currentVoice);
    }

    public void addClef() {
        currentVoice.addChild(MuseScoreDocumentCreator.createClef());
        currentVoice.addChild(MuseScoreDocumentCreator.createKeySig());
    }

    public MuseScoreDocumentAppender addStaffText(String text, boolean isBelow) {
        XMLObject staffText = new XMLObject("StaffText");
        currentVoice.addChild(staffText);
        if (isBelow)
            staffText.addChild("placement", "below");
        staffText.addChild("text", text);
        return this;
    }

    public MuseScoreDocumentAppender addNotes(List<Note> notes, Fraction duration, boolean addLimbText) {
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

    // notes == null is a rest
    public void addNotes(List<XMLObject> notes, Fraction duration) {
        XMLObject entry = (notes == null) ? new XMLObject("Rest") : new XMLObject("Chord");
        addNoteDuration(entry, duration);
        if (notes != null) {
            entry.addChild("StemDirection", "up");
            for (XMLObject note : notes)
                entry.addChild(note);
        }
        currentVoice.addChild(entry);
    }

    public void startTuplet(int numNotes, Fraction duration) {
        currentVoice.addChild(MuseScoreDocumentCreator.createTupletStart(numNotes, duration));
    }

    public void endTuplet() {
        currentVoice.addChild(MuseScoreDocumentCreator.createTupletEnd());
    }

    //
    // Private Methods
    //

    // assumes the duration is simplified
    // for now, assumes the denominator must be even, i.e. whole half quarter eigth 16th
    private void addNoteDuration(XMLObject note, Fraction duration) {
        int durationType = MuseScoreDocumentCreator.nextHigherDurationType(duration);
        int numDots = 0;
        if (duration.getNumerator() > 1) {
            numDots = (duration.getNumerator() - 1) / 2;
            durationType = durationType / 2;
        }

        if (numDots != 0)
            note.addChild("dots", ""+numDots);
        note.addChild("durationType", MuseScoreDocumentCreator.getDurationType(durationType));
    }
}
