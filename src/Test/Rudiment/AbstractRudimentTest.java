package Test.Rudiment;

import Rudiment.AbstractRudimentCreator;

public class AbstractRudimentTest {
    public static void main(String[] args) {
        AbstractRudimentCreator arc = AbstractRudimentCreator.getInstance();

        arc.logRudiment(arc.create("rest"));
        arc.logRudiment(arc.create("singlestroke"));
        arc.logRudiment(arc.create("doublestroke"));
        arc.logRudiment(arc.create("herta"));
        arc.logRudiment(arc.create("paradiddle"));
        arc.logRudiment(arc.create("fivestroke"));
        arc.logRudiment(arc.create("doubleparadiddle"));
    }
}
