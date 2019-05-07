package ponggame.client;

// SocketAction Class
// SocketAction.java

// All code graciously developed by Greg Turner. You have the right
// to reuse this code however you choose. Thanks Greg!

// Imports
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
import java.io.*;
import java.applet.*;

import javax.swing.JApplet;

class SocketAction extends Thread {
//  private DataInputStream inStream = null;
  private ByteBuffer inStream = null;
  protected PrintStream   outStream = null;
  //private Socket          socket = null;
//Create the server socket channel
  private ByteBuffer buf=null;
  private SocketChannel channel ,ball_channel;
  protected PongFrame a;
  static final int PORTNUM = 1234;
  String ip_address;
 
  private boolean break_out=false;
//Create client SocketChannel


  // nonblocking I/O
 
 // public SocketAction( JApplet a_inp ){
   public SocketAction( PongFrame a_out ){
    super("SocketAction");
	  //         channel=channel; 
	  System.out.println("a_inp is"+a_out);
	 // a=a_inp;
	  //try {
	  try {
		  channel = SocketChannel.open();
	  } catch (IOException e2) {
		  // TODO Auto-generated catch block
		  e2.printStackTrace();
	  }
	// nonblocking I/O
	  try {
		  channel.configureBlocking(false);
	  } catch (IOException e1) {
		  // TODO Auto-generated catch block
		  e1.printStackTrace();
	  }
	// Connection to host port 1234
	  if(Mywin.orientation == "left")
	  {
		  try {
			  channel.connect(new java.net.InetSocketAddress(InetAddress.getLocalHost(),PORTNUM ));
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
			  channel.connect(new java.net.InetSocketAddress(InetAddress.getLocalHost(),PORTNUM ));			 

		  } catch (IOException e1) {
			  // TODO Auto-generated catch block

			  e1.printStackTrace();
			  
		  }
	  }

		  if (Mywin.tcp_ip_str!=null)
		  { 
			  ip_address=Mywin.tcp_ip_str;
			  try {//remote
				  channel.connect(new java.net.InetSocketAddress(ip_address,PORTNUM ));

			  } catch (IOException e1) {

				  e1.printStackTrace();
			  }
		  }
	  
	  //           port.socket().bind(new java.net.InetSocketAddress(1234));
	  System.out.println("Client activ on port"+PORTNUM);
	  // Create the selector
	  Selector selector = null;
	  try {
		  selector = Selector.open();
	  } catch (IOException e1) {
		  // TODO Auto-generated catch block
		  e1.printStackTrace();
	  }
	  // Record to selector (OP_CONNECT type)
	  try {
		  SelectionKey key = channel.register(selector, SelectionKey.OP_CONNECT);
		  System.out.println("key clientKey is"+key);
	  } catch (ClosedChannelException e1) {
		  // TODO Auto-generated catch block
		  e1.printStackTrace();
	  }

	  // Waiting for the connection
	  try {
		  while (selector.select(1500)> 0) {

			  // Get keys
			  Set keys = selector.selectedKeys();
			  Iterator i = keys.iterator();

			  // For each key...
			  while (i.hasNext()) {
				  SelectionKey key = (SelectionKey)i.next();
				//  System.out.println("key client is"+key);
				 // channel.register(selector, SelectionKey.OP_READ);
				  // Remove the current key
				  i.remove();

				  // Get the socket channel held by the key
				  channel = (SocketChannel)key.channel();
				
				  // Attempt a connection
				  if (key.isConnectable()) {

					  SelectionKey key2 = channel.register(selector, SelectionKey.OP_READ);
					  System.out.println("key2 is now readable is"+key2);
					  // Connection OK
					  System.out.println("Server Found");
					  
					  
				  }
				  
			    if (key.isReadable()) {

					  channel = (SocketChannel) key.channel();

		    			// Read byte coming from the client
		    			int BUFFER_SIZE = 32;
		    			ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		    			try {
		    				channel.read(buf);
		    			}

		    			catch (Exception e) {
		    				// client is no longer active
		    				e.printStackTrace();
		    				return;
		    			}
		    			System.out.println("received by client"+buf.toString()+"\n");
		    			// Show bytes on the console
		    			buf.flip();
		    			Charset charset=Charset.forName("ISO-8859-1");
		    			CharsetDecoder decoder = charset.newDecoder();
		    			CharBuffer charBuffer = null;
		    			try {
		    				charBuffer = decoder.decode(buffer);
		    			} catch (CharacterCodingException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
		    			//System.out.print("received within client "+charBuffer.toString()+"\n");
		    			//System.out.print(charBuffer.toString());
		    			continue;
				  }
			     
				  // Close pendent connections
				  if (channel.isConnectionPending())
				  {
					  try {
						  channel.finishConnect();
					  } catch (IOException e) {
						  // TODO Auto-generated catch block
						  e.printStackTrace();
						  break_out=true;
						  break;
					  }
					  continue;
				  }
				  // Write continuously on the buffer
				  //   ByteBuffer buffer = null;
				  //   for (int i1 = 0;i1<20;i1++) {
				  //     send("lola  lalala");
				  //   }

			  } // inner while end 
			  if (break_out==true)
				  PongFrame.channel_readable=false;
				  break;
		  } //outer while end
	  } catch (IOException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
		  
	  }

  }



public void run() {
  }

  public void send(String s) {
	  // System.out.println("sent by client "+s+"\n");
	  ByteBuffer buffer = null;
	  buffer = 
		  ByteBuffer.wrap(
				  new String(s).getBytes());
	  try {
		  channel.write(buffer);
	  } catch (IOException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	  buffer.clear();
	  // outStream.println(s);
  }

  public String receive() throws IOException {
	  // Read byte coming from the client
	  int BUFFER_SIZE = 32;

	  ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
	  try {
		  channel.read(buffer);
		  //   buffer.flip();
	  }
	  catch (Exception e) {
		  // client is no longer active
		  e.printStackTrace();
		 // System.exit(1);
		  return "error";
	  }
	 //  System.out.println("receive client"+buffer.toString()+"\n");

	  //return buffer.toString();
	  buffer.flip();
	  Charset charset=Charset.forName("ISO-8859-1");
	  CharsetDecoder decoder = charset.newDecoder();
	  CharBuffer charBuffer = null;
	  try {
		  charBuffer = decoder.decode(buffer);
	  } catch (CharacterCodingException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	 // System.out.println("receive client converted"+charBuffer.toString()+"\n");
	  buffer.clear();
	  return charBuffer.toString();
  }

  public void closeConnections() {
	  try {
		  channel.close();
		  channel = null;
	  }
	  catch (IOException e) {
		  System.out.println("Couldn't close socket: " + e);
	  }
  }

  public boolean isConnected() {
	  return ((inStream != null) && (outStream != null) &&
			  (channel != null));
  }

  protected void finalize () {
	  if (channel != null) {
		  try {
			  channel.close();
		  }
		  catch (IOException e) {
			  System.out.println("Couldn't close socket: " + e);
		  }
		  channel = null;
	  }
  }
}
