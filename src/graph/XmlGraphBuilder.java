package graph;

import automaton.Automaton;
import automaton.State;
import automaton.Transition;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

public class XmlGraphBuilder {
    public static void saveAsDgmlGraph(Automaton automaton, String fileName) throws ParserConfigurationException, TransformerException {
        XmlGraph graph = toXmlGraph(automaton);
        Document xmlDoc = graph.makeXmlDocument();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(xmlDoc);
        StreamResult result = new StreamResult(new File(fileName));
        transformer.transform(source, result);
        //automaton.ToXmlGraph().MakeXmlDocument().Save(fileName);
    }

    public static XmlGraph toXmlGraph(Automaton automaton)
    {
        XmlGraph xg = new XmlGraph();

        Map<State, String> statesNames = automaton.getStatesNames();

        for (State state: automaton.getStates())
        {
            String name = "";
            String color = "white";

            if (state.isStart()) {
//                name += "Initial" + System.lineSeparator();
                color = "green";
            }

            name += statesNames.get(state);

            XmlGraphNode newNode = xg.createNode(name);
            newNode.setText(name + System.lineSeparator() + state.toString());
            newNode.setBackground(color);
        }

        for (Transition tr: automaton.getAllTransitions())
        {
            xg.getNodeById(statesNames.get(tr.getFrom()))
                    .connectTo(xg.getNodeById(statesNames.get(tr.getTo())))
                    .setText(tr.getSymbol().toString());
        }

        return xg;
    }
}
