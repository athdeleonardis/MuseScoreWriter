package MuseScoreWriter.IndexedStaff.Rudiment;

import MuseScoreWriter.IndexedStaff.IndexedStaff;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;
import MuseScoreWriter.Util.GlobalRandom;

import java.util.*;

public class RandomizedRudimentCreator {
    private TreeMap<Limb,List<String>> limbToNotePossibilities;
    private Limb previousLimb;
    private Limb startingLimb;

    public RandomizedRudimentCreator() {
        this.limbToNotePossibilities = new TreeMap<>();
    }

    //
    // Public Methods
    //

    public RandomizedRudimentCreator setPossibleNotes(Limb limb, List<String> notePossibilities) {
        if (notePossibilities == null || notePossibilities.isEmpty())
            this.limbToNotePossibilities.remove(limb);
        else
            limbToNotePossibilities.put(limb, notePossibilities);
        return this;
    }

    public RandomizedRudimentCreator setStartingLimb(Limb limb) {
        this.startingLimb = limb;
        return this;
    }

    public RandomizedRudimentCreator setPreviousLimb(Limb limb) {
        this.previousLimb = limb;
        return this;
    }

    public boolean hasAtLeastTwoLimbs() {
        return this.limbToNotePossibilities.keySet().size() > 1;
    }

    public <T extends Comparable<T>> IndexedStaff<Limb,Note>  create(IndexedStaff<T,?> rudiment, boolean avoidLastLimb) {
        Map<T,Set<T>> overlappingLines = new TreeMap<>();
        for (TreeMap<T,?> notes : rudiment) {
            addOverlap(notes, overlappingLines);
        }

        Map<T,Limb> lineToLimb = new TreeMap<>();
        for (TreeMap<T,?> notes : rudiment) {
            if (!notes.isEmpty()) {
                chooseLimbs(limbToNotePossibilities.keySet(), lineToLimb, overlappingLines, notes.keySet(), avoidLastLimb);
                avoidLastLimb = true;
            }
        }

        IndexedStaff<Limb,Note> randomizedRudiment = new IndexedStaff<Limb,Note>(rudiment.getName()).increaseToLength(rudiment.getLength());
        Map<T,Note> lineToNote = new TreeMap<>();
        Iterator<? extends TreeMap<T, ?>> rudimentIterator = rudiment.iterator();
        Iterator<TreeMap<Limb,Note>> randomizedRudimentIterator = randomizedRudiment.iterator();
        while (rudimentIterator.hasNext()) {
            TreeMap<T,?> notes = rudimentIterator.next();
            TreeMap<Limb,Note> randomizedNotes = randomizedRudimentIterator.next();
            for (T line : notes.keySet()) {
                if (!lineToNote.containsKey(line)) {
                    String noteText = chooseNote(lineToLimb.get(line));
                    lineToNote.put(line, NoteCreator.getInstance().create(noteText));
                }
                randomizedNotes.put(lineToLimb.get(line), lineToNote.get(line));
            }
        }

        return randomizedRudiment;
    }

    //
    // Private Methods
    //

    private static <T extends Comparable<T>> void addOverlap(TreeMap<T,?> notes, Map<T,Set<T>> overlappingLines) {
        for (T line : notes.keySet()) {
            if (!overlappingLines.containsKey(line))
                overlappingLines.put(line, new HashSet<>());
            Set<T> overlap = overlappingLines.get(line);
            overlap.addAll(notes.keySet());
            overlap.remove(line);
        }
    }

    // Maps the remaining integers
    private <T extends Comparable<T>> void chooseLimbs(Set<Limb> limbs, Map<T,Limb> lineToLimb, Map<T,Set<T>> overlappingLines, Set<T> lines, boolean avoidLastLimb) {
        for (T line : lines) {
            if (startingLimb != null) {
                chooseLimb(lineToLimb, line, startingLimb);
                startingLimb = null;
                avoidLastLimb = true;
            }
            else if (!lineToLimb.containsKey(line)) {
                Limb avoidLimb = (avoidLastLimb) ? previousLimb : null;
                Limb limbChoice = getLimbChoice(avoidLimb, lineToLimb, overlappingLines, line);
                chooseLimb(lineToLimb, line, limbChoice);
                avoidLastLimb = true;
            }
        }
    }

    private <T extends Comparable<T>> void chooseLimb(Map<T,Limb> lineToLimb, T line, Limb limb) {
        lineToLimb.put(line, limb);
        setPreviousLimb(limb);
    }

    private <T extends Comparable<T>> Limb getLimbChoice(Limb avoidLimb, Map<T,Limb> lineToLimb, Map<T,Set<T>> overlappingLines, T line) {
        Set<Limb> choices = new TreeSet<>(limbToNotePossibilities.keySet());
        if (avoidLimb != null)
            choices.remove(avoidLimb);
        for (T overlappingLine : overlappingLines.get(line))
            if (lineToLimb.containsKey(overlappingLine))
                choices.remove(lineToLimb.get(overlappingLine));
        return GlobalRandom.nextElement(choices);
    }

    private String chooseNote(Limb limb) {
        return GlobalRandom.nextElement(limbToNotePossibilities.get(limb));
    }
}
