package ex.corba.alu;

import nmsSession.NmsSession_IPOA;
import session.Session_I;

public class NmsSessionImpl extends NmsSession_IPOA {

	@Override
	public void eventLossCleared(String arg0) {
		System.out.println("Called eventLossCleared...");
	}

	@Override
	public void eventLossOccurred(String arg0, String arg1) {
		System.out.println("Called eventLossOccurred...");
	}

	public Session_I associatedSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void endSession() {
		System.out.println("Called endSession...");
	}

	@Override
	public void ping() {
		System.out.println("Called ping...");
	}

	@Override
	public void alarmLossOccurred(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
