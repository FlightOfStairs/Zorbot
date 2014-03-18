package zorbot.rtsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Response {
	public static final String version = "RTSP/1.0";
			
	public final int code;
	public final int seq;
	public final int session;
	
	public Response(int code, int seq, int session) {
		this.code = code;
		this.seq = seq;
		this.session = session;
	}
	
	public static Response parseResponse(String responseString) throws IOException {
		responseString = responseString.trim();
		
		BufferedReader reader = new BufferedReader(
				new StringReader(responseString));
		
		StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
		tokenizer.nextToken();
		
		int code = Integer.parseInt(tokenizer.nextToken());

		tokenizer = new StringTokenizer(reader.readLine());
		tokenizer.nextToken();
		
		int seq = Integer.parseInt(tokenizer.nextToken());
		
		tokenizer = new StringTokenizer(reader.readLine());
		tokenizer.nextToken();
		
		int session = Integer.parseInt(tokenizer.nextToken());
		
		return new Response(code, seq, session);
	}
	
	@Override
	public String toString() {
		String s = "";
		
		s += version + " " + code + " " + RTSPStatus.instance().getText(code) + "\r\n";
		s += "CSeq: " + seq + "\r\n";
		s += "Session: " + session + "\r\n\r\n";
		
		return s;
	}

}
