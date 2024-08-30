package Test.Util;

import MuseScoreWriter.Util.GlobalRandom;
import MuseScoreWriter.Util.RandomProportionChooser;

import java.util.HashMap;

public class RandomProportionChooserTest {
    public static void main(String[] args) {
        RandomProportionChooser<Integer> randomProportionChooser = new RandomProportionChooser<Integer>();
        HashMap<Integer,Integer> counts = new HashMap<>();
        int numbersToChooseFrom = GlobalRandom.nextPositiveInt(20);
        for (int i = 0; i < numbersToChooseFrom; i++) {
            randomProportionChooser.setProportion(GlobalRandom.getInstance().nextFloat(), i);
            counts.put(i,0);
        }

        int num_tests = 1000000;
        for (int i = 0; i < num_tests; i++) {
            int numberChosen = randomProportionChooser.getItem();
            counts.put(numberChosen, counts.get(numberChosen) + 1);
        }

        for (int number : counts.keySet()) {
            float test_probability = (float)(counts.get(number))/num_tests;
            float actual_probability = randomProportionChooser.getProportion(number) / randomProportionChooser.getTotalProportion();
            float error = Math.abs(test_probability - actual_probability) * 100;
            if (test_probability != 0)
                error /= test_probability;
            System.out.println("Number: " + number + ", Probability: " + actual_probability + ", Test Probability: " + test_probability + ", Error: " + error + "%");
        }
    }
}
