package test.java;

import junit.framework.TestCase;

public class ValidateXML extends TestCase {
	
	public void testParser() throws Exception {
		
		main.java.TtsExtractor.parseDocxToXml("DTD0000210338_TTS_NET.docx", "test.xml");
		main.java.XMLValidation.main("test.xml", "Schema_lite.xsd");
	}
}
