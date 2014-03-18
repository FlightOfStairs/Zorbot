package zorbot.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import zorbot.Main;

/**
 * Class to handle GET requests.
 * @author Alistair Smith
 */
public class GETHandler extends Handler {
	private final String path;

	/**
	 * Create new get handler.
	 * @param path String of path of file client requested.
	 * @param headers Map of headers.
	 * @param connection Socket connection to client.
	 */
	public GETHandler(String path, Map<String, String> headers,
			Socket connection) {
		super(headers, connection);
		
		this.path = path;
	}

	@Override
	public void respond() {
		File file = new File(Main.webroot + path); // find file within webroot
		if(file.isDirectory()) file = new File(
				Main.webroot + path + File.separator + Main.defaultFile);
		
		try { // attepmt the normal case: file exists
			FileInputStream in = new FileInputStream(file);
			
			status(200); // OK
			
			header("Date", rfcDate());
			header("Server", Main.SERVER_STRING);
			header("Connection", "close"); // no keep-alive
			
			byte[] buffer = new byte[1024];
			int bytes = 0;
			
			// copy bytes from the file to the socket.
			while((bytes = in.read(buffer)) != -1)
				data(buffer, bytes);
			
			in.close();
			
		} catch (FileNotFoundException e) {	// if file does not exist.
			status(404); // send 404 Not Found
			data("404 Not Found");
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			close();
		}
	}


}
