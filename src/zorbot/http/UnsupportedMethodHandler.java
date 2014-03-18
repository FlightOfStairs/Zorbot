package zorbot.http;

import java.net.Socket;
import java.util.Map;

/**
 * Handler for unsupported methods. Will always return 501 Not Implemented.
 * @author Alistair Smith
 */
public class UnsupportedMethodHandler extends Handler {

	/**
	 * Create new handler for unsupported methods.
	 * @param headers Map of headers.
	 * @param connection Socket connection to client.
	 */
	protected UnsupportedMethodHandler(Map<String, String> headers,
			Socket connection) {
		super(headers, connection);
	}
	
	@Override
	public void respond() {
		status(501);
		data("501 Not Implemented");
		close();
	}
}
