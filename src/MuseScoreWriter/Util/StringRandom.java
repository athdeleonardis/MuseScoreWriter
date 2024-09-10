package MuseScoreWriter.Util;

public class StringRandom {
    public static String fromRandomProportionChooser(int length, RandomProportionChooser<Character> chooser) {
        char[] characters = new char[length];
        for (int i = 0; i < length; i++) {
            characters[i] = chooser.getItem();
        }
        return new String(characters);
    }
}
