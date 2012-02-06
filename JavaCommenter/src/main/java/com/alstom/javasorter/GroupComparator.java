/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alstom.javasorter;

import java.util.Comparator;
import javax.xml.xpath.XPathExpressionException;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author gmanciet
 */
class GroupComparator implements Comparator {

    protected static final  Logger logger = Logger.getLogger(GroupComparator.class.getName());
    private static GroupComparatorByName n = new GroupComparatorByName();
    
    @Override
    public int compare(Object arg0, Object arg1) {

        if (arg0 instanceof Element && arg1 instanceof Element) {
            Element elt0 = (Element) arg0;
            Element elt1 = (Element) arg1;
            
            String elt0NodeName = elt0.getNodeName();
            String elt1NodeName = elt1.getNodeName();
            
            if(elt0NodeName.equals("addData")) return 1;
            if(elt1NodeName.equals("addData")) return -1;
            
            if(elt0NodeName.equals("inputVars") && elt1NodeName.equals("outputVars")) return -1;
            if(elt1NodeName.equals("inputVars") && elt0NodeName.equals("outputVars")) return 1;
            
            String arg0Name = "";
            String arg1Name = "";
            try {
                arg0Name = DOMUtilExt.getValueWithXPath(elt0, "addData/data[@name='Geensys.CB.group']/value");
                arg1Name = DOMUtilExt.getValueWithXPath(elt1, "addData/data[@name='Geensys.CB.group']/value");
            } catch (XPathExpressionException ex) {
                logger.error(ex);
            }
            
            return n.compare(arg0Name, arg1Name);
    
        } else {
            return ((Node) arg0).getNodeName().compareTo(
                    ((Node) arg1).getNodeName());
        }

    }
}