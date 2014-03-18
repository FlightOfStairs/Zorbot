package zorbot.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class allowing lookup of HTTP status codes. Codes must be stored in
 * a file named HTTPStatusCodes.txt in the program directory.
 * @author Alistair Smith
 */
public class HTTPStatus {
	private static class SingletonHolder {
		private static final HTTPStatus instance = new HTTPStatus();
	}
	
	public static HTTPStatus instance() { return SingletonHolder.instance; }
	
	private static final String codePath = "HTTPStatusCodes.txt";

	private Map<Integer, String> statuses = new HashMap<Integer, String>();
	
	private HTTPStatus() {
		try {
			BufferedReader reader =
				new BufferedReader(new FileReader(new File(codePath)));
			
			String line;
			
			// Loop through lines in file. Status code/key is everything before
			// the first space. Text/value is everything after.
			while((line = reader.readLine()) != null)
				statuses.put(new Integer(line.substring(0, 3)),
						line.substring(4));
			reader.close();
			
		} catch (IOException e) { e.printStackTrace(); }
		
	}
	
	/**
	 * Get the error message text of a given code. i.e. getText(404) returns
	 * "Not Found".
	 * @param code Index of status code.
	 * @return String representation of status code.
	 */
	public String getText(int code) {
		if(statuses.containsKey(code)) return statuses.get(code);
		return "";
	}
}
