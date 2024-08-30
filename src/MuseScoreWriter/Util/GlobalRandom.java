package MuseScoreWriter.Util;

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

    public static Object nextElement(List list) {
        return list.get(nextPositiveInt(list.size()));
    }
}
