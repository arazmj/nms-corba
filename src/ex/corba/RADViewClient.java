// HelloClient.java
package ex.corba;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import ems.ner.NerEntryEx;
import ems.ner.NerService;
import ems.ner.NerServiceHelper;
import ems.sys.Session;
import ems.sys.SessionFactory;
import ems.sys.SessionFactoryHelper;

public class RADViewClient {
	private String corbaConnect;
	private String host;
	private String login;
	private String pass;

	public void openEmsSession(String args[]) throws Exception {
		Properties props = getConnectionParams();

		// create and initialize the ORB
		ORB orb = ORB.init(args, props);
		System.out.println("ORB.init called.");

		// Option 1: Naming context using RIR
		// NamingContextExt rootContext = NamingContextExtHelper.narrow(orb
		// .resolve_initial_references("NameService"));

		// Option 2: Naming context using corbaloc URL
		NamingContextExt rootContext = NamingContextExtHelper.narrow(orb
				.string_to_object(corbaConnect));
		System.out.println("rootContext: " + rootContext);

		// Obtaining reference to SessionFactory from Naming Service
		String name = "MNG164/EMS/RAD-DACS-CC/EMS_NER_SESSION.FACTORY";
		System.out.println("name: " + name);

		SessionFactory sessionFactory = SessionFactoryHelper.narrow(rootContext
				.resolve_str(name));

		System.out.println("sessionFactory: " + sessionFactory);

		// Creation Session
		Session session = sessionFactory.createSession();
		System.out.println("session: " + session);
       
		// Obtaining service from Session
		NerService nerService = NerServiceHelper.narrow(session
				.getService("NerService"));
		System.out.println("nerService: " + nerService);

		// Call API
		// String version = nerService.getVersion();
		// System.out.println("version: " + version);

		// get all entries, start with - "/" (root)
		// get all derived attributes from parent NER entries - true
		// get all atributes specific entry - true
		// filter of specific attributes - new String[0] (no filter - will get
		// all)
		// recursive get of all subtree (of "start with" entry) - true
		NerEntryEx[] entries = nerService.getEntryList("/", true, true,
				new String[0], true);
		System.out.println("NER entry names:");

		for (NerEntryEx entry : entries) {
			System.out.println(entry.name);
		}
	}

	public Properties getConnectionParams() throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(new File("corba.properties")));

		corbaConnect = "corbaloc:iiop:" + props.getProperty("host") + ":"
				+ props.getProperty("port") + "/NameService";

		host = props.getProperty("host");
		login = props.getProperty("login");
		pass = props.getProperty("password");

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
		try {
			RADViewClient main = new RADViewClient();
			main.openEmsSession(args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}