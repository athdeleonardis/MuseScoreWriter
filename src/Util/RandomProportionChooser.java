package Util;

import java.util.HashMap;
import java.util.Random;

// For choosing items with a probability proportional to their set proportion.
public class RandomProportionChooser<T> {
    private HashMap<T,Float> proportions;
    private float total;

    public RandomProportionChooser() {
        proportions = new HashMap<>();
        total = 0;
    }

    public RandomProportionChooser<T> setProportion(T item, float proportion) {
        if (proportions.containsKey(item)) {
            total -= proportions.get(item);
        }
        proportions.put(item, proportion);
        total += proportion;
        return this;
    }

    public T getItem() {
        float proportion = GlobalRandom.getInstance().nextFloat() * total;
        float proportionAccumulation = 0;
        T itemToReturn = null;
        for (T item : proportions.keySet()) {
            proportionAccumulation += proportions.get(item);
            itemToReturn = item;
            if (proportionAccumulation >= proportion)
                break;
        }
        return itemToReturn;
    }

    public float getProportion(T item) {
        return proportions.get(item);
    }

    public float getTotalProportion() {
        return total;
    }
}
