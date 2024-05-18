package AbstractStaff.Rudiment;

import AbstractStaff.AbstractStaff;

public class AbstractRudimentCreator {
    private static AbstractRudimentCreator instance;

    private AbstractRudimentCreator() {

    }

    public static AbstractRudimentCreator getInstance() {
        if (instance == null)
            instance = new AbstractRudimentCreator();
        return instance;
    }

    public AbstractStaff<Integer,Boolean> create(String name) {
        switch (name.toLowerCase()) {
            case "rest":
                return new AbstractStaff<Integer,Boolean>("Rest").setNoteAtPosition(0,0,null);
            case "singlestroke":
                return new AbstractStaff<Integer,Boolean>("SingleStroke").setNoteAtPosition(0, 0, true);
            case "doublestroke":
                return new AbstractStaff<Integer,Boolean>("DoubleStroke")
                        .increaseToCapacity(2)
                        .setNoteAtPosition(0, 0, true)
                        .setNoteAtPosition(0, 1, true);
            case "herta":
                return new AbstractStaff<Integer,Boolean>("Herta")
                        .increaseToCapacity(6)
                        .setNoteAtPosition(0, 0, true)
                        .setNoteAtPosition(1, 1, true)
                        .setNoteAtPosition(2, 2, true)
                        .setNoteAtPosition(3, 4, true);
            case "paradiddle":
                return new AbstractStaff<Integer,Boolean>("Paradiddle")
                        .increaseToCapacity(4)
                        .setNoteAtPosition(0, 0, true)
                        .setNoteAtPosition(1, 1, true)
                        .setNoteAtPosition(2, 2, true)
                        .setNoteAtPosition(2, 3, true);
            case "fivestroke":
                return new AbstractStaff<Integer,Boolean>("FiveStroke")
                        .increaseToCapacity(5)
                        .setNoteAtPosition(0, 0, true)
                        .setNoteAtPosition(0, 1, true)
                        .setNoteAtPosition(1, 2, true)
                        .setNoteAtPosition(1, 3, true)
                        .setNoteAtPosition(2, 4, true);
            case "doubleparadiddle":
                return new AbstractStaff<Integer,Boolean>("DoubleParadiddle")
                        .increaseToCapacity(6)
                        .setNoteAtPosition(0, 0, true)
                        .setNoteAtPosition(1, 1, true)
                        .setNoteAtPosition(2, 2, true)
                        .setNoteAtPosition(2, 3, true)
                        .setNoteAtPosition(3, 4, true)
                        .setNoteAtPosition(3, 5, true);
            default:
                return null;
        }
    }

    public void logRudiment(AbstractStaff<Integer,Boolean> abstractRudiment) {
        String toLog = "Abstract Rudiment: { name: " + abstractRudiment.getName() + ", notes: { ";
        for (int position = 0; position < abstractRudiment.Length(); position++) {
            boolean hasNote = false;
            for (int limbId : abstractRudiment.getNoteNames()) {
                if (abstractRudiment.hasNoteAtPosition(limbId, position)) {
                    hasNote = true;
                    toLog += limbId;
                }
            }
            if (!hasNote)
                toLog += "_";
            toLog += ",";
        }
        toLog += " }}";
        System.out.println(toLog);
    }

    public void test() {
    }
}
