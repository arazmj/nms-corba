package ex.corba.alu;

import nmsSession.NmsSession_I;
import session.Session_I;
import emsSession.EmsSession_I;

public class PingThread extends Thread {
	NmsSession_I nms = null;
	EmsSession_I ems = null;
	Session_I ses = null;
	boolean state = true;

	PingThread(NmsSession_I nms, EmsSession_I ems) {
		this.nms = nms;
		this.ems = ems;
		// this.ses = ems.associatedSession();

		start();
	}

	public void run() {

		boolean runThread = true;

		while (runThread && state) {
			try {
				System.out.println("ping");

				this.ems.ping();

				Thread.sleep(3000);
			} catch (Exception ex) {
				ex.printStackTrace();
				runThread = false;
			}
		}
	}

	public void stopPing() {
		this.state = false;
	}
}
