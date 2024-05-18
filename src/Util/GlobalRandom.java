package Util;

import java.util.Random;

public class GlobalRandom {
    private static Random instance;

    public static Random getInstance() {
        if (instance == null)
            instance = new Random();
        return instance;
    }

    public static int nextPositiveInt(int modulo) {
        return (getInstance().nextInt() & Integer.MAX_VALUE) % modulo;
    }
}
