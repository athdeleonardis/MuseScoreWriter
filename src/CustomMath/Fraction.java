package CustomMath;

import static CustomMath.Integer.gcd;

public class Fraction {
    private int numerator;
    private int denominator;

    public Fraction(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        // assert this.denominator > -1 ???
    }

    public Fraction(Fraction other) {
        this.numerator = other.getNumerator();
        this.denominator = other.getDenominator();
    }

    public String toString() {
        return "" + this.numerator + "/" + this.denominator;
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public Fraction subtract(Fraction other) {
        this.numerator = this.numerator * other.denominator - other.numerator * this.denominator;
        this.denominator = this.denominator * other.denominator;
        return this;
    }

    public Fraction simplify() {
        int f = (this.numerator > -1) ? gcd(this.numerator, this.denominator) : gcd(-this.numerator, this.denominator);
        this.numerator /= f;
        this.denominator /= f;
        return this;
    }

    public Fraction negate() {
        this.numerator = -this.numerator;
        return this;
    }

    public boolean isZero() {
        return this.numerator == 0;
    }

    public boolean isNegative() {
        return this.numerator < 0;
    }

    public boolean exactlyEquals(Fraction other) {
        return this.numerator == other.getNumerator() && this.denominator == other.getDenominator();
    }

    public Fraction min(Fraction other) {
        return (this.numerator * other.getDenominator() < other.getNumerator() * this.denominator) ? this : other;
    }

    public Fraction multiply(int multiplier) {
        this.numerator *= multiplier;
        return this;
    }

    public Fraction divide(Fraction other) {
        this.numerator *= other.getDenominator();
        this.denominator *= other.getNumerator();
        return this;
    }

    public float getValue() {
        return (float) this.numerator / this.denominator;
    }
}
