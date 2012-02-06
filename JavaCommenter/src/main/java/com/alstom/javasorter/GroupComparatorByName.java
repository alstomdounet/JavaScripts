/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alstom.javasorter;

import java.util.Comparator;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 *
 * @author gmanciet
 */
class GroupComparatorByName implements Comparator {

    protected static final Logger logger = Logger.getLogger(GroupComparatorByName.class.getName());

    @Override
    public int compare(Object arg0, Object arg1) {

        if (arg0 instanceof String && arg1 instanceof String) {


            String arg0Name = (String) arg0;
            String arg1Name = (String) arg1;

            if (arg0Name.equals("")) {
                return 1;
            }
            if (arg1Name.equals("")) {
                return -1;
            }

            logger.debug(arg0Name.indexOf(arg1Name));
            if (arg0Name.indexOf(arg1Name+".") == 0 && arg0Name.length() > arg1Name.length()) {
                return -1;
            }
            
            if (arg1Name.indexOf(arg0Name+".") == 0 && arg1Name.length() > arg0Name.length()) {
                return 1;
            }

            int result = arg0Name.compareTo(arg1Name);

            if (result < 0) {
                logger.debug(arg0Name + " is lower than " + arg1Name);
            }
            if (result == 0) {
                logger.debug(arg0Name + " is equal to " + arg1Name);
            }
            if (result > 0) {
                logger.debug(arg0Name + " is higher than " + arg1Name);
            }

            return result;
        } else {
            return ((Node) arg0).getNodeName().compareTo(
                    ((Node) arg1).getNodeName());
        }

    }
}