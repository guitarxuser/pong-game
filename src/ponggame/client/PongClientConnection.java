package ponggame.client;
// Imports
import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Exchanger;
import java.applet.*;

import javax.swing.JApplet;

class PongClientConnection extends SocketAction {
  static final int PORTNUM = 1234;
  static final int ERROR = -1;
  static final int PLSWAIT = -2;
  static final int YOURTURN = -3;
  static final int THEIRTURN = -4;
  static final int THEYWON = -5;
  static final int THEYQUIT = -6;
  static final int THEYTIED = -7;
  static final int GAMEOVER = -8;
  private static  SocketChannel SocketChannel = null;
  private static SocketChannel port ;
  private static PongFrame a_out;
  
  public PongClientConnection(PongFrame pongFrame)  {
      super(a_out);
      a_out=pongFrame;
 // public PongClientConnection()  {
      //a.getCodeBase().getHost().
	 // port.socket().bind(new java.net.InetSocketAddress(1234));
	 // super(port);
    
  }
 

public int getTheirMove() {
    // Make sure we're still connected
   // if (!isConnected()) 
   //   throw new NullPointerException("Attempted to read closed socket! bubu");
    
    try {
      String s = receive();
      String s_out;
      
      int three_or_two=1;// 1 means thre digits
      
      if (s == null)
        return GAMEOVER;
      s = s.trim();
      //handling for multiplied sended y coordinates
      int length =s.length();    
      
      if (length > 3)
      {
    	
    	System.out.println("client Received from server: " + s);
    	System.out.println("length-2 :"+ new Integer(s.substring(length-2)).intValue() );
    	System.out.println("length-4 :"+ new Integer(s.substring(length-4)).intValue() );
    	//three_or_two=length %2; //modulo
    	if(new Integer(s.substring(length-2)).intValue() == new Integer(s.substring((length-4),length-2)).intValue()-3)
    	{
    		s_out=s.substring((length-2));
    	}
    	else if(new Integer(s.substring(length-2)).intValue() == new Integer(s.substring(length-4,length-2)).intValue()+3)
    	{
    		s_out=s.substring((length-2));
    	}
    	else
    	{
    		s_out=s.substring((length-3));
    	}   	
   
    	System.out.println("length is"+length);
    	System.out.println("substr is "+s_out);
    	
      }
      else
      {
      s_out=s;
      //  System.out.println("length is"+length);
      // System.out.println("substr is "+s_out);
      }
      
      //s_out=s;
      try {
        return (new Integer(s_out)).intValue();
      }
      
      catch (NumberFormatException e) {
        // It was probably a status report error
        return getStatus(s);
      }
    }
    catch (IOException e) {
      System.out.println("I/O Error client: " + e);
      System.exit(1);
      return 0;
    }
  }

  private int getStatus(String s) {
    s = s.trim();
    if (s.startsWith("PLSWAIT"))
      return PLSWAIT;
    if (s.startsWith("THEIRTURN"))
      return THEIRTURN;
    if (s.startsWith("YOURTURN"))
      return YOURTURN;
    if (s.startsWith("THEYWON"))
      return THEYWON;
    
    if (s.startsWith("THEYQUIT"))
      return THEYQUIT;
    if (s.startsWith("THEYTIED"))
      return THEYTIED;
    if (s.startsWith("GAMEOVER"))
      return GAMEOVER;

    // Something has gone horribly wrong!
  // System.out.println("received invalid status from server: " + s);
    return ERROR;
  }

  public void sendMove(int col) {
    String s = (new Integer(col)).toString();
   String s_assigned = "X"+s;
   send(s_assigned);
  }

  public void sendIQUIT() {
    send("IQUIT");
  }

  public void sendIWON() {
    send("IWON");
  }

  public void sendITIED() {
    send("ITIED");
  }
  public void sendYOURTURN() {
	    send("YOURTURN");
	  }
  public void sendSENTSTRING() {
	    send("SENTSTRING");
	  }

//exchanger for paddles coordinates LEFT


}


