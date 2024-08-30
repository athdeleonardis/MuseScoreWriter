package MuseScoreWriter.Util;

import MuseScoreWriter.CustomMath.Fraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// For the purpose of having embedded portions of measures,
// e.g. A measure contains a group, contains a tuplet, contains another tuplet
// Each should be a fraction less than or equal the size of the parent.
public class FractionStack {
    private Stack<Fraction> stack;

    public FractionStack() {
        this.stack = new Stack<>();
    }

    public void push(Fraction fraction) {
        stack.push(new Fraction(fraction));
    }

    public Fraction peek() {
        if (stack.isEmpty())
            return null;
        return stack.peek();
    }

    public int size() {
        return stack.size();
    }

    public void subtract(Fraction fraction) {
        for (Fraction f : stack) {
            f.subtract(fraction).simplify();
        }
    }

    public List<Integer> popAllZero() {
        ArrayList<Integer> popped = new ArrayList<>();
        while (!stack.isEmpty() && stack.peek().isZero()) {
            stack.pop();
            popped.add(stack.size()); // Push the index of the newly popped item
        }
        return popped;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("FractionStack: { ");
        for (Fraction f : stack) {
            str.append(f + ", ");
        }
        str.append("}");
        return str.toString();
    }
}
