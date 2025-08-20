package MuseScoreWriter.TimeLine.Rudiment;

import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.IndexedStaff.Rudiment.RandomizedRudimentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.MuseScore.Note.NoteCreator;
import MuseScoreWriter.TimeLine.TimeLine;
import MuseScoreWriter.Util.GlobalRandom;

import javax.sound.sampled.Line;
import java.util.*;

public class TimeLineRandomizedRudimentCreator {
    private TreeMap<Limb, List<String>> limbToNotePossibilities;
    private Limb previousLimb;
    private Limb startingLimb;

    public TimeLineRandomizedRudimentCreator() { this.limbToNotePossibilities = new TreeMap<>(); }

    public void setPossibleNotes(Limb limb, List<String> notePossibilities) {
        if (notePossibilities == null || notePossibilities.isEmpty())
            limbToNotePossibilities.remove(limb);
        else
            limbToNotePossibilities.put(limb, notePossibilities);
    }

    public void setStartingLimb(Limb limb) { this.startingLimb = limb; }
    public void setPreviousLimb(Limb limb) { this.previousLimb = limb; }
    public boolean hasAtLeastTwoLimbs() { return limbToNotePossibilities.keySet().size() > 1; }

    public <T extends Comparable<T>> TimeLine<Map<Limb,Note>> create(TimeLine<Collection<T>> rudiment, boolean avoidLastLimb) {
        TimeLine<Map<Limb,Note>> randomizedRudiment = new TimeLine<>(rudiment.getName());
        randomizedRudiment.setDuration(rudiment.getDuration());

        Map<T, Set<T>> overlappingLines = findOverlappingLines(rudiment);
        Map<T, Limb> lineToLimb = chooseLimbs(overlappingLines, avoidLastLimb);
        Map<T, Note> lineToNote = chooseNotes(lineToLimb);

        for (Map.Entry<Fraction, Collection<T>> entry : rudiment) {
            Map<Limb, Note> randomizedEntry = new TreeMap<>();
            Fraction time = entry.getKey();
            Collection<T> lines = entry.getValue();
            for (T line : lines) {
                Limb limb = lineToLimb.get(line);
                Note note = lineToNote.get(line);
                randomizedEntry.put(limb, note);
            }
            randomizedRudiment.insert(time, randomizedEntry);
        }

        return randomizedRudiment;
    }

    private <T extends Comparable<T>> Map<T, Set<T>> findOverlappingLines(TimeLine<Collection<T>> rudiment) {
        Map<T, Set<T>> overlappingLines = new TreeMap<>();
        for (Map.Entry<Fraction,Collection<T>> entry : rudiment) {
            Collection<T> lines = entry.getValue();
            for (T line : lines) {
                Set<T> overlap = overlappingLines.get(line);
                if (overlap == null) {
                    overlap = new TreeSet<>();
                    overlappingLines.put(line, overlap);
                }
                overlap.addAll(lines);
                overlap.remove(line);
            }
        }
        return overlappingLines;
    }

    private <T extends Comparable<T>> Map<T,Limb> chooseLimbs(Map<T,Set<T>> overlappingLines, boolean avoidLastLimb) {
        Map<T,Limb> limbChoices = new TreeMap<>();
        Set<T> lines = overlappingLines.keySet();

        for (T line : lines) {
            if (startingLimb != null) {
                limbChoices.put(line, startingLimb);
                previousLimb = startingLimb;
                startingLimb = null;
                avoidLastLimb = true;
            }
            else if (!limbChoices.containsKey(line)) {
                Limb avoidLimb = avoidLastLimb ? previousLimb : null;
                Limb limbChoice = getLimbChoice(line, limbChoices, overlappingLines, avoidLimb);
                limbChoices.put(line, limbChoice);
                previousLimb = limbChoice;
                avoidLastLimb = true;
            }
        }

        return limbChoices;
    }

    private <T extends Comparable<T>> Limb getLimbChoice(T line, Map<T, Limb> limbChoices, Map<T, Set<T>> overlappingLines, Limb avoidLimb) {
        Set<Limb> choices = new TreeSet<>(limbToNotePossibilities.keySet());
        if (avoidLimb != null) {
            choices.remove(avoidLimb);
        }
        for (T overlappingLine : overlappingLines.get(line)) {
            Limb toRemove = limbChoices.get(overlappingLine);
            if (toRemove != null) {
                choices.remove(toRemove);
            }
        }
        return GlobalRandom.nextElement(choices);
    }

    private <T extends Comparable<T>> Map<T, Note> chooseNotes(Map<T, Limb> limbChoices) {
        Map<T, Note> noteChoices = new TreeMap<>();
        for (Map.Entry<T, Limb> entry : limbChoices.entrySet()) {
            T line = entry.getKey();
            Limb limb = entry.getValue();
            String note = GlobalRandom.nextElement(limbToNotePossibilities.get(limb));
            noteChoices.put(line, NoteCreator.getInstance().create(note));
        }
        return noteChoices;
    }
}
