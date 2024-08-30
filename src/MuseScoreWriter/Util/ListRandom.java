package Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListRandom<T> {
    // Warning: Shuffles the supplied list in place
    public static List randomList(List list, int size) {
        Collections.shuffle(list);
        List returnList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            returnList.add(list.get(i));
        }
        return returnList;
    }
}
