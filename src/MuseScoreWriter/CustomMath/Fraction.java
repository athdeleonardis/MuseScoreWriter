package MuseScoreWriter.CustomMath;

import static MuseScoreWriter.CustomMath.Integer.gcd;

public class Fraction extends Object implements Comparable<Fraction> {
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

    public Fraction add(Fraction other) {
        this.numerator = this.numerator * other.denominator + other.numerator * this.denominator;
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

    public boolean equals(Fraction other) {
        return this.numerator * other.denominator == other.numerator * this.denominator;
    }

    public static Fraction min(Fraction a, Fraction b) {
        return (a.numerator * b.getDenominator() < b.getNumerator() * a.denominator) ? a : b;
    }

    public Fraction multiply(int multiplier) {
        this.numerator *= multiplier;
        return this;
    }

    public Fraction multiply(Fraction other) {
        this.numerator *= other.numerator;
        this.denominator *= other.denominator;
        return this;
    }

    public Fraction divide(Fraction other) {
        this.numerator *= other.getDenominator();
        this.denominator *= other.getNumerator();
        return this;
    }

    public Fraction divide(int divisor) {
        this.denominator *= divisor;
        return this;
    }

    public float getValue() {
        return (float) this.numerator / this.denominator;
    }

    public Fraction reciprocate() {
        int temp = this.numerator;
        this.numerator = this.denominator;
        this.denominator = temp;
        return this;
    }

    public int quotient() { return this.numerator / this.denominator; }

    @Override
    public int compareTo(Fraction o) {
        return this.subtract(o).getNumerator();
    }

    public static Fraction zero() {
        return new Fraction(0, 1);
    }

    public static Fraction parseFraction(String str) {
        String[] nums = str.split("/");
        return new Fraction(java.lang.Integer.parseInt(nums[0]), java.lang.Integer.parseInt(nums[1]));
    }
}
