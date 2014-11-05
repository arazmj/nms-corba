package ex.corba.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class JaxbOutputHandler {
	private File fileName;

	public JaxbOutputHandler(String file) {
		// specify the location and name of xml file to be created
		if (file != null) {
			fileName = new File(file);
		} else {
			fileName = new File("output.xml");
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
