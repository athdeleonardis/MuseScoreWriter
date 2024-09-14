package MuseScoreWriter.MuseScore.Document;

import MuseScoreWriter.XML.XMLObject;

public class MuseScoreDocument {
    private XMLObject document;
    private XMLObject staff;

    public MuseScoreDocument(XMLObject document, XMLObject staff) {
        this.document = document;
        this.staff = staff;
    }

    public XMLObject getDocumentXML() { return document; }
    public XMLObject getStaffXML() { return staff; }
}
