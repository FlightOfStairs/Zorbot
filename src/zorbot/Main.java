package zorbot;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import zorbot.http.HTTPServer;
import zorbot.http.HTTPWorker;
import zorbot.rtsp.RTSPServer;


/**
 * Class to kick everything off and hold default values.
 * @author Alistair Smith
 */
public class Main {
	
	public static final int HTTP_PORT = 1337;
	
	public static final String SERVER_STRING = "Zorbot (best server ever)";

	public static final String webroot = System.getProperty("user.home") +
				File.separator + "www";
	
	public static final String defaultFile = "index.htm";
		
	public static void main(String[] args) throws IOException {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		HTTPServer httpServer = new HTTPServer(threadPool);
		httpServer.start();
		
		RTSPServer rtspServer = new RTSPServer(threadPool);
		rtspServer.start();
		
	}
}
