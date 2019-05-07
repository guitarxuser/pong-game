package ponggame.ball;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
//  w  ww  . ja v  a2 s.  c om
import java.util.Vector;

public class echo_srv extends Thread{
	public static DatagramChannel server = null;
	public static ByteBuffer buffer = ByteBuffer.allocate(1024);
	public static InetSocketAddress sAddr=null;
	static final int PORTNUM = 8989;
	private static String s_ball_x_to_send="";
	private Vector<Integer> v= new Vector<Integer>(); 
	
	public echo_srv (Vector<Integer> v )  {
		this.v=v;
		try {
			server = DatagramChannel.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		sAddr=InetAddress.getLocalHost();
		try {
			sAddr = new InetSocketAddress(InetAddress.getLocalHost(), PORTNUM);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			server.bind(sAddr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//server.close();
	}
	public void run(){
		while (true) {
		//			System.out.println("Waiting for a  message  from"
		//					+ "  a  remote  host at " + sAddr);
		SocketAddress remoteAddr = null;
		try {
			remoteAddr = server.receive(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buffer.flip();
		int limits = buffer.limit();
		byte bytes[] = new byte[limits];
		//data from ball calculation

		//			buffer.get(bytes, 0, limits);
		//			String msg = new String(bytes);
	
		synchronized(v){
			try {
				v.wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			buffer=ByteBuffer.wrap(getS_ball_x_to_send().getBytes());
		//	System.out.println("echo_serv to Client at " + remoteAddr + "  says: " + s_ball_x_to_send);

			buffer.rewind();
			
			try {
				server.send(buffer, remoteAddr);
				v.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			buffer.clear();
		}//synchronized
		}
	}
	public static String getS_ball_x_to_send() {
		return s_ball_x_to_send;
	}
	public static void setS_ball_x_to_send(String s_ball_x_to_send) {
		echo_srv.s_ball_x_to_send = s_ball_x_to_send;
	}


}
