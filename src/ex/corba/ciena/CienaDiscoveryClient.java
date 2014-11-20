package ex.corba.ciena;

import java.io.FileOutputStream;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netcracker.ciena.oncenter.v11.emsSession.EmsSession_I;
import com.netcracker.ciena.oncenter.v11.globaldefs.ProcessingFailureException;

import ex.corba.ciena.error.CorbaErrorProcessor;
import ex.corba.ciena.transform.sax.Corba2XMLHandler;

public class CienaDiscoveryClient extends CienaConnection {
	public static final Logger LOG = LoggerFactory
			.getLogger(CienaDiscoveryClient.class);

	protected static Corba2XMLHandler handler;

	public static void main(String args[]) {
		CienaDiscoveryClient main = new CienaDiscoveryClient();
		EmsSession_I emsSession = null;

		try {
			emsSession = main.openEmsSession(args);

			// main.executeCommands(emsSession);
			main.executeCommandsXmlOutput(emsSession);
		} catch (ProcessingFailureException prf) {
			LOG.error("Alcatel OMS 1350: "
					+ CorbaErrorProcessor.printError(prf));
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
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

		if (props.getProperty("getAllManagedElements") != null
				&& props.getProperty("getAllManagedElements").equalsIgnoreCase(
						"yes")) {
			cmd.getAllManagedElements();
		}

		if (props.getProperty("getAllEquipment") != null
				&& props.getProperty("getAllEquipment").equalsIgnoreCase("yes")) {
			cmd.getAllEquipment();
		}

		handler.handlerBuilderEnd();
	}
}