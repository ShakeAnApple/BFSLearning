package graph;

import java.util.ArrayList;
import java.util.List;

public class XmlGraphNode {
    XmlGraph _owner;

    List<XmlGraphLink> _links = new ArrayList<XmlGraphLink>();

    private String _id;
    private String _text;
    private String _background;

    public String getBackground() {
        return _background;
    }

    public String getId() {
        return _id;
    }

    public String getText() {
        return _text;
    }

    public void setBackground(String background) {
        _background = background;
    }

    public void setText(String text) {
        _text = text;
    }

    public XmlGraphNode(XmlGraph owner, String id)
    {
        _owner = owner;

        _id = id;
    }

    public XmlGraphLink[] getConnectionTargets()
    {
        XmlGraphLink[] arr = new XmlGraphLink[_links.size()];
        _links.toArray(arr);
        return arr;
    }

    public XmlGraphLink connectTo(XmlGraphNode target)
    {
        if (target._owner != _owner)
            throw new IllegalArgumentException();

        XmlGraphLink link = new XmlGraphLink(target);
        _links.add(link);
        return link;
    }

    public XmlGraphNode createNext(String id)
    {
        XmlGraphNode node = _owner.createNode(id);
        this.connectTo(node);
        return node;
    }

    public XmlGraphNode createPrev(String id)
    {
        XmlGraphNode node = _owner.createNode(id);
        node.connectTo(this);
        return node;
    }

    public int CompareTo(XmlGraphNode other)
    {
        if (other.getId() == null)
            return 1;

        return this._id.equals(other._id) ? 1 : 0;
    }
}
