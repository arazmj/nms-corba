package ex.corba.alu;

import java.io.FileOutputStream;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import emsSession.EmsSession_I;
import ex.corba.alu.error.CorbaErrorProcessor;
import ex.corba.alu.transform.sax.Corba2XMLHandler;
import globaldefs.ProcessingFailureException;

public class AlcatelDiscoveryClient extends AlcatelConnection {
	public static final Logger LOG = LoggerFactory
			.getLogger(AlcatelDiscoveryClient.class);

	protected static Corba2XMLHandler handler;

	public static void main(String args[]) {
		AlcatelDiscoveryClient main = new AlcatelDiscoveryClient();
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

		if (props.getProperty("getAllPTPs") != null
				&& props.getProperty("getAllPTPs").equalsIgnoreCase("yes")) {
			cmd.getAllPTPs();
		}

		if (props.getProperty("getAllTopologicalLinks") != null
				&& props.getProperty("getAllTopologicalLinks")
						.equalsIgnoreCase("yes")) {
			cmd.getAllTopologicalLinks();
		}

		if (props.getProperty("getAllSubnetworkConnections") != null
				&& props.getProperty("getAllSubnetworkConnections")
						.equalsIgnoreCase("yes")) {
			cmd.getAllSubnetworkConnections();
		}

		if (props.getProperty("getRoute") != null
				&& props.getProperty("getRoute").equalsIgnoreCase("yes")) {
			cmd.getRoute();
		}

		if (props.getProperty("getAllProtectionGroups") != null
				&& props.getProperty("getAllProtectionGroups")
						.equalsIgnoreCase("yes")) {
			cmd.getAllProtectionGroups();
		}

		if (props.getProperty("getAllFDFrs") != null
				&& props.getProperty("getAllFDFrs").equalsIgnoreCase("yes")) {
			cmd.getAllFDFrs();
		}

		if (props.getProperty("getTopologicalLinksOfFDFr") != null
				&& props.getProperty("getTopologicalLinksOfFDFr")
				.equalsIgnoreCase("yes")) {
			cmd.getTopologicalLinksOfFDFr();
		}
		
		if (props.getProperty("getContainedPotentialTPs") != null
				&& props.getProperty("getContainedPotentialTPs")
						.equalsIgnoreCase("yes")) {
			cmd.getContainedPotentialTPs();
		}

		handler.handlerBuilderEnd();
	}
}