package Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListRandom<T> {
    private List<Float> chanceProportions;
    private float total;

    public ListRandom() {
        chanceProportions = new ArrayList();
    }

    public ListRandom setProportion(int count, float proportion) {
        while (chanceProportions.size() < count+1)
            chanceProportions.add(0f);
        total -= chanceProportions.get(count);
        total += proportion;
        chanceProportions.set(count, proportion);
        return this;
    }

    // Shuffles the provided list in place
    public List<T> randomList(List<T> list) {
        int size = randomIndex();
        return randomList(list, size);
    }

    public int randomIndex() {
        float proportion = GlobalRandom.getInstance().nextFloat();
        proportion *= total;
        int index = 0;
        float proportion_accumulation = chanceProportions.get(0);
        while (proportion > proportion_accumulation) {
            proportion_accumulation += chanceProportions.get(index+1);
            index++;
        }
        return index;
    }

    public List<T> randomList(List<T> list, int size) {
        Collections.shuffle(list);
        List<T> returnList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            returnList.add(list.get(i));
        }
        return returnList;
    }
}
