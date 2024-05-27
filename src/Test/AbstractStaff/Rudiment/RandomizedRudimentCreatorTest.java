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
import Util.RandomProportionChooser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomizedRudimentCreatorTest {
    public static void main(String[] args) {
        // Reader / writer objects
        Fraction timeSignature = new Fraction(4,4);
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender("RandomizedRudimentCreatorTest", "Subtitle", "Composer");
        NoteGroupReader ngr = new NoteGroupReader().setTimeSignature(timeSignature).setGroupSize(new Fraction(1,4));
        RandomizedRudimentCreator rrc = new RandomizedRudimentCreator();

        // Rudiments
        String[] rudimentNames = { "FiveStroke", "Herta", "Rest" };
        AbstractRudimentCreator arc = AbstractRudimentCreator.getInstance();
        ArrayList<AbstractStaff<Integer, Boolean>> rudiments = new ArrayList();
        for (String rudimentName : rudimentNames)
            rudiments.add(arc.create(rudimentName));

        // Notes
        List<String> handNotes = Arrays.asList("Snare", "HighTom", "MidTom", "LowTom");
        RandomProportionChooser<Integer> numDrumsChooser = new RandomProportionChooser<Integer>()
                .setProportion(1, 0.9f)
                .setProportion(4,0.1f);

        // Limbs
        List<Limb> limbPossibilities = Arrays.asList(new Limb[]{ Limb.RightArm, Limb.LeftArm });
        rrc.setPossibleLimbs(limbPossibilities);

        Fraction unitSize = new Fraction(1,16);

        // Append lots of rudiments to a staff
        AbstractStaff<Limb,Note> allNotes = new AbstractStaff<Limb, Note>("RandomizedRudimentCreatorTest");
        int numRudiments = 64;
        while (numRudiments > 0) {
            // Choose the rudiment
            AbstractStaff<Integer,Boolean> chosenAbstractRudiment = (AbstractStaff<Integer, Boolean>)GlobalRandom.nextElement(rudiments);
            if (chosenAbstractRudiment.getName().equals("Rest"))
                rrc.setLastLimb(null);

            // Place the rudiment on drums
            List<String> drumsChosen = (List<String>)ListRandom.randomList(handNotes, numDrumsChooser.getItem());
            rrc.setPossibleNotes(Limb.RightArm, drumsChosen);
            rrc.setPossibleNotes(Limb.LeftArm, drumsChosen);

            AbstractStaff<Limb,Note> concreteRudiment = rrc.create(chosenAbstractRudiment, true);

            allNotes.addNotes(concreteRudiment, allNotes.getLength(), 1, false);

            numRudiments--;
        }

        // Read the staff to a file
        ngr.setRudiment(allNotes, 0, allNotes.getLength());
        while (!ngr.isFinished()) {
            AbstractStaffChunk<Note> chunk = ngr.readChunk(unitSize);
            Fraction notesDuration = new Fraction(unitSize).multiply(chunk.length).simplify();
            System.out.println(chunk.toString());
            msda.addNotes(chunk.notes, notesDuration, true);
        }

        msda.getDocumentCreator().getDocument().compile("music/RandomizedRudimentCreatorTest.mscx");
    }
}
