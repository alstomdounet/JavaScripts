/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alstom.javasorter;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author gmanciet
 */
public class ControlBuildProcessor {

    protected static Logger logger = Logger.getLogger(ControlBuildProcessor.class.getName());

    public static Document openXmlDocument(String filename) throws Exception {
        File file = new File(filename);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        return doc;
    }

    public static Document orderVariables(Document doc, boolean modifyCoords) throws Exception {
        doc.getDocumentElement().normalize();

        //groupVariablesInNode(doc, "inputVars");
        //groupVariablesInNode(doc, "outputVars");
        //orderVariablesInNode(doc, "inputVars");
        //orderVariablesInNode(doc, "outputVars");
        //orderGroupsText(doc, "Geensys.CB.groupIn");
        //orderGroupsText(doc, "Geensys.CB.groupOut");
        
        if(modifyCoords) {
            //changeNodeCoords(doc, "inVariable", false);
            //changeNodeCoords(doc, "outVariable", true);
        }
        
        //orderGroupsInNode(doc);

        return doc;
    }

    public static boolean orderVariables(String filename, String outfilename, boolean modifyCoords) throws Exception {
        Document doc = openXmlDocument(filename);

        doc = orderVariables(doc, modifyCoords);

        write(doc, outfilename);
        return true;
    }

    public static void write(Document doc, String filename) throws Exception {

        Transformer t = TransformerFactory.newInstance().newTransformer();

        t.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1"); // NOI18N
        t.setOutputProperty(OutputKeys.INDENT, "yes"); // NOI18N
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // NOI18N

        DOMSource source = new DOMSource(doc);
        Result result = new StreamResult(filename);
        t.transform(source, result);

    }

    private static NodeList findNodes(Document doc, String NodeName) {
        NodeList nodeLst = doc.getElementsByTagName(NodeName);
        logger.debug("Found element(s) entitled " + NodeName + " : " + nodeLst.getLength() + " node(s).");

        return nodeLst;
    }

    public static void changeNodeCoords(Document doc, String nodeName, boolean rightMostCoord) throws XPathExpressionException {
        DOMUtilExt domExt = new DOMUtilExt(doc);

        String xPath = "//body/FBD/" + nodeName + "/position";
        
        NodeList matches = domExt.getNodesWithXPath("/project/types/pous/pou[@pouType='program']");
        if(matches.getLength() == 1) {
            logger.info("Component is a MAC");
        }
        else {
            logger.info("Component is not a MAC. Coordinates won't be changed.");
            return;
        }

        List<String> list = new ArrayList();
        list.add("@x");
        list.add("@y");
        List<List> existingGroups = domExt.getValuesWithXPath(xPath, list);

        int selXValue = 9999999;
        if (rightMostCoord) {
            selXValue = 0;
        }

        int minYValue = 999999;
        for (List resultList : existingGroups) {
            int xValue = Integer.parseInt(resultList.get(list.indexOf("@x")).toString());
            int yValue = Integer.parseInt(resultList.get(list.indexOf("@y")).toString());
            if (yValue < minYValue) {
                minYValue = yValue;
                selXValue = xValue;
            } else if ((yValue == minYValue) && ((xValue < selXValue && !rightMostCoord) || (xValue > selXValue && rightMostCoord))) {
                selXValue = xValue;
            }
        }

        List<Node> dataList = DOMUtilExt.convertNodeList(domExt.getNodesWithXPath(xPath));

        String xValueString = String.valueOf(selXValue);
        String yValueString = String.valueOf(minYValue);

        for (Node node : dataList) {
            NamedNodeMap attributes = node.getAttributes();
            Node namedItem = attributes.getNamedItem("x");
            namedItem.setNodeValue(xValueString);
            attributes.setNamedItem(namedItem);

            namedItem = attributes.getNamedItem("y");
            namedItem.setNodeValue(yValueString);
            attributes.setNamedItem(namedItem);
        }
    }

    private static Document groupVariablesInNode(Document doc, String nodeName) throws XPathExpressionException {

        DOMUtilExt domExt = new DOMUtilExt(doc);

        List<String> existingGroups = domExt.getValuesWithXPath("//" + nodeName + "/addData/data[@name='Geensys.CB.group']", "value");
        existingGroups.add("");

        //Collections.sort(existingGroups, new GroupComparatorByName());

        for (String group : existingGroups) {
            logger.info("Processing group \"" + group + "\"");

            String xPath = "//interface/" + nodeName + "/addData";

            String extent = "";

            if (group.equals("")) {
                extent = "[not(data[@name='Geensys.CB.group'])]";
            } else {
                extent = "[data[@name='Geensys.CB.group']/value='" + group + "']";
            }

            NodeList dataList = domExt.getNodesWithXPath(xPath + extent);

            if (dataList.getLength() <= 1) {
                logger.debug("Group \"" + group + "\" doesn't needs to be merged");
                continue;
            }

            Node rootNode = dataList.item(0).getParentNode().getParentNode();

            for (int index = 1; index < dataList.getLength(); index++) {
                Node node = dataList.item(index);

                for (Node varNode : DOMUtilExt.getNodesWithXPath(node.getParentNode(), "variable")) {
                    dataList.item(0).getParentNode().appendChild(varNode);
                }
                Node n = dataList.item(index).getParentNode();
                rootNode.removeChild(n);
            }
        }

        return doc;
    }

    private static Document orderGroupsText(Document doc, String tagName) throws XPathExpressionException {
        String xPath = "//interface/addData/data[@name='" + tagName + "']/value";

        DOMUtilExt domExt = new DOMUtilExt(doc);

        List<Node> nodesWithXPath = DOMUtilExt.convertNodeList(domExt.getNodesWithXPath(xPath));

        for (Node n : nodesWithXPath) {
            String text = n.getTextContent();

            logger.debug("Original order for \"" + tagName + "\" : \"" + text + "\"");

            String[] list = text.split(",,");

            List<String> list2 = new ArrayList();

            for (String item : list) {
                item = item.replace(",", "");
                list2.add(item);
            }
            Collections.sort(list2);

            String finalString = "";
            for (String item : list2) {
                finalString = finalString + item + ",,";
            }

            finalString = finalString.substring(0, finalString.length() - 1);

            n.setTextContent(finalString);
            logger.debug("Final order for \"" + tagName + "\" : \"" + finalString + "\"");
        }

        return doc;
    }

    private static Document orderGroupsInNode(Document doc) throws XPathExpressionException {


        DOMUtilExt domExt = new DOMUtilExt(doc);

        Node node = domExt.getNodeWithXPath("//interface");

        DOMUtilExt.sortChildNodes(node, false, 1, new GroupComparator());

        return doc;
    }

    private static Document orderVariablesInNode(Document doc, String nodename) {
        NodeList nodeLst = findNodes(doc, nodename);

        for (int s = 0; s < nodeLst.getLength(); s++) {

            Node fstNode = nodeLst.item(s);

            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                logger.debug("Found node with " + fstNode.getChildNodes().getLength() + " child(s)");

                DOMUtilExt.sortChildNodes(fstNode, false, 1, new VariableComparator());
            }
        }
        return doc;
    }
}
