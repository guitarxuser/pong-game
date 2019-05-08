package ponggame.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Vector;
import java.util.concurrent.Exchanger;

public class udp_cli extends Thread{
	
	public static ByteBuffer buffer = ByteBuffer.allocate(1024);
	public static InetSocketAddress serverAddress=null;
	public static String response="";
	static final int PORTNUM = 8989;
	public static DatagramChannel client = null;
	private Vector<Integer> v= new Vector<Integer>(); 
	private  Exchanger<String> ex= new Exchanger<String>();
	String ip_address;
	public  udp_cli(Vector<Integer> v) throws Exception {
		this.v=v;

	    client = DatagramChannel.open();

	    client.bind(null);

	    String msg = "Hello I'am the Ball server";
	    buffer = ByteBuffer.wrap(msg.getBytes());
	  
	    /////////////////generic///////////////////
	    if(Mywin.orientation == "left")
		  {
			  try {
				  serverAddress = new InetSocketAddress(InetAddress.getLocalHost(), PORTNUM);
				  //  port.socket().bind(new java.net.InetSocketAddress(a.getCodeBase().getHost(), PORTNUM ));
				  //  port.connect(new java.net.InetSocketAddress(a.getCodeBase().getHost(), PORTNUM ));
			  } catch (IOException e1) {
				  // TODO Auto-generated catch block
				  e1.printStackTrace();
			  }
		  }
	    if((Mywin.orientation == "right") && (Mywin.tcp_ip_str==null))
		  {	  
			  try { //local
				  serverAddress = new InetSocketAddress(InetAddress.getLocalHost(), PORTNUM);			 

			  } catch (IOException e1) {
				  // TODO Auto-generated catch block

				  e1.printStackTrace();
				  
			  }
		  }

			  if (Mywin.tcp_ip_str!=null)
			  { 
				  ip_address=Mywin.tcp_ip_str;
				  // serverAddress = new InetSocketAddress(InetAddress.getLocalHost(), PORTNUM);
				  serverAddress = new java.net.InetSocketAddress(ip_address,PORTNUM );
			  }
	   
	    /* Initialization */
	    try {
			client.send(buffer, serverAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	    /////////////////////////////////////
	    

	public void run()
	{
		while(true)
		{

			try {
				client.send(buffer, serverAddress);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (v) {
				buffer.clear();
				try {
					v.clear();
					v.add(1);
					client.receive(buffer);

				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				buffer.flip();
				int limits = buffer.limit();
				byte bytes[] = new byte[limits];
				buffer.get(bytes, 0, limits);
				response = new String(bytes);	 
//				System.out.println("Clienty says, Server  responded: " + response);
				v.notify();	
			}
		}
	}	
	
	public void udp_send(String s) {
		  // System.out.println("sent by client "+s+"\n");
		  ByteBuffer buffer = null;
		  buffer = 
			  ByteBuffer.wrap(
					  new String(s).getBytes());
		  try {
			  client.send(buffer, serverAddress);
		  } catch (IOException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
		  buffer.clear();
		  // outStream.println(s);
	  }
}
