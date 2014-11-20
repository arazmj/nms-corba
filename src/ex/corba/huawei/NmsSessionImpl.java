package ex.corba.huawei;

import com.huawei.u2000.nmsSession.NmsSession_IPOA;
import com.huawei.u2000.session.Session_I;

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
}
