package Test.AbstractStaff.Rudiment;

import MuseScoreWriter.AbstractStaff.AbstractStaff;
import MuseScoreWriter.AbstractStaff.AbstractStaffReader.AbstractStaffChunk;
import MuseScoreWriter.AbstractStaff.AbstractStaffReader.NoteGroupReader;
import MuseScoreWriter.AbstractStaff.Rudiment.AbstractRudimentCreator;
import MuseScoreWriter.AbstractStaff.Rudiment.RandomizedRudimentCreator;
import MuseScoreWriter.CustomMath.Fraction;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentAppender;
import MuseScoreWriter.MuseScore.Document.MuseScoreDocumentCreator;
import MuseScoreWriter.MuseScore.Limb;
import MuseScoreWriter.MuseScore.Note.Note;
import MuseScoreWriter.Util.RandomProportionChooser;

import java.util.Arrays;

public class RudimentTupletTest {
    public static void main(String args[]) {
        AbstractStaff<Integer,Boolean> abstractRudiment = AbstractRudimentCreator.getInstance().create("Paradiddle");

        RandomizedRudimentCreator rrc = new RandomizedRudimentCreator();
        rrc.setPossibleLimbs(Arrays.asList(Limb.RightArm, Limb.LeftArm));
        rrc.setLastLimb(Limb.LeftArm);
        rrc.setPossibleNotes(Limb.RightArm, Arrays.asList("Snare"));
        rrc.setPossibleNotes(Limb.LeftArm, Arrays.asList("Snare"));

        NoteGroupReader ngr = new NoteGroupReader()
                .setTimeSignature(new Fraction(4,4))
                .setGroupSize(new Fraction(1,4));

        int currentTuplet = 4;
        RandomProportionChooser<Integer> tupletChooser = new RandomProportionChooser<Integer>()
                .setProportion(3, 1)
                .setProportion(4, 1)
                .setProportion(5, 1);
        boolean wasInTuplet = false;

        MuseScoreDocumentCreator msdc = new MuseScoreDocumentCreator("RudimentTupletTest", "subtitle", "composer");
        MuseScoreDocumentAppender msda = new MuseScoreDocumentAppender().setDocumentCreator(msdc);

        Fraction unit = new Fraction(1,4);
        int numRudiments = 64;
        while (numRudiments > 0) {
            AbstractStaff<Limb, Note> rudiment = rrc.create(abstractRudiment, true);
            ngr.setRudiment(rudiment, 0, rudiment.getLength());

            // Read until the rudiment is finished
            // Whenever a new chunk is started, change the tuplet
            while (!ngr.rudimentIsFinished()) {
                if (ngr.groupIsFinished()) {
                    currentTuplet = tupletChooser.getItem();
                    if (wasInTuplet)
                        msda.endTuplet();
                    if (currentTuplet != 4)
                        msda.startTuplet(currentTuplet, unit);
                    wasInTuplet = currentTuplet != 4;
                }
                AbstractStaffChunk<Note> chunk = ngr.readChunk(new Fraction(1,4).divide(currentTuplet));
                msda.addNotes(chunk.notes, new Fraction(unit).divide(currentTuplet).multiply(chunk.length), true);
            }

            numRudiments--;
        }

        if (wasInTuplet)
            msda.endTuplet();

        msda.getDocumentCreator().getDocument().compile("music/RudimentTupletTest.mscx");
    }
}
