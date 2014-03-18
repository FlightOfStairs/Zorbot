package zorbot.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class HTTPServer extends Thread {
	public static final String HTTP_VERSION = "1.1";
	
	public static final int HTTP_PORT = 1337;
	
	private final ExecutorService threadPool;
	
	public HTTPServer(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}
	
	public void run() {
		try {
			ServerSocket server = new ServerSocket(HTTP_PORT);
			
			while (true) {
				try {
					Socket connection = server.accept();
					threadPool.execute(new HTTPWorker(connection));
				} catch (IOException e) { e.printStackTrace(); }
			}
		} catch (IOException e) { e.printStackTrace(); }
	}
}