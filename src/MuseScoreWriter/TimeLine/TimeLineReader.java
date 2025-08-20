package MuseScoreWriter.TimeLine;

import MuseScoreWriter.CustomMath.Fraction;

import java.util.Iterator;
import java.util.Map;

public class TimeLineReader<T> {
    public class TimeLineData {
        public Fraction position;
        public Fraction duration;
        public T entry;

        public TimeLineData(Fraction position, Fraction duration, T entry) {
            this.position = position;
            this.duration = duration;
            this.entry = entry;
        }

        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append("{");
            str.append(position);
            str.append(", ");
            str.append(duration);
            str.append(", ");
            str.append(entry);
            str.append("}");
            return str.toString();
        }
    }

    private TimeLine<T> timeLine;
    private Iterator<Map.Entry<Fraction,T>> timeLineIterator;
    private Fraction totalReadTime;
    private Fraction readDurationRemaining;
    private Map.Entry<Fraction,T> nextEntry;

    public TimeLineReader() {
        totalReadTime = new Fraction(0,0);
        readDurationRemaining = new Fraction(0,0);
    }

    public void setTimeLine(TimeLine<T> timeLine) {
        this.timeLine = timeLine;
        this.timeLineIterator = timeLine.iterator();
        getNextEntry();
        this.readDurationRemaining = new Fraction(timeLine.getDuration());
        this.totalReadTime = new Fraction(0,1);
    }

    public boolean isFinished() {
        return readDurationRemaining.isZero();
    }

    public TimeLineData read(Fraction maxDuration) {
        T entry = (nextEntry != null && totalReadTime.equals(nextEntry.getKey())) ? nextEntry.getValue() : null;
        if (entry != null)
            getNextEntry();

        Fraction position = new Fraction(totalReadTime);
        Fraction duration = new Fraction(Fraction.min(maxDuration, readDurationRemaining)).simplify();
        if (nextEntry != null) {
            duration = Fraction.min(duration, new Fraction(nextEntry.getKey()).subtract(position).simplify());
        }

        totalReadTime.add(duration).simplify();
        readDurationRemaining.subtract(duration).simplify();

        return new TimeLineData(position, duration, entry);
    }

    private void getNextEntry() {
        if (timeLineIterator.hasNext()) {
            this.nextEntry = timeLineIterator.next();
            return;
        }
        this.nextEntry = null;
    }
}
