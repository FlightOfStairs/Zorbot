package zorbot.rtsp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class RTSPServer extends Thread {
	public static final int RTSP_PORT = 31337;
	
	private int nextSessionID = 0;
	
	private final Map<Integer, Session> sessions =
		new HashMap<Integer, Session>();

	private final ExecutorService threadPool;

	public RTSPServer(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}
	
	public synchronized int getNewSessionID() {	return nextSessionID++;	}
	
	public synchronized Session getSession(int sessionID) {
		return sessions.get(sessionID);
	}
	
	public synchronized void addSession(Session session) {
		sessions.put(session.getSessionID(), session);
	}
	
	public synchronized void removeSession(Session session) {
		sessions.remove(session.getSessionID());
	}
	
	public void run() {
		try {
			ServerSocket server = new ServerSocket(RTSP_PORT);
			
			while (true) {
				try {
					Socket connection = server.accept();
					threadPool.execute(new RTSPWorker(connection, this));
				} catch (IOException e) { e.printStackTrace(); }
			}
		} catch (IOException e) { e.printStackTrace(); }
	}
}
