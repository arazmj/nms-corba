package ex.corba.ciena;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

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

import com.ciena.oc.emsSession.EmsSession_I;
import com.ciena.oc.emsSession.EmsSession_IHolder;
import com.ciena.oc.emsSessionFactory.EmsSessionFactory_I;
import com.ciena.oc.emsSessionFactory.EmsSessionFactory_IHelper;
import com.ciena.oc.nmsSession.NmsSession_I;
import com.ciena.oc.nmsSession.NmsSession_IHelper;
import com.ciena.oc.nmsSession.NmsSession_IPOATie;

public class CienaConnection {
	protected String corbaConnect;
	protected String login;
	protected String pass;
	protected String emsName;
	protected String realEMSName;
	protected ORB orb;
	protected POA rootPOA;
	protected Properties props;

	public static void main(String args[]) {
		CienaConnection main = new CienaConnection();
		EmsSession_I emsSession = null;

		try {
			main.openEmsSession(args);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			main.closeEmsSession(emsSession);
		}
	}

	public EmsSession_I openEmsSession(String args[]) throws Exception {
		Properties props = getConnectionParams();

		// create and initialize the ORB
		orb = ORB.init(args, props);
		System.out.println("ORB.init called.");

		// Get the root naming context
		NamingContextExt rootContext = NamingContextExtHelper.narrow(orb
				.resolve_initial_references("NameService"));
		System.out.println("NameService found.");

		// Resolve the reference to EMSSessionFactory
		NameComponent name[] = rootContext
				.to_name("TMF_MTNM.Class/CIENA.Vendor/" + emsName
						+ ".EmsInstance/2\\.0.Version");

		NamingContextExt subContext = NamingContextExtHelper.narrow(rootContext
				.resolve(name));

		// Obtaining reference to SessionFactory
		org.omg.CORBA.Object ems = subContext.resolve_str(emsName
				+ ".EmsSessionFactory_I");

		System.out.println("ems: " + ems);

		EmsSessionFactory_I sessionFactory = EmsSessionFactory_IHelper
				.narrow(ems);
		System.out.println("\nEmsSessionFactory: " + sessionFactory);

		// Create NMS Session
		NmsSession_I nmsSession = createNmsSession();

		// Create EMS Session
		EmsSession_IHolder sessionHolder = new EmsSession_IHolder();
		sessionFactory.getEmsSession(login, pass, nmsSession, sessionHolder);
		EmsSession_I emsSession = sessionHolder.value;

		System.out.println("\nAuthentication successful!!! emsSession: "
				+ emsSession);

		return emsSession;
	}

	public EmsSession_I openEmsSessionUsingIOR(String args[]) throws Exception {
		Properties props = getConnectionParams();

		// create and initialize the ORB
		orb = ORB.init(args, props);
		System.out.println("ORB.init called.");

		// read stringified object to file (IOR file)
		FileReader fr = new FileReader("ciena-lab.ior");
		BufferedReader br = new BufferedReader(fr);
		String ior = br.readLine();
		br.close();

		// Obtaining reference to SessionFactory
		org.omg.CORBA.Object ems = orb.string_to_object(ior);

		System.out.println("ems: " + ems);

		EmsSessionFactory_I sessionFactory = EmsSessionFactory_IHelper
				.narrow(ems);
		System.out.println("\nEmsSessionFactory: " + sessionFactory);

		// Create NMS Session
		NmsSession_I nmsSession = createNmsSession();

		// Create EMS Session
		EmsSession_IHolder sessionHolder = new EmsSession_IHolder();
		sessionFactory.getEmsSession(login, pass, nmsSession, sessionHolder);
		EmsSession_I emsSession = sessionHolder.value;

		System.out.println("\nAuthentication successful!!! emsSession: "
				+ emsSession);

		return emsSession;
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
		NmsSession_I nmsSession = NmsSession_IHelper.narrow(tieobj._this());

		return nmsSession;
	}

	public void closeEmsSession(EmsSession_I emsSession) {
		if (emsSession != null) {
			emsSession.endSession();
		}

		if (rootPOA != null) {
			rootPOA.destroy(true, true);
		}

		if (orb != null) {
			orb.shutdown(true);
			orb.destroy();
		}
	}

	public Properties getConnectionParams() throws Exception {
		props = new Properties();
		props.load(new FileInputStream(new File("ciena.properties")));

		corbaConnect = "corbaloc:iiop:" + props.getProperty("host") + ":"
				+ props.getProperty("port") + "/"
				+ props.getProperty("NameService");

		login = props.getProperty("login");
		pass = props.getProperty("password");
		emsName = props.getProperty("EMSname");

		// prepare ems name for usage
		realEMSName = emsName;
		emsName = emsName.replaceAll("/", "\\\\/");
		emsName = emsName.replaceAll("\\.", "\\\\\\.");

		props.setProperty("jacorb.net.socket_factory",
				"org.jacorb.orb.factory.DefaultSocketFactory");
		props.setProperty("jacorb.net.server_socket_factory",
				"org.jacorb.orb.factory.DefaultServerSocketFactory");

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

		// Proxy address in IOR
		String iorProxy = props.getProperty("jacorb.ior_proxy_address");
		System.out.println("iorProxy: " + iorProxy);

		if (iorProxy != null && !iorProxy.trim().equals("")) {
			props.setProperty("jacorb.ior_proxy_address", iorProxy);
		}

		return props;
	}
}