package Test.AbstractStaff.Rudiment;

import AbstractStaff.AbstractStaff;
import AbstractStaff.AbstractStaffReader.AbstractStaffChunk;
import AbstractStaff.AbstractStaffReader.NoteGroupReader;
import AbstractStaff.Rudiment.AbstractRudimentCreator;
import AbstractStaff.Rudiment.RandomizedRudimentCreator;
import CustomMath.Fraction;
import MuseScore.Document.MuseScoreDocumentAppender;
import MuseScore.Limb;
import MuseScore.Note.Note;
import Util.GlobalRandom;
import Util.ListRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomizedRudimentCreatorTest {
    public static void main(String[] args) {
        Fraction timeSignature = new Fraction(4,4);
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender("RandomizedRudimentCreatorTest", "Subtitle", "Composer");
        NoteGroupReader ngr = new NoteGroupReader().setTimeSignature(timeSignature).setGroupSize(new Fraction(1,4));
        RandomizedRudimentCreator rrc = new RandomizedRudimentCreator();

        String[] rudimentNames = { "FiveStroke", "Herta", "Rest" };
        AbstractRudimentCreator arc = AbstractRudimentCreator.getInstance();
        ArrayList<AbstractStaff<Integer, Boolean>> rudiments = new ArrayList();
        for (String rudimentName : rudimentNames)
            rudiments.add(arc.create(rudimentName));

        List<String> handNotes = Arrays.asList("Snare", "HighTom", "MidTom", "LowTom");
        ListRandom handNotesRandom = new ListRandom()
                .setProportion(1, 0.9f)
                .setProportion(4,0.1f);

        List<Limb> limbPossibilities = Arrays.asList(new Limb[]{ Limb.RightArm, Limb.LeftArm });
        rrc.setPossibleLimbs(limbPossibilities);

        Fraction unitSize = new Fraction(1,16);

        AbstractStaff<Limb,Note> allNotes = new AbstractStaff<Limb, Note>("RandomizedRudimentCreatorTest");
        int numRudiments = 64;
        while (numRudiments > 0) {
            AbstractStaff<Integer,Boolean> chosenAbstractRudiment = (AbstractStaff<Integer, Boolean>)GlobalRandom.nextElement(rudiments);
            if (chosenAbstractRudiment.getName().equals("Rest"))
                rrc.setLastLimb(null);

            List<String> drumsChosen = handNotesRandom.randomList(handNotes);
            rrc.setPossibleNotes(Limb.RightArm, drumsChosen);
            rrc.setPossibleNotes(Limb.LeftArm, drumsChosen);

            AbstractStaff<Limb,Note> concreteRudiment = rrc.create(chosenAbstractRudiment, true);

            allNotes.addNotes(concreteRudiment, allNotes.getLength(), 1, false);

            numRudiments--;
        }

        ngr.setRudiment(allNotes, 0, allNotes.getLength());
        while (!ngr.isFinished()) {
            AbstractStaffChunk<Note> chunk = ngr.readChunk(unitSize);
            Fraction notesDuration = new Fraction(unitSize).multiply(chunk.length);
            msda.addNotes(chunk.notes, notesDuration, true);
        }

        msda.getDocumentCreator().getDocument().compile("music/RandomizedRudimentCreatorTest.mscx");
    }
}
