package ex.corba.alu;

import emsSession.EmsSession_I;

public class AlcatelDiscoveryClient extends AbstractClient {

	public static void main(String args[]) {
		AlcatelDiscoveryClient main = new AlcatelDiscoveryClient();
		EmsSession_I emsSession = null;
		
		try {
			emsSession = main.openEmsSession(args);

			CorbaCommands cmd = new CorbaCommands(emsSession,
					main.realEMSName);

			cmd.getAllManagedElementNames();
			cmd.getAllEquipment();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			main.closeEmsSession(emsSession);
		}
	}
}