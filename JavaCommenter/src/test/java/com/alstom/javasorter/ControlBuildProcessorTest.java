/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alstom.javasorter;

import java.io.File;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author gmanciet
 */
public class ControlBuildProcessorTest extends TestCase {

    protected static Logger logger = Logger.getLogger(ControlBuildProcessorTest.class.getName());

    public ControlBuildProcessorTest(String testName) {
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
     * Test of orderVariables method, of class ControlBuildProcessor.
     */
    public void testOrderVariables() throws Exception {
        System.out.println("orderVariables");
        String filename = "src/test/resources/test_order.xml";

        File tempDir = CreateTempDirectory.createTempDirectory();
        logger.debug("Chemin temporaire : " + tempDir);

        String refFileName = tempDir.getPath() + File.separator + "test_order_output.ref.xml";
        String outFileName = tempDir.getPath() + File.separator + "test_order_output.xml";

        Document doc = ControlBuildProcessor.openXmlDocument(filename);

        ControlBuildProcessor.write(doc, refFileName);
        String refMd5 = Md5Generator.computeMD5(refFileName);


        doc = ControlBuildProcessor.orderVariables(doc, false);
        ControlBuildProcessor.write(doc, outFileName);
        String md5 = Md5Generator.computeMD5(outFileName);

        assertEquals("File are not modified : ", false, refMd5.compareTo(md5) == 0);

        logger.error("Test case is not finished!!!");

    }
    
    /**
     * Test of changeNodePositions method, of class ControlBuildProcessor.
     */
    public void testChangeNodePositions() throws Exception {
        System.out.println("orderVariables");
        String filename = "src/test/resources/test_order.xml";

        File tempDir = CreateTempDirectory.createTempDirectory();
        logger.debug("Chemin temporaire : " + tempDir);

        String refFileName = tempDir.getPath() + File.separator + "test_order_output.ref.xml";
        String outFileName = tempDir.getPath() + File.separator + "test_order_output.xml";

        Document doc = ControlBuildProcessor.openXmlDocument(filename);

        ControlBuildProcessor.write(doc, refFileName);
        String refMd5 = Md5Generator.computeMD5(refFileName);

        doc = ControlBuildProcessor.orderVariables(doc, true);
        
        ControlBuildProcessor.write(doc, outFileName);
        String md5 = Md5Generator.computeMD5(outFileName);

        assertEquals("File are not modified : ", false, refMd5.compareTo(md5) == 0);

        logger.error("Test case is not finished!!!");

    }
}
