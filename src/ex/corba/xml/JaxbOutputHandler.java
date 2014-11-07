package ex.corba.xml;

import java.io.File;
import java.io.FileWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.ContentHandler;

public class JaxbOutputHandler {
	private File fileName;
	private XMLStreamWriter streamWriter;
	private ContentHandler contentHandler;

	public JaxbOutputHandler(String file) {
		if (file != null) {
			fileName = new File(file);
		} else {
			fileName = new File("output.xml");
		}
	}

	public JaxbOutputHandler(ContentHandler aContentHandler) {
		contentHandler = aContentHandler;
	}

	public JaxbOutputHandler() {
		try {
			XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
			streamWriter = outFactory.createXMLStreamWriter(new FileWriter(
					"test.xml"));
		} catch (Exception ex) {

		}
	}

	public void printManagedElement(ManagedElement me) throws Exception {
		if (me == null)
			return;

		// create JAXB context and initializing Marshaller
		JAXBContext jaxbContext = JAXBContext.newInstance(ManagedElement.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// for getting nice formatted output
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
				Boolean.TRUE);

		// Writing to console
		// jaxbMarshaller.marshal(me, System.out);

		// Writing to XML file
		jaxbMarshaller.marshal(me, fileName);
	}

	public void printManagedElementStream(ManagedElement me) throws Exception {
		if (me == null)
			return;

		// create JAXB context and initializing Marshaller
		JAXBContext jaxbContext = JAXBContext.newInstance(ManagedElement.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// for getting nice formatted output
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
				Boolean.TRUE);

		// Writing to XML file
		jaxbMarshaller.marshal(me, streamWriter);
	}

	public void printManagedElementContentHandler(ManagedElement me)
			throws Exception {
		if (me == null)
			return;

		// create JAXB context and initializing Marshaller
		JAXBContext jaxbContext = JAXBContext.newInstance(ManagedElement.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// for getting nice formatted output
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
				Boolean.TRUE);

		// Writing to XML file
		jaxbMarshaller.marshal(me, contentHandler);
	}

	public void print(NmsObjects nmsObjects) throws Exception {
		// create JAXB context and initializing Marshaller
		JAXBContext jaxbContext = JAXBContext.newInstance(NmsObjects.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// for getting nice formatted output
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
				Boolean.TRUE);

		// Writing to console
		// jaxbMarshaller.marshal(nmsObjects, System.out);

		// Writing to XML file
		jaxbMarshaller.marshal(nmsObjects, fileName);
	}
}
