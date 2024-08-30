package XML;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class XMLObject {
    private String head;
    private String name;
    private ArrayList<String> tagNames = new ArrayList();
    private ArrayList<String> tagValues = new ArrayList();
    private boolean doRecompileHead = true;
    private int customIndent = 1;

    private String body;
    private ArrayList<XMLObject> children = new ArrayList();
    private ArrayList<XMLObject> parents = new ArrayList();

    private String tail;

    private String text;
    private boolean isInline;

    //
    // Constructors
    //

    public XMLObject(String name, String body) {
        this.name = name;
        this.body = body;
        this.tail = "</" + name + ">\n";
        compileHead();
        this.text = this.head + this.body + this.tail;
        this.isInline = true;
    }

    public XMLObject(String head, String tail, int customIndent) {
        this.head = head;
        this.tail = tail;
        this.customIndent = customIndent;
    }

    public XMLObject(String name) {
        this.name = name;
        this.tail = "</" + name + ">\n";
    }

    //
    // Add information
    //

    public XMLObject addTag(String name, String value) {
        tagNames.add(name);
        tagValues.add(value);
        this.doRecompileHead = true;
        return this;
    }

    public XMLObject addChild(XMLObject child) {
        children.add(child);
        child.addParent(this);
        return this;
    }

    public XMLObject addChild(String name, String value) {
        XMLObject newObj = new XMLObject(name, value);
        children.add(newObj);
        newObj.addParent(this);
        return this;
    }

    private void addParent(XMLObject parent) {
        this.parents.add(parent);
    }

    private void compileHead() {
        if (!doRecompileHead)
            return;

        StringBuilder strBldr = new StringBuilder();
        strBldr
                .append('<')
                .append(this.name);
        for (int i = 0; i < tagNames.size(); i++) {
            strBldr
                    .append(" ")
                    .append(tagNames.get(i));
            String value = tagValues.get(i);
            if (value.equals(""))
                continue;
            strBldr
                    .append("=\"")
                    .append(value)
                    .append('"');
        }
        strBldr.append('>');
        doRecompileHead = false;
        this.head = strBldr.toString();
        if (isInline)
            this.text = this.head + this.body + this.tail;
    }

    public void build(StringBuilder strBldr, int indentation, String indentationString) {
        compileHead();

        addIndentation(strBldr, indentation, indentationString);
        if (isInline) {
            strBldr.append(text);
            return;
        }

        strBldr.append(head);
        if (children.size() > 0) {
            strBldr.append('\n'); // Newline after head
            for (XMLObject child : children) {
                child.build(strBldr, indentation+customIndent, indentationString);
            }
            addIndentation(strBldr, indentation+1, indentationString); // Indentation before tail
        }
        else if (isInline)
            strBldr.append(body);
        strBldr.append(tail);
    }

    public void addIndentation(StringBuilder strBldr, int indentation, String indentationString) {
        for (int i = 0; i < indentation; i++)
            strBldr.append(indentationString);
    }

    public void compile(String filename) {
        try {
            FileWriter writer = new FileWriter(filename, false);
            writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            StringBuilder xmlStringBuilder = new StringBuilder();
            build(xmlStringBuilder, 0, "  ");
            writer.write(xmlStringBuilder.toString());
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
