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
import MuseScore.Note.NoteCreator;
import Util.GlobalRandom;

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

        List<List<String>> handNotes = new ArrayList();
        handNotes.add(Arrays.asList(new String[]{ "Snare" }));
        handNotes.add(Arrays.asList(new String[]{ "HighTom" }));
        handNotes.add(Arrays.asList(new String[]{ "HighTom" }));
        handNotes.add(Arrays.asList(new String[]{ "LowTom" }));
        handNotes.add(Arrays.asList(new String[]{ "Snare", "HighTom", "MidTom", "LowTom" }));

        List<Limb> limbPossibilities = Arrays.asList(new Limb[]{ Limb.RightArm, Limb.LeftArm });
        rrc.setPossibleLimbs(limbPossibilities);

        Fraction unitSize = new Fraction(1,16);

        int numRudiments = 64;
        while (numRudiments > 0) {
            AbstractStaff<Integer,Boolean> chosenAbstractRudiment = rudiments.get(GlobalRandom.nextPositiveInt(rudiments.size()));
            if (chosenAbstractRudiment.getName().equals("Rest"))
                rrc.setLastLimb(null);

            List<String> drumsChosen = handNotes.get(GlobalRandom.nextPositiveInt(handNotes.size()));
            rrc.setPossibleNotes(Limb.RightArm, drumsChosen);
            rrc.setPossibleNotes(Limb.LeftArm, drumsChosen);

            AbstractStaff<Limb,Note> concreteRudiment = rrc.create(chosenAbstractRudiment, true);
            ngr.setRudiment(concreteRudiment, 0, concreteRudiment.Length());
            while (!ngr.isFinished()) {
                AbstractStaffChunk<Note> chunk = ngr.readChunk(unitSize);
                Fraction notesDuration = new Fraction(unitSize).multiply(chunk.length);
                msda.addNotes(chunk.notes, notesDuration, true);
            }

            numRudiments--;
        }

        msda.getDocumentCreator().getDocument().compile("music/RandomizedRudimentCreatorTest.mscx");
    }
}
