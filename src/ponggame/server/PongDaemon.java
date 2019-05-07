package ponggame.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.io.*;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


public class PongDaemon extends Thread implements Runnable{
  public static final int PORTNUM = 1234;
  public static final int PLAYER1_ID = 2;
  public static final int PLAYER2_ID = 3;
  public static final int BALL_ID = 1;
  
  private ServerSocketChannel    port;
  private  ServerSocketChannel server=null;
  private static  SocketChannel read_channel=null;
  private static  SocketChannel ball_channel=null;
  private static  SocketChannel client_channel_1=null;
  private static  SocketChannel client_channel_2=null;
  private Selector selector=null;

  private Vector<SelectionKey>          p1Queue = null;
  private Vector          p2Queue = null;
  boolean players_paired=false;
  
  //--------------Constructor----------------------//
  
  public PongDaemon() throws IOException {

//	  super(client_channel);
	  //  super (key);

	  p1Queue = new Vector(10,10);
	  p2Queue = new Vector(10,10);
	  
	  ///////////////////////////////////////////////////////////////////////////////
	  // Create the server socket channel
	  
	  server = ServerSocketChannel.open();
	  // nonblocking I/O
	  server.configureBlocking(false);
	  // host-port PORTNUM
   //   SocketAddress address = new InetSocketAddress(
   //           InetAddress.getLocalHost(), PORTNUM);
      
	//  server.socket().bind(new java.net.InetSocketAddress("pcsika02",PORTNUM));
      server.socket().bind(new java.net.InetSocketAddress(
              InetAddress.getLocalHost(), PORTNUM));
	
	  System.out.println("Server activ on tcp ip port "+InetAddress.getLocalHost()+" :"+PORTNUM);
	  // Create the selector
	  selector = Selector.open();
	  System.out.println("selector open");
	  // Recording server to selector (type OP_ACCEPT)
	  server.register(selector,SelectionKey.OP_ACCEPT);
	  //UDP part
	 
  } 

  public void run() {
    // Even though we are functioning as a daemon in regard to the
    // game, we don't want to be declared as a daemon thread here
    // because we don't want the runtime system to kill us off.
  //  Socket clientSocket;
    
//    while (true) {
/////////////////////////////////////////////////////////////////////////////////
    	// Infinite server loop
    for(;;) {
    //sstem.out.println("trying");
    	// Waiting for events
    	try {	
			selector.select();
			//System.out.println("selector?");	
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	//sysem.out.println("trying out");	
    	// Get keys
    	Set keys = selector.selectedKeys();
    	Iterator i = keys.iterator();
        //System.out.println("Iterator i"+i);
    	// For each keys...
    	
    	
    	while(i.hasNext()) {
//    		System.out.println("next ?"+i+"\n");
    		SelectionKey key = (SelectionKey) i.next();
    		if (!p1Queue.contains(key))
    		{
    			p1Queue.add(key);
    			System.out.println("p1Queue is"+p1Queue+"\n");
    			System.out.println(p1Queue.size()+"\n");
    		}
    		if (p1Queue.size()==4)//we have the first pair and ball
    		{
    		//	System.out.println("paired p1Queue is"+p1Queue+"\n");
      		//	System.out.println("elem 1"+p1Queue.elementAt(0)+"\n");
    		//	System.out.println("elem 2"+p1Queue.elementAt(1)+"\n");
    		//	System.out.println("elem 3"+p1Queue.elementAt(2)+"\n");
    		//	System.out.println("elem 4"+p1Queue.elementAt(3)+"\n");  			
    			
    		}
    		
           // System.out.println("key is "+key+"\n");
            
   //         SelectionKey key1 = (SelectionKey)
        //    System.out.println("i"+i+"\n");
            
    		// Remove the current key
    		i.remove();

    		// if isAccetable = true
    		// then a client required a connection
    		if (key.isAcceptable()) {
    			// get client socket channel
    			//   	      SocketChannel client = server.accept();
    			//   	      new PongPlayer(this, client).start();
    			try {    	    	  
    				
    			System.out.println("read channel");
    			read_channel = server.accept();
       			// Non Blocking I/O
    			read_channel.configureBlocking(false);
    			// recording to the selector (reading)
    			read_channel.register(selector, SelectionKey.OP_READ);
    	   	  // 	System.out.println("PngPlayer now");	
    		 // new PongPlayer(this, client_channel).start();
    			}
    			catch (IOException e) {
    				System.out.println("Couldn't connect player: " + e);
    				System.exit(1);
    			}

    		}

    		// if isReadable = true{
    		// then the server is ready to read 
   			// Read byte coming from the client
			int BUFFER_SIZE = 8;
			ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    		if (key.isReadable()) {

    			read_channel = (SocketChannel) key.channel();

    			try {
    				read_channel.read(buffer);
    				
    			}   			
    			catch (Exception e) {
    				// client is no longer active
    				e.printStackTrace();
    				try {
						read_channel.close();
						System.out.println("chanel closed");
						remove_client_keys(key);
						key.cancel();
						
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
    				//return;
    			}
    			//System.out.println("received by deamon but buffer"+buffer.toString()+"\n");
    			// Show bytes on the console
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
    			//System.out.print("received within daemon "+charBuffer.toString()+"\n");
    		//	System.out.print(charBuffer.toString());
    		
				
    			if (p1Queue.size()==4)//we have the first pair and ball

    			{
    				
    				if(players_paired=false) //first run flag
    				{
    					players_paired=true;
    					init_ball();
    				}

    				if (key==p1Queue.elementAt(PLAYER1_ID)) //P1 coordinates
    				{
    					SelectionKey key1 = p1Queue.elementAt(PLAYER2_ID); //the cross change assignment 1 -> 2
    					client_channel_1 = (SocketChannel) key1.channel();
    					try {
    						String paddleX_buffer="X"+new String(charBuffer.toString()); //paddle protocol X123X124X125
    						buffer = ByteBuffer.wrap(
    								new String(paddleX_buffer.toString()).getBytes());

    						System.out.println("writing chanel 1 \n"+key1+"\n");
    						client_channel_1.write(buffer);
    						//System.out.println("ret1="+ret1);
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    					///////////////  LEFT PADDLE  to ball//////////////////

    					SelectionKey key1_3 = p1Queue.elementAt(BALL_ID); 
    					ball_channel = (SocketChannel) key1_3.channel();
    					try {
    						String paddle1_buffer="L"+new String(charBuffer.toString()); //assign paddle
    						buffer = ByteBuffer.wrap(paddle1_buffer.getBytes());
    						//   						buffer = ByteBuffer.wrap(
    						//       					        new String(charBuffer.toString()).getBytes()); 
    						System.out.println("RIGHT writing to ball, chanel 1 to 3 \n"+key1_3+"\n");
    						System.out.println("charBuffer is\n"+paddle1_buffer+"\n");
    						ball_channel.write(buffer);    						
    						//System.out.println("ret1="+ret1);
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    				}

    				if (key==p1Queue.elementAt(PLAYER2_ID)) //P2 coordinates
    				{

    					SelectionKey key2 = (SelectionKey)p1Queue.elementAt(PLAYER1_ID); //the cross change assignment 2 -> 1

    					client_channel_2 = (SocketChannel) key2.channel();
    					try {
    						String paddleX_buffer="X"+new String(charBuffer.toString()); //paddle protocol X123X124X125
    						buffer = ByteBuffer.wrap(
    								new String(paddleX_buffer.toString()).getBytes());
    						System.out.println("writing chanel 2 \n"+key2+"\n");
    						client_channel_2.write(buffer);
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}


    					////////////////  RIGHT  PADDLE  to ball/////////////////

    					SelectionKey key2_3 = p1Queue.elementAt(BALL_ID);
    					ball_channel = (SocketChannel) key2_3.channel();
    					try {
    						String paddle1_buffer="R"+new String(charBuffer.toString()); //assign paddle
    						buffer = ByteBuffer.wrap(paddle1_buffer.getBytes());
    						//   						buffer = ByteBuffer.wrap(
    						//       					        new String(charBuffer.toString()).getBytes()); 
    						System.out.println("LEFT writing to ball, chanel 1 to 3 \n"+key2_3+"\n");
    						System.out.println("charBuffer is\n"+paddle1_buffer+"\n");
    						ball_channel.write(buffer);    						
    						//System.out.println("ret1="+ret1);
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    				}
    				////////////////BALL to paddles////////////////////
    				
    				
    				if (key==p1Queue.elementAt(BALL_ID)) //ball coordinates
    				{

    					SelectionKey key1 = (SelectionKey)p1Queue.elementAt(PLAYER1_ID); //the cross change assignment 2 -> 1

    					client_channel_1 = (SocketChannel) key1.channel();
    					try {
    						String ball_buffer="B"+new String(charBuffer.toString()); //paddle protocol X123X124X125
    						buffer = ByteBuffer.wrap(
    								new String(ball_buffer.toString()).getBytes());
    						System.out.println("RIGHT writing ball coordinates to paddle 1  \n"+key1+"\n");
       						System.out.println("charBuffer is\n"+ball_buffer+"\n");  						
    						client_channel_1.write(buffer);
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}


    					////////////////  RIGHT  PADDLE  to ball/////////////////

    					SelectionKey key2 = p1Queue.elementAt(PLAYER2_ID);
    					client_channel_2 = (SocketChannel) key2.channel();
    					try {
    						String ball_buffer="B"+new String(charBuffer.toString()); //assign paddle
    						buffer = ByteBuffer.wrap(ball_buffer.getBytes());
    						//   						buffer = ByteBuffer.wrap(
    						//       					        new String(charBuffer.toString()).getBytes()); 
    		
    						System.out.println("LEFT writing  ball coordinates to paddle 2, \n"+key2+"\n");
    						System.out.println("charBuffer is\n"+ball_buffer+"\n");
    						client_channel_2 .write(buffer);    						
    						//System.out.println("ret1="+ret1);
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    				}
    				
    			} //gro\ufffde ife alle 4 dabei?
    			
                buffer.clear(); //!!!!!! important
    			continue;
    		}
    	}
    }
  }

  protected void init_ball()
  {
		int BUFFER_SIZE = 8;
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		
		SelectionKey key1 = (SelectionKey)p1Queue.elementAt(2); //the cross change assignment 2 -> 1

		client_channel_1 = (SocketChannel) key1.channel();
		try {
			String ball_buffer="B000"; //z
			buffer = ByteBuffer.wrap(
					new String(ball_buffer.toString()).getBytes());					
			client_channel_1.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		SelectionKey key2 = (SelectionKey)p1Queue.elementAt(3); //the cross change assignment 2 -> 1		
		client_channel_2 = (SocketChannel) key2.channel();
		try {
			String ball_buffer="B000"; //z
			buffer = ByteBuffer.wrap(
					new String(ball_buffer.toString()).getBytes());					
			client_channel_2.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	  
  }

 
  protected void remove_client_keys(SelectionKey key)
  {

	  if (key==p1Queue.elementAt(1))
	  {
		  p1Queue.removeElementAt(1);  //the 1 element to be removed than the 2 element will be the first
	  }
	  if (key==p1Queue.elementAt(2))
	  {
		  p1Queue.remove(2);           //cause that the last one will be removed
	  }
	  if (key==p1Queue.elementAt(3))
	  {
		  p1Queue.remove(3);            //cause that the last one will be removed
	  }
  }


  protected void finalize() {
	  if (port != null) {
		  try {
			  System.out.println("closing port"+port);
			  port.close(); 
		  }
		  catch (IOException e) {
			  System.out.println("Error closing port: " + e);
		  }
		  port = null;
	  }
  }
}
