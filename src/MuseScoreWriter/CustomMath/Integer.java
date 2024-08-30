package MuseScoreWriter.CustomMath;

public class Integer {
    // Calculate the greatest common denominator of two integers
    public static int gcd(int a, int b) {
        if (b == 0)
            return a;
        return gcd(b, a % b);
    }

    // Just copied off stack overflow, don't even know if it's correct haha
    public static int log2(int bits) {
        int log = 0;
        if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
        if( bits >= 256 ) { bits >>>= 8; log += 8; }
        if( bits >= 16  ) { bits >>>= 4; log += 4; }
        if( bits >= 4   ) { bits >>>= 2; log += 2; }
        return log + ( bits >>> 1 );
    }

    public static int pow2(int log) {
        return 1 << log;
    }
}
