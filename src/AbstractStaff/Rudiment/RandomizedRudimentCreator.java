package AbstractStaff.Rudiment;

import AbstractStaff.AbstractStaff;
import MuseScore.Limb;
import MuseScore.Note.Note;
import MuseScore.Note.NoteCreator;
import Test.AbstractStaff.Rudiment.RandomizedRudimentCreatorTest;
import Util.GlobalRandom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RandomizedRudimentCreator {
    private List<Limb> possibleLimbs;
    private HashMap<Limb,List<String>> notePossibilitiesPerLimb;
    private Limb previousLimb;

    public RandomizedRudimentCreator() {
        this.possibleLimbs = new ArrayList<>();
        this.notePossibilitiesPerLimb = new HashMap();
    }

    public RandomizedRudimentCreator setPossibleLimbs(List<Limb> possibleLimbs) {
        this.possibleLimbs = possibleLimbs;
        return this;
    }

    public RandomizedRudimentCreator setPossibleNotes(Limb limb, List<String> notePossibilities) {
        this.notePossibilitiesPerLimb.put(limb, notePossibilities);
        return this;
    }

    public RandomizedRudimentCreator setLastLimb(Limb limb) {
        this.previousLimb = limb;
        return this;
    }

    public AbstractStaff<Limb,Note> create(AbstractStaff abstractStaff, boolean avoidSequentialSameLimb) {
        AbstractStaff<Limb,Note> rudiment = new AbstractStaff(abstractStaff.getName()).increaseToCapacity(abstractStaff.Length());

        for (Object obj : abstractStaff.getNoteNames()) {
            // Choose limb
            Limb chosenLimb = possibleLimbs.get(GlobalRandom.nextPositiveInt(possibleLimbs.size()));
            while (avoidSequentialSameLimb && chosenLimb.equals(previousLimb))
                chosenLimb = possibleLimbs.get(GlobalRandom.nextPositiveInt(possibleLimbs.size()));
            previousLimb = chosenLimb;

            List<String> possibleNotes = notePossibilitiesPerLimb.get(chosenLimb);
            String chosenNoteName = possibleNotes.get(GlobalRandom.nextPositiveInt(possibleNotes.size()));
            Note chosenNote = NoteCreator.getInstance().create(chosenNoteName);
            chosenNote.setLimb(chosenLimb);

            for (int position = 0; position < abstractStaff.Length(); position++) {
                if (abstractStaff.hasNoteAtPosition(obj, position))
                    rudiment.setNoteAtPosition(chosenLimb, position, chosenNote);
            }
        }

        return rudiment;
    }
}
