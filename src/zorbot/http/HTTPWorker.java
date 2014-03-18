package zorbot.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


/**
 * Worker thread. When run with a given connection as input, it parses the HTTP
 * headers sent by the client and passes them off to the approprate handler.
 * @author Alistair Smith
 */
public class HTTPWorker implements Runnable {	
	private final Socket connection;
	
	public HTTPWorker(Socket connection) { this.connection = connection; }

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			
			// Request is first line of header.
			String request = reader.readLine();
			
			System.out.println(request);
			
			String[] requestParts = request.split(" ");
			String method = requestParts[0]; // Method is first word.
			
			Map<String, String> headers = new HashMap<String, String>();
			
			String line;
			
			// Parse remaining headers. Key is everything before the first
			// colon; value is everything after, excluding the space.
			while((line = reader.readLine()) != null) {
				int colonPos = line.indexOf(":");
				if(colonPos == -1) break;
				String key = line.substring(0, colonPos);
				String value = line.substring(colonPos + 2);
				headers.put(key, value);
			}
						
			Handler h;
			
			// Figure out which method handler is best suited for this request.
			// Pass it the connection, headers and anything else it needs.
			if(method.equals("GET")) {
				h = new GETHandler(requestParts[1], headers, connection);
			} else h = new UnsupportedMethodHandler(headers, connection);
			
			h.respond(); // have it respond to the client.
			
		} catch (IOException e) { e.printStackTrace(); }
	}

}
