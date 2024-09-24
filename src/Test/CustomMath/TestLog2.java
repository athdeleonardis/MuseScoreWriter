package Test.CustomMath;

public class TestLog2 {
    public static void main(String args[]) {
        int n = 1000;
        for (int i = 0; i < n; i++) {
            int log = MuseScoreWriter.CustomMath.Integer.log2(i);
            int pow = MuseScoreWriter.CustomMath.Integer.pow2(log);
            System.out.println("" + i + " -> " + log + " -> " + pow);
        }
    }
}
