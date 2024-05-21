package Test.Util;

import Util.GlobalRandom;
import Util.ListRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListRandomTest {
    public static void main(String[] args) {
        ListRandom<Integer> listRandom = new ListRandom();

        int numProportions = GlobalRandom.nextPositiveInt(9)+1;
        float[] proportions = new float[numProportions];
        float totalProportion = 0f;
        for (int i = 0; i < numProportions; i++) {
            proportions[i] = GlobalRandom.getInstance().nextFloat();
            totalProportion += proportions[i];
            listRandom.setProportion(i, proportions[i]);
        }

        List<Integer> numbersToChooseFrom = Arrays.asList(new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        List<Integer> arraySizesChosen = new ArrayList(proportions.length);
        for (int i = 0; i < proportions.length; i++) {
            arraySizesChosen.add(0);
        }

        int num_tests = 1000000;
        for (int i = 0; i < num_tests; i++) {
            List<Integer> numbersChosen = listRandom.randomList(numbersToChooseFrom);
            int size = numbersChosen.size();
            arraySizesChosen.set(size, arraySizesChosen.get(size)+1);
        }
        for (int i = 0; i < arraySizesChosen.size(); i++) {
            float test_proportion = (float)(arraySizesChosen.get(i))/num_tests;
            float actual_proportion = proportions[i] / totalProportion;
            float error = Math.abs(test_proportion - actual_proportion) * 100;
            if (test_proportion != 0)
                error /= test_proportion;
            System.out.println("Size: " + i + ", Proportion: " + actual_proportion + ", Test Proportion: " + test_proportion + ", Error: " + error + "%");
        }
    }
}
