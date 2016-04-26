package ex.corba.huawei;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.u2000.emsSession.EmsSession_I;
import com.huawei.u2000.globaldefs.ProcessingFailureException;

import ex.corba.huawei.error.CorbaErrorProcessor;
import ex.corba.huawei.transform.sax.Corba2XMLHandler;

public class HuaweiDiscoveryClient extends HuaweiConnection {
	public static final Logger LOG = LoggerFactory.getLogger(HuaweiDiscoveryClient.class);

	protected static Corba2XMLHandler handler;

	public static void main(String args[]) {
		HuaweiDiscoveryClient main = new HuaweiDiscoveryClient();
		EmsSession_I emsSession = null;

		try {
			emsSession = main.openEmsSession(args);

			// main.executeCommands(emsSession);
			main.executeCommandsXmlOutput(emsSession);
		} catch (ProcessingFailureException prf) {
			LOG.error("Huawei U2000: " + CorbaErrorProcessor.printError(prf));
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

	public void executeCommandsXmlOutput(EmsSession_I emsSession) throws Exception {
		OutputFormat format = OutputFormat.createPrettyPrint();
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream("huawei-" + sdf.format(currentDate) + ".xml"), format);

		handler = new Corba2XMLHandler(xmlWriter);

		CorbaCommands cmd = new CorbaCommands(emsSession, realEMSName, xmlWriter);

		handler.handlerBuilderStart();

		if (props.getProperty("getAllManagedElements") != null
				&& props.getProperty("getAllManagedElements").equalsIgnoreCase("yes")) {
			cmd.getAllManagedElements();
		}

		if (props.getProperty("getAllEquipment") != null
				&& props.getProperty("getAllEquipment").equalsIgnoreCase("yes")) {
			cmd.getAllEquipment();
		}

		if (props.getProperty("getAllPTPs") != null && props.getProperty("getAllPTPs").equalsIgnoreCase("yes")) {
			cmd.getAllPTPs();
		}

		if (props.getProperty("getAllTopologicalLinks") != null
				&& props.getProperty("getAllTopologicalLinks").equalsIgnoreCase("yes")) {
			cmd.getAllTopologicalLinks();
		}

		if (props.getProperty("getAllSubnetworkConnections") != null
				&& props.getProperty("getAllSubnetworkConnections").equalsIgnoreCase("yes")) {
			cmd.getAllSubnetworkConnections();
		}

		if (props.getProperty("getRoute") != null && props.getProperty("getRoute").equalsIgnoreCase("yes")) {
			cmd.getRoute();
		}

		if (props.getProperty("getAllProtectionGroups") != null
				&& props.getProperty("getAllProtectionGroups").equalsIgnoreCase("yes")) {
			cmd.getAllProtectionGroups();
		}

		if (props.getProperty("getAllEthService") != null
				&& props.getProperty("getAllEthService").equalsIgnoreCase("yes")) {
			cmd.getAllEthService();
		}

		if (props.getProperty("getAllMstpEndPoints") != null
				&& props.getProperty("getAllMstpEndPoints").equalsIgnoreCase("yes")) {
			cmd.getAllMstpEndPoints();
		}

		if (props.getProperty("getBindingPath") != null
				&& props.getProperty("getBindingPath").equalsIgnoreCase("yes")) {
			cmd.getBindingPath();
		}

		handler.handlerBuilderEnd();
	}
}
