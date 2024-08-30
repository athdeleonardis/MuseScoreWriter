package MuseScoreWriter.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListRandom {
    // Warning: Shuffles the supplied list in place
    public static <T> List<T> randomList(List<T> list, int size) {
        Collections.shuffle(list);
        List<T> returnList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            returnList.add(list.get(i));
        }
        return returnList;
    }
}
