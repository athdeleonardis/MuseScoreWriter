package MuseScoreWriter.TimeLine;

import MuseScoreWriter.CustomMath.Fraction;

import java.util.*;

public class TimeLine<T> implements Iterable<Map.Entry<Fraction,T>> {
    private final String name;
    private final TreeMap<Fraction,T> timeline;
    private Fraction duration;

    public TimeLine(String name) {
        this.name = name;
        timeline = new TreeMap<>();
        duration = Fraction.zero();
    }

    public String getName() { return name; }
    public Fraction getDuration() { return duration; }
    public void setDuration(Fraction duration) { this.duration = new Fraction(duration); }

    public T getEntry(Fraction time) {
        time = new Fraction(time).simplify();
        if (timeline.containsKey(time))
            return timeline.get(time);
        return null;
    }

    public void insert(Fraction time, T data) {
        time = new Fraction(time).simplify();

        if (data == null && timeline.containsKey(time)) {
            timeline.remove(time);
            return;
        }
        if (data == null)
            return;

        timeline.put(time, data);
    }

    public Fraction[] getTimes() {
        Set<Fraction> keySet = timeline.keySet();
        return keySet.toArray(new Fraction[keySet.size()]);
    }

    public Iterator<Map.Entry<Fraction,T>> iterator() { return timeline.entrySet().iterator(); }

    public String toString() {
        StringBuilder str = new StringBuilder("TimeLine{name=");
        str.append(name);
        str.append(", duration=");
        str.append(duration);
        str.append(", entries=[");
        boolean addComma = false;
        for (Map.Entry<Fraction,T> entry : this) {
            if (addComma) {
                str.append(", ");
            }
            addComma = true;
            str.append("{");
            str.append(entry.getKey());
            str.append(",");
            str.append(entry.getValue().toString());
            str.append("}");
        }
        str.append("]}");
        return str.toString();
    }
}
