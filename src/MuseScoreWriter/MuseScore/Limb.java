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

    public static Limb parseLimb(String str) {
        switch (str.toLowerCase()) {
            case "leftfoot":
            case "leftleg":
                return LeftLeg;
            case "rightfoot":
            case "rightleg":
                return RightLeg;
            case "lefthand":
            case "leftarm":
                return LeftArm;
            case "righthand":
            case "rightarm":
                return RightArm;
        }
        return null;
    }
}
