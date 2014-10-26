package ex.corba;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import com.netcracker.huawei.t2000.v200r002c01.emsSession.EmsSession_IHolder;
import com.netcracker.huawei.t2000.v200r002c01.emsSessionFactory.EmsSessionFactory_I;
import com.netcracker.huawei.t2000.v200r002c01.emsSessionFactory.EmsSessionFactory_IHelper;

public class HuaweiClient extends AbstractClient {

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
		NameComponent name[] = rootContext
				.to_name("TMF_MTNM.Class/HUAWEI.Vendor/" + emsName
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
		nmsSession = createNmsSession();

		// Create EMS Session
		EmsSession_IHolder sessionHolder = new EmsSession_IHolder();
		sessionFactory.getEmsSession(login, pass, nmsSession, sessionHolder);
		emsSession = sessionHolder.value;
		System.out.println("emsSession: " + emsSession);
	}

	public static void main(String args[]) {
		HuaweiClient main = new HuaweiClient();

		try {
			main.openEmsSession(args);

			CorbaCommands cmd = new CorbaCommands(main.emsSession, main.realEMSName);

			cmd.getAllManagedElementNames();
			cmd.getAllEquipment();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			main.closeEmsSession();
		}
	}
}