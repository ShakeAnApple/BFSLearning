package graph;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XmlGraph {
    Map<String, XmlGraphNode> _nodes = new HashMap<String, XmlGraphNode>();

//    public XmlGraphNode this[String id] { get { return _nodes[id]; } }
    public XmlGraphNode getNodeById(String id){
        return _nodes.get(id);
    }

    public XmlGraphNode createNode(String id)
    {
        XmlGraphNode node = new XmlGraphNode(this, (id == null ? UUID.randomUUID().toString() : id));
        _nodes.put(node.getId(), node);
        return node;
    }
        /*
            <DirectedGraph xmlns="http://schemas.microsoft.com/vs/2009/dgml"
                          Layout="Sugiyama" GraphDirection="TopToBottom">
             <Nodes>
               <Node  Id="a"  />
               <Node  Id="b" Label="label" />
               <Node  Id="c" />
             </Nodes>
             <Links>
               <Link Source="a" Target="b" />
               <Link Source="a" Target="c" />
               <Link Source="b" Target="c" />
             </Links>
           </DirectedGraph>
           */

    public Document makeXmlDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        //doc.AppendChild(doc.CreateXmlDeclaration("1.0", "utf-8", null));
        Element root = doc.createElement("DirectedGraph");
        doc.appendChild(root);

        Element nodes = doc.createElement("Nodes");
        root.appendChild(nodes);
        Element links = doc.createElement("Links");
        root.appendChild(links);

        int lc = 1;

        for (XmlGraphNode item: _nodes.values())
        {
            Element node = doc.createElement("Node");
            nodes.appendChild(node);

            Attr attrId = doc.createAttribute("Id");
            attrId.setValue(item.getId());
            node.setAttributeNode(attrId);

            if (item.getText() != null && !item.getText().isEmpty()){
                Attr attrLabel = doc.createAttribute("Label");
                attrLabel.setValue(item.getText());
                node.setAttributeNode(attrLabel);
            }

            if (item.getBackground() != null && !item.getBackground().isEmpty()){
                Attr attrBackground = doc.createAttribute("Background");
                attrBackground.setValue(item.getBackground());
                node.setAttributeNode(attrBackground);
            }

            for (XmlGraphLink target: item.getConnectionTargets())
            {
                Element link = doc.createElement("Link");
                links.appendChild(link);

                Attr attrSource = doc.createAttribute("Source");
                attrSource.setValue(item.getId());
                link.setAttributeNode(attrSource);

                Attr attrTarget = doc.createAttribute("Target");
                attrTarget.setValue(target.getTarget().getId());
                link.setAttributeNode(attrTarget);

                Attr attrIndex = doc.createAttribute("Index");
                attrIndex.setValue(Integer.toString(lc++));
                link.setAttributeNode(attrIndex);

                if (target.getText() != null && !target.getText().isEmpty()){
                    Attr attrLabel = doc.createAttribute("Label");
                    attrLabel.setValue(target.getText());
                    link.setAttributeNode(attrLabel);
                }

                if (target.getColor() != null && !target.getColor().isEmpty()){
                    Attr attrStroke = doc.createAttribute("Stroke");
                    attrStroke.setValue(target.getColor());
                    link.setAttributeNode(attrStroke);

                    Attr attrStTh = doc.createAttribute("StrokeThickness");
                    attrStTh.setValue("3");
                    link.setAttributeNode(attrStTh);
                }
            }
        }

        Attr attrSchema = doc.createAttribute("xmlns");
        attrSchema.setValue("http://schemas.microsoft.com/vs/2009/dgml");
        root.setAttributeNode(attrSchema);
        return doc;
    }
}
