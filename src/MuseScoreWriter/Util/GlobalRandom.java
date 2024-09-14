package MuseScoreWriter.Util;

import java.util.Collection;
import java.util.Random;
import java.util.List;

public class GlobalRandom<T> {
    private static Random instance;

    public static Random getInstance() {
        if (instance == null)
            instance = new Random();
        return instance;
    }

    public static int nextPositiveInt(int modulo) {
        return (getInstance().nextInt() & Integer.MAX_VALUE) % modulo;
    }

    public static <T> T nextElement(Collection<T> collection) {
        int nextElem = nextPositiveInt(collection.size());
        for (T elem : collection) if (nextElem-- == 0) return elem;
        return null;
    }
}
