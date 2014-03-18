package zorbot.rtsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

public class Request {
	public static final String version = "RTSP/1.0";
	
	public enum Type { SETUP, PLAY, PAUSE, TEARDOWN }
	
	public final Type type;
	public final String url;
	public final int seq;
	
	public final int session;
	private int port = -1;
	
	public Request(Type type, String url, int seq, int session) {
		this.type = type;
		this.url = url;
		this.seq = seq;
		this.session = session;
	}
	
	public Request(String url, int seq, int port) {
		this(Type.SETUP, url, seq, -1);
		this.port = port;
	}
	
	public int getPort() { return port; }
	
	public static Request parseRequest(String requestString) throws IOException {
		requestString = requestString.trim();
		
		BufferedReader reader = new BufferedReader(
				new StringReader(requestString));
		
		StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
		
		Type type = Type.valueOf(tokenizer.nextToken());
		String url = tokenizer.nextToken();
		
		tokenizer = new StringTokenizer(reader.readLine());
		
		tokenizer.nextToken();
		int seq = Integer.parseInt(tokenizer.nextToken());
		
		tokenizer = new StringTokenizer(reader.readLine());
		
		if(type == Type.SETUP) {
			tokenizer.nextToken();tokenizer.nextToken();tokenizer.nextToken();
			
			int port = Integer.parseInt(tokenizer.nextToken());
			
			return new Request(url, seq, port);
		} else {
			tokenizer.nextToken();
			
			int session = Integer.parseInt(tokenizer.nextToken());
			
			return new Request(type, url, seq, session);
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		
		s += type + " " + url + " " + version + "\r\n";
		s += "CSeq: " + seq + "\r\n";
		
		if(type == Type.SETUP)
			s += "Transport: RTP/UDP; client_port= " + port + "\r\n\r\n";
		else
			s += "Session: " + session + "\r\n\r\n";
		
		return s;
	}
}
