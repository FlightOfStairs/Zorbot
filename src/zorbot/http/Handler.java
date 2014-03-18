package zorbot.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Abstract class that all Method handlers must extend. Provides various methods
 * to ensure sanity of data sent back to the client.
 * @author Alistair Smith
 */
public abstract class Handler {
	
	private final Socket connection;
	private final OutputStream out;
	private final Map<String, String> headers;
	
	// Whether or not we're done sending status to the client.
	private boolean statusSent = false; 
	// Whether or not we're done with headers. 
	private boolean headersSent = false;

	/**
	 * Create a new Handler.
	 * @param headers Map to look up header values.
	 * @param connection Socket onnection to the client.
	 */
	protected Handler(Map<String, String> headers, Socket connection) {
		this.headers = headers;
		this.connection = connection;
		
		OutputStream tmpOut = null;
		try { tmpOut = connection.getOutputStream(); }
		catch (IOException e) { e.printStackTrace(); }
		this.out = tmpOut;
	}
	
	/**
	 * Get the value of a given header.
	 * @param key String representing the HTTP header to be found. Must be
	 * correct case.
	 * @return String value of the header.
	 */
	protected String getHeaderValue(String key) { return headers.get(key); }
	
	/**
	 * Send a status code to the client. i.e. "200 OK" or "404 Not Found". This
	 * method can only ever be called once on a handler: any more and it will
	 * throw and IllegalStateException.
	 * @param code Integer status code; 200 for OK, 404 for Not Found etc.
	 */
	protected void status(int code) {
		if(statusSent) throw new IllegalStateException("Already sent status");
		
		byte[] bytes = ("HTTP/" + HTTPServer.HTTP_VERSION + " " + code + " " +
				HTTPStatus.instance().getText(code) + "\r\n").getBytes();
		
		try { out.write(bytes); }
		catch (IOException e) { e.printStackTrace(); }
		
		statusSent = true;
	}
	
	/**
	 * Send a header of given key and value. This method must be sent after
	 * status has been sent, and before data has started being sent otherwise
	 * an IllegalStateException will be thrown.
	 * @param key Header to send.
	 * @param value Value of that header.
	 */
	protected void header(String key, String value) {
		if(! statusSent) throw new IllegalStateException("Must send status " +
				"before headers");
		if(headersSent) throw new IllegalStateException("Cannot send headers " +
				"after data");
		
		key = key.substring(0, 1).toUpperCase() + key.substring(1);
		
		byte[] bytes = (key + ": " + value + "\r\n").getBytes();
		
		try { out.write(bytes); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	/**
	 * Mark headers as completed so that we can move on to data, and send
	 * &lt;CR&gt;&lt;LF&gt; to the client. After this method has been called,
	 * no more headers can be sent. If status has not been sent, or headers have
	 * previously been completed, throws IllegalStateException.
	 */
	protected void completeHeaders() {
		if(! statusSent) throw new IllegalStateException("Must send status " +
			"before headers");
		if(headersSent)
			throw new IllegalStateException("Headers already completed");
		
		try { out.write("\r\n".getBytes()); }
		catch (IOException e) { e.printStackTrace(); }
		
		headersSent = true;
	}
	
	/**
	 * Send string of data to the client. No more headers can be sent after
	 * this method has been called. If status hasn't been sent,
	 * IllegalStateException will be thrown.
	 * @param s Data to be sent.
	 */
	protected void data(String s) {
		byte[] bytes = s.getBytes();
		data(bytes, bytes.length);
	}
	
	/**
	 * Send number of bytes from given byte array to the client. No more headers
	 * can be sent after this method has been called. If status hasn't been
	 * sent, IllegalStateException will be thrown.
	 * @param bytes Array of bytes containing the data.
	 * @param noBytes How many bytes (consecutive from the start of the array)
	 * to be sent.
	 */
	protected void data(byte[] bytes, int noBytes) {
		if(! statusSent) throw new IllegalStateException("Must send status " +
				"before data");
		
		if(! headersSent) completeHeaders(); // move on automatically
		
		try { out.write(bytes, 0, noBytes); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	/**
	 * Close the connection. Can be called at any time. If called before
	 * status is sent, sends "500 Internal Server Error". After calling close(),
	 * nothing more can be sent to the client.
	 */
	protected void close() {
		if(!statusSent) status(500); // Internal server error
		if(!headersSent) completeHeaders();
		
		try { 
			connection.shutdownOutput();
			connection.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	/**
	 * Get the current date/time formatted according to RFC 1123
	 * @return String of formatted date/time.
	 */
	protected String rfcDate() { return rfcDate(new Date()); }
	
	/**
	 * Get given date formatted according to RFC 1123
	 * @param d Date to be formatted.
	 * @return String of formatted date/time.
	 */
	protected String rfcDate(Date d) {
		SimpleDateFormat formatter =
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		return formatter.format(d);
	}
	
	/**
	 * Send response data to the client.
	 */
	public abstract void respond();
}
