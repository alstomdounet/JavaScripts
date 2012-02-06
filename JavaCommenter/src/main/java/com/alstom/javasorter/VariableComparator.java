package com.alstom.javasorter;

import java.util.Comparator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class VariableComparator implements Comparator {

    public int compare(Object arg0, Object arg1) {

        if (arg0 instanceof Element && arg1 instanceof Element) {
            Element elt0 = (Element) arg0;
            Element elt1 = (Element) arg1;
            
            if(elt0.getNodeName().equals("addData")) return 1;
            if(elt1.getNodeName().equals("addData")) return -1;
            
            String arg0Name = elt0.getAttribute("name");
            String arg1Name = elt1.getAttribute("name");

            int result = arg0Name.compareTo(arg1Name);
            
            return result;
        } else {
            return ((Node) arg0).getNodeName().compareTo(
                    ((Node) arg1).getNodeName());
        }

    }
}