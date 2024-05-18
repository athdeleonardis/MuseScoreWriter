package MuseScore.Note;

import MuseScore.Limb;
import XML.XMLObject;

public class Note implements Comparable {
    public String name;
    public XMLObject xmlObject;
    public int pitch;
    public Limb limb;

    public Note(String name, int pitch, int tpc) {
        this.name = name;
        this.pitch = pitch;
        this.xmlObject = new XMLObject("Note")
                .addChild("pitch", ""+pitch)
                .addChild("tpc", ""+tpc);
    }

    public Note setLimb(Limb limb) {
        this.limb = limb;
        return this;
    }

    public int compareTo(Object other) {
        return this.pitch - ((Note)other).pitch;
    }

    public String toString() {
        return name;
    }
}
