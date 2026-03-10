package MuseScoreWriter.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// For choosing items with a probability proportional to their set proportion.
public class RandomProportionChooser<T> {
    private HashMap<T,Float> proportions;
    private float total;

    public RandomProportionChooser() {
        proportions = new HashMap<>();
        total = 0;
    }

    public RandomProportionChooser(RandomProportionChooser<T> other) {
        proportions = new HashMap<>(other.proportions);
        total = other.getTotalProportion();
    }

    public RandomProportionChooser<T> setProportion(float proportion, T item) {
        if (proportions.containsKey(item)) {
            total -= proportions.get(item);
        }
        proportions.put(item, proportion);
        total += proportion;
        return this;
    }

    public boolean remove(T item) {
        if (proportions.containsKey(item)) {
            float proportion = proportions.get(item);
            proportions.remove(item);
            total -= proportion;
            return true;
        }
        return false;
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

    public List<T> getItems(int nItems) {
        List<T> choices = new ArrayList<>(nItems);
        RandomProportionChooser<T> newChooser = new RandomProportionChooser<>(this);
        for (int i = 0; i < nItems; i++) {
            T choice = newChooser.getItem();
            newChooser.remove(choice);
            choices.add(choice);
        }
        return choices;
    }

    public List<T> getItems() { return proportions.keySet().stream().collect(Collectors.toList()); }
    public boolean isEmpty() { return proportions.isEmpty(); }

    public float getProportion(T item) {
        return proportions.get(item);
    }

    public float getTotalProportion() {
        return total;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("RandomProportionChooser=[");
        boolean appendComma = false;
        for (Map.Entry<T,Float> entry : proportions.entrySet()) {
            if (appendComma)
                str.append(", ");
            appendComma = true;
            str.append("{ entry=");
            str.append(entry.getKey());
            str.append(", proportion=");
            str.append(entry.getValue());
            str.append(" }");
        }
        str.append("]");
        return str.toString();
    }
}
