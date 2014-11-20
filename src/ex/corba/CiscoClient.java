package ex.corba;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import nmsSession.NmsSession_I;
import nmsSession.NmsSession_IHelper;
import nmsSession.NmsSession_IPOATie;

import org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE;
import org.omg.BiDirPolicy.BOTH;
import org.omg.BiDirPolicy.BidirectionalPolicyValueHelper;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import emsSession.EmsSession_I;
import emsSession.EmsSession_IHolder;
import emsSessionFactory.EmsSessionFactory_I;
import emsSessionFactory.EmsSessionFactory_IHelper;

public class CiscoClient {
	private String corbaConnect;
	private String login;
	private String pass;
	private String emsName;
	private ORB orb;
	private POA rootPOA = null;
	private NmsSession_I nmsSession = null;
	private EmsSession_I emsSession = null;

	public void openEmsSession(String args[]) throws Exception {
		Properties props = getConnectionParams();

		// create and initialize the ORB
		orb = ORB.init(args, props);
		System.out.println("ORB.init called.");

		// Get the root naming context
		NamingContextExt rootContext = NamingContextExtHelper.narrow(orb
				.resolve_initial_references("NameService"));
		System.out.println("NameService found.");

		// Resolve the reference to EMSSessionFactory
		NameComponent[] name = new NameComponent[6];
		name[0] = new NameComponent("TMF_MTNM", "Class");
		name[1] = new NameComponent("Cisco Systems", "Vendor");
		name[2] = new NameComponent("Cisco Prime Optical", "EMSInstance");
		name[3] = new NameComponent("9_8", "Version");
		// where version = "9_8" for Prime Optical 9.8
		name[4] = new NameComponent("PrimeOptical", "EMS");
		// ctm_sys_id = "PrimeOptical"
		name[5] = new NameComponent("SessionFactory", "EmsSessionFactory");

		org.omg.CORBA.Object ems = rootContext.resolve(name);

		System.out.println("ems: " + ems);

		EmsSessionFactory_I sessionFactory = EmsSessionFactory_IHelper
				.narrow(ems);
		System.out.println("\nEmsSessionFactory: " + sessionFactory);

		// Create NMS Session
		nmsSession = createNmsSession();

		// Create EMS Session
		EmsSession_IHolder sessionHolder = new EmsSession_IHolder();
		sessionFactory.getEmsSession(login, pass, nmsSession, sessionHolder);
		emsSession = sessionHolder.value;
		System.out.println("emsSession: " + emsSession);
	}

	public NmsSession_I createNmsSession() throws Exception {
		// Create policy
		Any any = orb.create_any();
		BidirectionalPolicyValueHelper.insert(any, BOTH.value);
		Policy biDirPolicy = orb.create_policy(BIDIRECTIONAL_POLICY_TYPE.value,
				any);
		Policy[] policy = new Policy[] { biDirPolicy };
		System.out.println("policy: " + policy);

		// Activate policy
		POA rootPOA = POAHelper.narrow(orb
				.resolve_initial_references("RootPOA"));
		rootPOA.the_POAManager().activate();

		// Create NMS Session
		NmsSessionImpl nms = new NmsSessionImpl();
		NmsSession_IPOATie tieobj = new NmsSession_IPOATie(nms, rootPOA);
		rootPOA.activate_object(tieobj);
		nmsSession = NmsSession_IHelper.narrow(tieobj._this());

		return nmsSession;
	}

	public void closeEmsSession() {
		if (emsSession != null) {
			emsSession.endSession();
		}

		if (rootPOA != null) {
			rootPOA.destroy(true, true);
		}

		orb.shutdown(true);
		orb.destroy();

	}

	public Properties getConnectionParams() throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(new File("corba.properties")));

		corbaConnect = "corbaloc:iiop:" + props.getProperty("host") + ":"
				+ props.getProperty("port") + "/NameService";

		login = props.getProperty("login");
		pass = props.getProperty("password");
		emsName = props.getProperty("EMSname");

		props.setProperty("jacorb.net.socket_factory",
				"org.jacorb.orb.factory.DefaultSocketFactory");
		props.setProperty("jacorb.net.server_socket_factory",
				"org.jacorb.orb.factory.DefaultServerSocketFactory");

		props.setProperty("jacorb.log.default.verbosity", "4");
		props.setProperty("jacorb.logfile", "log/jacorb.log");

		props.setProperty(
				"org.omg.PortableInterceptor.ORBInitializerClass.bidir_init",
				"org.jacorb.orb.giop.BiDirConnectionInitializer");
		props.setProperty("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
		props.setProperty("org.omg.CORBA.ORBSingletonClass",
				"org.jacorb.orb.ORBSingleton");

		props.setProperty("ORBInitRef.NameService", corbaConnect);

		props.setProperty("jacorb.connection.client.pending_reply_timeout",
				"600000");
		props.setProperty(
				"jacorb.connection.client.timeout_ignores_pending_messages",
				"on");

		return props;
	}

	public static void main(String args[]) {
		CiscoClient main = new CiscoClient();

		try {
			main.openEmsSession(args);

			CorbaCommands cmd = new CorbaCommands(main.emsSession, main.emsName);

			cmd.getAllManagedElementNames();
			cmd.getAllEquipment();
			cmd.getSNC();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			main.closeEmsSession();
		}
	}
}