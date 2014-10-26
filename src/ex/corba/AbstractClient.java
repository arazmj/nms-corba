package ex.corba;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE;
import org.omg.BiDirPolicy.BOTH;
import org.omg.BiDirPolicy.BidirectionalPolicyValueHelper;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import com.netcracker.huawei.t2000.v200r002c01.emsSession.EmsSession_I;
import com.netcracker.huawei.t2000.v200r002c01.nmsSession.NmsSession_I;
import com.netcracker.huawei.t2000.v200r002c01.nmsSession.NmsSession_IHelper;
import com.netcracker.huawei.t2000.v200r002c01.nmsSession.NmsSession_IPOATie;

public abstract class AbstractClient {
	protected String corbaConnect;
	protected String login;
	protected String pass;
	protected String emsName;
	protected String realEMSName;
	protected ORB orb;
	protected POA rootPOA = null;
	protected NmsSession_I nmsSession = null;
	protected EmsSession_I emsSession = null;

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
}