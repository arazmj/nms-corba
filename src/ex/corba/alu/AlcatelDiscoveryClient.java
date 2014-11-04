package ex.corba.alu;

public class AlcatelDiscoveryClient extends AbstractClient {

	public static void main(String args[]) {
		AlcatelDiscoveryClient main = new AlcatelDiscoveryClient();

		try {
			main.openEmsSession(args);

			CorbaCommands cmd = new CorbaCommands(main.emsSession,
					main.realEMSName);

			cmd.getAllManagedElementNames();
			cmd.getAllEquipment();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			main.closeEmsSession();
		}
	}
}