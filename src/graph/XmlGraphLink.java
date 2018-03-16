package graph;

public class XmlGraphLink {
    private XmlGraphNode _target;
    private String _text;
    private String _color;

    public XmlGraphNode getTarget() {
        return _target;
    }

    public String getColor() {
        return _color;
    }

    public String getText() {
        return _text;
    }

    public void setColor(String color) {
        _color = color;
    }

    public void setText(String text) {
        _text = text;
    }

    public XmlGraphLink(XmlGraphNode target)
    {
        _target = target;
    }

//    int IComparable<XmlGraphLink>.CompareTo(XmlGraphLink other)
//    {
//        return this.Target.CompareTo(other.Target);
//    }
}
