package Test.CustomMath;

import MuseScoreWriter.CustomMath.Fraction;

public class TestFraction {
    public static void main(String[] args) {
        Fraction fraction1 = new Fraction(1,2);
        System.out.println(fraction1);
        Fraction toSubtract = new Fraction(1,3);
        Fraction fraction2 = new Fraction(fraction1).subtract(toSubtract);
        System.out.println(fraction1 + " - " + toSubtract + " = " + fraction2);
        Integer toMultiply1 = 2;
        Fraction fraction3 = new Fraction(fraction2).multiply(toMultiply1);
        System.out.println(fraction2 + " * " + toMultiply1 + " = " + fraction3);
        Fraction simplified = new Fraction(fraction3).simplify();
        System.out.println(fraction3 + " = " + simplified);
        System.out.println(simplified + " = " + simplified.getValue());
    }
}
