package Test.XML;

import MuseScoreWriter.XML.XMLObject;

public class XMLObjectTest {
    public static void main(String[] args) {
        String filename = args[0];
        new XMLObject("Parent")
                .addChild("Child1", "Value1")
                .addChild("Child2", "Value2")
                .compile(filename);
    }
}
