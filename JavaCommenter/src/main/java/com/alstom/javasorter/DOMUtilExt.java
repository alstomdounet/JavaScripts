package com.alstom.javasorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sun.org.apache.xerces.internal.util.DOMUtil;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

public class DOMUtilExt extends DOMUtil {

    protected static Logger logger = Logger.getLogger(DOMUtilExt.class.getName());
    private Document doc;

    /**
     * Sorts the children of the given node upto the specified depth if
     * available
     * 
     * @param node -
     *            node whose children will be sorted
     * @param descending -
     *            true for sorting in descending order
     * @param depth -
     *            depth upto which to sort in DOM
     * @param comparator -
     *           comparator used to sort, if null a default NodeName
     *           comparator is used.
     */
    public DOMUtilExt(Document doc) {
        this.doc = doc;
    }

    public NodeList getNodesWithXPath(String xPathQuery) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(xPathQuery);
        logger.debug("Executing following XPATH query :" + xPathQuery);

        NodeList elem1List = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        logger.debug("Result(s) of query : " + elem1List.getLength());
        return elem1List;
    }

    public Node getNodeWithXPath(String xPathQuery) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(xPathQuery);
        logger.debug("Executing following XPATH query :" + xPathQuery);

        Node elem = (Node) expr.evaluate(doc, XPathConstants.NODE);
        if (elem != null) {
            logger.debug("Result found.");
        } else {
            logger.warn("No result found.");
        }
        return elem;
    }

    public static List<Node> convertNodeList(NodeList nodeList) {
        List<Node> list = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            list.add(nodeList.item(i));
        }
        return list;
    }

    public static List<Node> getNodesWithXPath(Node node, String xPathQuery) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(xPathQuery);
        logger.debug("Executing following XPATH query :" + xPathQuery);

        NodeList elem = (NodeList) expr.evaluate(node, XPathConstants.NODESET);
        List<Node> list = convertNodeList(elem);
        logger.debug("Result(s) of query : " + list.size());
        return list;
    }

    public static List<Node> getNodesWithXPath(NodeList nodeList, String xPathQuery) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(xPathQuery);
        logger.debug("Executing following XPATH query :" + xPathQuery);

        List<Node> list = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList result = (NodeList) expr.evaluate(nodeList.item(i), XPathConstants.NODESET);

            for (int j = 0; j < result.getLength(); j++) {
                list.add(result.item(j));
            }


        }
        logger.debug("Result(s) of query : " + list.size());
        return list;
    }
    
    public static String getValueWithXPath(Node n, String xPathQuery) throws XPathExpressionException {

        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(xPathQuery);

        String group = (String) expr.evaluate(n, XPathConstants.STRING);
        return group;
    }

    public List<String> getValuesWithXPath(String xPathQuery, String additionnalxPath) throws XPathExpressionException {

        NodeList nodeList = getNodesWithXPath(xPathQuery);

        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(additionnalxPath);

        List list = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            String group = (String) expr.evaluate(nodeList.item(i), XPathConstants.STRING);

            list.add(group);
            logger.debug("Found match : " + group);
        }
        return list;
    }
    
    public List<List> getValuesWithXPath(String xPathQuery, List<String> additionnalxPathList) throws XPathExpressionException {

        NodeList nodeList = getNodesWithXPath(xPathQuery);

        XPath xpath = XPathFactory.newInstance().newXPath();
        
        List<XPathExpression> exprList = new ArrayList();
        for(String additionnalxPath : additionnalxPathList) {
            exprList.add(xpath.compile(additionnalxPath));
        }

        List list = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            
            List<String> resultList = new ArrayList();
            for(XPathExpression expr : exprList) {
                String result = (String) expr.evaluate(nodeList.item(i), XPathConstants.STRING);
                resultList.add(result);
            }

            list.add(resultList);
            logger.debug("Found group : " + resultList);
        }
        return list;
    }

    public static void sortChildNodes(Node node, boolean descending,
            int depth, Comparator comparator) {

        List nodes = new ArrayList();
        NodeList childNodeList = node.getChildNodes();
        if (depth > 0 && childNodeList.getLength() > 0) {
            for (int i = 0; i < childNodeList.getLength(); i++) {
                Node tNode = childNodeList.item(i);
                sortChildNodes(tNode, descending, depth - 1,
                        comparator);
                // Remove empty text nodes
                if ((!(tNode instanceof Text))
                        || (tNode instanceof Text && ((Text) tNode).getTextContent().trim().length() > 1)) {
                    nodes.add(tNode);
                }
            }
            Comparator comp = (comparator != null) ? comparator
                    : new DefaultNodeNameComparator();
            if (descending) {
                //if descending is true, get the reverse ordered comparator
                Collections.sort(nodes, Collections.reverseOrder(comp));
            } else {
                Collections.sort(nodes, comp);
            }

            for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                Node element = (Node) iter.next();
                node.appendChild(element);
            }
        }

    }
}

class DefaultNodeNameComparator implements Comparator {

    @Override
    public int compare(Object arg0, Object arg1) {
        return ((Node) arg0).getNodeName().compareTo(
                ((Node) arg1).getNodeName());
    }
}