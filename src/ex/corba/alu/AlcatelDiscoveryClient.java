package ex.corba.alu;

import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import emsSession.EmsSession_I;
import ex.corba.alu.transform.sax.Corba2XMLHandler;

public class AlcatelDiscoveryClient extends AlcatelConnection {
	public static final Logger LOG = Logger
			.getLogger(AlcatelActivationClient.class);

	protected static Corba2XMLHandler handler;

	public static void main(String args[]) {
		AlcatelDiscoveryClient main = new AlcatelDiscoveryClient();
		EmsSession_I emsSession = null;

		try {
			emsSession = main.openEmsSession(args);

			// main.executeCommands(emsSession);
			main.executeCommandsXmlOutput(emsSession);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			main.closeEmsSession(emsSession);
		}
	}

	public void executeCommands(EmsSession_I emsSession) throws Exception {
		CorbaCommands cmd = new CorbaCommands(emsSession, realEMSName);

		cmd.getAllManagedElementNames();
	}

	public void executeCommandsXmlOutput(EmsSession_I emsSession)
			throws Exception {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream("output.xml"),
				format);

		handler = new Corba2XMLHandler(xmlWriter);

		CorbaCommands cmd = new CorbaCommands(emsSession, realEMSName,
				xmlWriter);

		handler.handlerBuilderStart();
		cmd.getAllManagedElements();
		handler.handlerBuilderEnd();
	}
}