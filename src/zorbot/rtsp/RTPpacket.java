package zorbot.rtsp;

public class RTPpacket{

	  //size of the RTP header:
	  static int HEADER_SIZE = 12;

	  //Fields that compose the RTP header
	  public int Version;
	  public int Padding;
	  public int Extension;
	  public int CC;
	  public int Marker;
	  public int PayloadType;
	  public int SequenceNumber;
	  public int TimeStamp;
	  public int Ssrc;
	  
	  //Bitstream of the RTP header
	  public byte[] header;

	  //size of the RTP payload
	  public int payloadSize;
	  //Bitstream of the RTP payload
	  public byte[] payload;
	  


	  //--------------------------
	  //Constructor of an RTPpacket object from header fields and payload bitstream
	  //--------------------------
	  public RTPpacket(int PType, int Framenb, int Time, byte[] data, int dataLength){
	    //fill by default header fields:
	    Version = 2;
	    Padding = 0;
	    Extension = 0;
	    CC = 0;
	    Marker = 0;
	    Ssrc = 0;

	    //fill changing header fields:
	    SequenceNumber = Framenb;
	    TimeStamp = Time;
	    PayloadType = PType;
	    
	    //build the header bistream:
	    //--------------------------
	    header = new byte[HEADER_SIZE];
	    
	    header[0] = 0x40; // V./P/X/CC..
	    header[1] = 0x1A; // M/PT.....
	    
	    //sequence num
	    header[2] = (byte) ((Framenb >> 8) & 0xFF);
	    header[3] = (byte) (Framenb & 0xFF);
	    
	    //timestamp
	    header[4] = (byte) ((Time >> 24) & 0xFF);
	    header[5] = (byte) ((Time >> 16) & 0xFF);
	    header[6] = (byte) ((Time >> 8) & 0xFF);
	    header[7] = (byte) (Time & 0xFF);
	    
	    //SSRC
	    int server = 37337;
	    header[8] = (byte) ((server >> 24) & 0xFF);
	    header[9] = (byte) ((server >> 16) & 0xFF);
	    header[10] = (byte) ((server >> 8) & 0xFF);
	    header[11] = (byte) (server & 0xFF);

	 

	    //fill the payload bitstream:
	    //--------------------------
	    payloadSize = dataLength;
	    payload = new byte[dataLength];

	    for(int i = 0; i < dataLength; i++)
	    	payload[i] = data[i];

	    // ! Do not forget to uncomment method printheader() below !

	  }
	    
	  //--------------------------
	  //Constructor of an RTPpacket object from the packet bistream 
	  //--------------------------
	  public RTPpacket(byte[] packet, int packet_size)
	  {
	    //fill default fields:
	    Version = 2;
	    Padding = 0;
	    Extension = 0;
	    CC = 0;
	    Marker = 0;
	    Ssrc = 0;

	    //check if total packet size is lower than the header size
	    if (packet_size >= HEADER_SIZE) 
	      {
		//get the header bitsream:
		header = new byte[HEADER_SIZE];
		for (int i=0; i < HEADER_SIZE; i++)
		  header[i] = packet[i];

		//get the payload bitstream:
		payloadSize = packet_size - HEADER_SIZE;
		payload = new byte[payloadSize];
		for (int i=HEADER_SIZE; i < packet_size; i++)
		  payload[i-HEADER_SIZE] = packet[i];

		//interpret the changing fields of the header:
		PayloadType = header[1] & 127;
		SequenceNumber = unsigned_int(header[3]) + 256*unsigned_int(header[2]);
		TimeStamp = unsigned_int(header[7]) + 256*unsigned_int(header[6]) + 65536*unsigned_int(header[5]) + 16777216*unsigned_int(header[4]);
	      }
	 }

	  //--------------------------
	  //getpayload: return the payload bistream of the RTPpacket and its size
	  //--------------------------
	  public int getPayload(byte[] data) {

	    for (int i=0; i < payloadSize; i++)
	      data[i] = payload[i];

	    return(payloadSize);
	  }

	  //--------------------------
	  //getpayload_length: return the length of the payload
	  //--------------------------
	  public int getPayloadLength() {
	    return(payloadSize);
	  }

	  //--------------------------
	  //getlength: return the total length of the RTP packet
	  //--------------------------
	  public int getLength() {
	    return(payloadSize + HEADER_SIZE);
	  }

	  //--------------------------
	  //getpacket: returns the packet bitstream and its length
	  //--------------------------
	  public int getPacket(byte[] packet)
	  {
	    //construct the packet = header + payload
	    for (int i=0; i < HEADER_SIZE; i++)
		packet[i] = header[i];
	    for (int i=0; i < payloadSize; i++)
		packet[i+HEADER_SIZE] = payload[i];

	    //return total size of the packet
	    return(payloadSize + HEADER_SIZE);
	  }

	  //--------------------------
	  //gettimestamp
	  //--------------------------

	  public int getTimeStamp() {
	    return(TimeStamp);
	  }

	  //--------------------------
	  //getsequencenumber
	  //--------------------------
	  public int getSequenceNumber() {
	    return(SequenceNumber);
	  }

	  //--------------------------
	  //getpayloadtype
	  //--------------------------
	  public int getPayloadType() {
	    return(PayloadType);
	  }


	  //--------------------------
	  //print headers without the SSRC
	  //--------------------------
	  public void printHeader()
	  {
	    for (int i=0; i < (HEADER_SIZE-4); i++)
	      {
		for (int j = 7; j>=0 ; j--)
		  if (((1<<j) & header[i] ) != 0)
		    System.out.print("1");
		else
		  System.out.print("0");
		System.out.print(" ");
	      }

	    System.out.println();
	  }

	  //return the unsigned value of 8-bit integer nb
	  static int unsigned_int(int nb) {
	    if (nb >= 0)
	      return(nb);
	    else
	      return(256+nb);
	  }

	}
