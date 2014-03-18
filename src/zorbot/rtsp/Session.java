package zorbot.rtsp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.Timer;

import zorbot.Main;

public class Session implements ActionListener {
	int imageNum = 0; //image nb of the image currently transmitted
	VideoStream video; //VideoStream object used to access video frames
	static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
	static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
	static int VIDEO_LENGTH = 500; //length of the video in frames

	private final int session;
	private final InetAddress clientIP;
	private final int clientPort;

	Timer timer;

	private String url;

	private State state = State.INIT;

	private final DatagramSocket rtpSocket;


	public Session(int session, InetAddress clientIP, int clientPort) throws SocketException, UnknownHostException {
		this.session = session;
		this.clientIP = clientIP;
		this.clientPort = clientPort;

		System.out.println(clientPort);
		
		rtpSocket = new DatagramSocket();

		timer = new Timer(FRAME_PERIOD, this);
		timer.setInitialDelay(0);
		timer.setCoalesce(true);
	}

	public int getSessionID() { return session; }

	public Response newRequest(Request request)  {
		
		switch(request.type) {
		
		case SETUP:
			if(state != State.INIT)
				return new Response(455, request.seq, session);
			try {
				video = new VideoStream(
						Main.webroot + File.separator + request.url);
				state = State.READY;
			} catch(FileNotFoundException e) {
				return new Response(404, request.seq, session);
			}
			return new Response(200, request.seq, session);
			
			
		case PLAY:
			if(state != State.READY)
				return new Response(455, request.seq, session);
			timer.start();
			state = State.PLAYING;
			return new Response(200, request.seq, request.session);
			

		case PAUSE:
			if(state != State.PLAYING)
				return new Response(455, request.seq, session);
			timer.stop();
			state = State.READY;
			return new Response(200, request.seq, session);
			
			
		case TEARDOWN:
			timer.stop();
			state = State.INIT;
			return new Response(200, request.seq, session);
			
			
		default:
			// bad things
			return new Response(500, request.seq, session);
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if(imageNum >= VIDEO_LENGTH) {
			timer.stop();
			return;
		}
			imageNum++;

		try {
			byte[] buf = new byte[15000];
			int imageLength = video.getnextframe(buf);

			RTPpacket rtpPacket = new RTPpacket(MJPEG_TYPE, imageNum, 
					imageNum*FRAME_PERIOD, buf, imageLength);

			int packetLength = rtpPacket.getLength();

			byte[] packetBits = new byte[packetLength];
			rtpPacket.getPacket(packetBits);

			//send the packet as a DatagramPacket over the UDP socket 
			DatagramPacket senddp = new DatagramPacket(
					packetBits, packetLength, clientIP, clientPort);
			rtpSocket.send(senddp);

			//rtpPacket.printHeader();

			System.out.println("Send frame #" + imageNum);
		} catch(Exception e) { e.printStackTrace(); }
	}
}
