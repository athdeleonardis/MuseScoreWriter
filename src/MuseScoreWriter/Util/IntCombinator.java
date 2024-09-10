package MuseScoreWriter.Util;

import java.util.Arrays;
import java.util.List;

public class IntCombinator {
    private int[] values;
    private int[] maxValues;
    private boolean finished;

    public IntCombinator(List<Integer> maxValues) {
        this.values = new int[maxValues.size()];
        this.maxValues = new int[maxValues.size()];
        for (int i = 0; i < maxValues.size(); i++)
            this.maxValues[i] = maxValues.get(i);
    }

    public void reset() {
        this.finished = false;
        Arrays.fill(values, 0);
    }

    public void next() {
        for (int i = 0; i < values.length; i++) {
            values[i] += 1;
            values[i] %= maxValues[i];
            if (values[i] == 0) {
                if (i == values.length-1)
                    finished = true;
            }
            else
                break;
        }
    }

    public int[] getValues() {
        return values;
    }

    public boolean isFinished() {
        return finished;
    }
}
