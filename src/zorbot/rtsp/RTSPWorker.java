package zorbot.rtsp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class RTSPWorker implements Runnable {
	
	private final RTSPServer server;
	private final Socket connection;

	private Session session;
	
	public RTSPWorker(Socket connection, RTSPServer server) {
		this.server = server;
		this.connection = connection;
	}

	@Override
	public void run() {
		try {
			while(connection.isConnected()) {
				String requestS = "";
				
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(connection.getOutputStream()));
				
				String line = reader.readLine();
				
				while(line != null && line.length() > 0) {
					requestS += line + "\r\n";
					line = reader.readLine();
				}
				
				Request request = Request.parseRequest(requestS);
				
				if(request.type == Request.Type.SETUP) {
					session = 
						new Session(server.getNewSessionID(),
								connection.getInetAddress(), request.getPort());
					
					server.addSession(session);
				} else if(session == null) 
					session = server.getSession(request.session);
				
				Response response = session.newRequest(request);
				
				writer.write(response.toString());
				
				writer.flush();
				
				if(request.type == Request.Type.TEARDOWN) {
					server.removeSession(session);
					connection.close();
				}
			}
		} catch (IOException e) { e.printStackTrace(); }
	}
}
