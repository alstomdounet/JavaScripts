/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alstom.javasorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author gmanciet
 */
public class GroupComparatorByNameTest extends TestCase {

    public GroupComparatorByNameTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of compare method, of class GroupComparatorByName.
     */
    public void testCompare() {
        System.out.println("compare");

        List<String> list = new ArrayList();
        list.add("ListZ");
        list.add("ListA");
        list.add("List1");
        list.add("");
        list.add("L_Toto.subgroup");
        list.add("ListB");
        list.add("L_Toto");
        list.add("ListA.tptp");
        list.add("L_Toto.subgroup.toto");

        Collections.sort(list, new GroupComparatorByName());

        List<String> sortedList = new ArrayList();
        sortedList.add("L_Toto.subgroup.toto");
        sortedList.add("L_Toto.subgroup");
        sortedList.add("L_Toto");
        sortedList.add("List1");
        sortedList.add("ListA.tptp");
        sortedList.add("ListA");
        sortedList.add("ListB");
        sortedList.add("ListZ");
        sortedList.add("");

        assertEquals(sortedList, list);
    }
}
