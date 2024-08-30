package MuseScoreWriter.MuseScore;

public enum Limb {
    LeftLeg,
    RightLeg,
    LeftArm,
    RightArm;

    public String toString() {
        switch (this) {
            case LeftLeg: return "L";
            case RightLeg: return "R";
            case LeftArm: return "L";
            case RightArm: return "R";
        }
        return null;
    }

    public boolean isArm() {
        return this == LeftArm || this == RightArm;
    }
}
