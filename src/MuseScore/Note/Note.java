package MuseScore.Note;

import MuseScore.Limb;
import XML.XMLObject;

public class Note implements Comparable {
    public String name;
    public XMLObject xmlObject;
    public int pitch;
    public Limb limb;

    public int compareTo(Object other) {
        return this.pitch - ((Note)other).pitch;
    }

    public String toString() {
        return name;
    }
}
