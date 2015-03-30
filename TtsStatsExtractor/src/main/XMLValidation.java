package main;

import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.xml.sax.SAXException;

public class XMLValidation {

	public static void main(String XmlFileName, String XsdFileName) {
		try {
			Source xsdFile = new StreamSource(new File(XsdFileName));
			Source xmlFile = new StreamSource(new File(XmlFileName));
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			validator.validate(xmlFile);
			System.out.println(xmlFile.getSystemId() + " is valid");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			System.out.println("Invalid block: " + e.getLocalizedMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
