package Test.Util;

import MuseScoreWriter.Util.RandomProportionChooser;
import MuseScoreWriter.Util.StringRandom;

public class StringRandomTest {
    public static void main(String[] args) {
        RandomProportionChooser<Character> characterChooser = new RandomProportionChooser<>();
        characterChooser.setProportion(2, 'a')
                .setProportion(2,'b')
                .setProportion(1,'x');

        RandomProportionChooser<Integer> lengthChooser = new RandomProportionChooser<>();
        lengthChooser.setProportion(1, 3)
                .setProportion(1, 5)
                .setProportion(1, 7);

        int numRandomString = 10;
        for (int i = 0; i < numRandomString; i++) {
            int length = lengthChooser.getItem();
            String randomString = StringRandom.fromRandomProportionChooser(length, characterChooser);
            System.out.println(randomString);
        }
    }
}
