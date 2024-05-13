package Util;
import java.util.ArrayList;

public class ArrayListUtil {
    public static void rotateArrayList(ArrayList arr, int amount) {
        for (int i = 0; i < amount; i++) {
            Object val = arr.get(0);
            arr.remove(0);
            arr.add(val);
        }
    }
}
