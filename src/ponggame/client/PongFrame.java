package ponggame.client;




import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;

import java.io.*;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;



public class PongFrame  extends JFrame{

	//Background task for loading images.
	//network variables
	public boolean isFocusTraversable(){return true;}
	public static boolean channel_readable=true;
	private String          status = new String("Connecting...");
	private boolean         gameOver = true, myMove;
	private int             gameState = 0;
	public static PongClientConnection  connection = null;
	public static BufferStrategy bufferStrategy;
//	Exchanger<String> exgr = new Exchanger<String>();
	protected static  ConcurrentLinkedQueue<Integer> racket_1 =new ConcurrentLinkedQueue<Integer>();
	protected static  ConcurrentLinkedQueue<Integer> racket_2 =new ConcurrentLinkedQueue<Integer>();
	private int col;
	private int old_racket_y;
//	udp_cli udp_client;
	
	private static String orientation="default";
	
	public PongFrame(String orientation) {
		
	    PongFrame.setOrientation(orientation);
		establish_connection connectivity =new establish_connection();
		connectivity.start();
		//Execute a job on the event-dispatching thread; creating this  GUI.
		if(PongFrame.channel_readable==true)
		{

			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {

						createAndShowGUI();

					}
				});
			} catch (Exception e) {
				System.err.println("createGUI didn't complete successfully "+ e);
			}
		}
		else
		{
			 JOptionPane.showMessageDialog(null,"Server Might Be Down! try remote one","Warning",JOptionPane.WARNING_MESSAGE);
		}
		
	}
	private void getMinimumSize(int i, int j) {
		// TODO Auto-generated method stub

	}

	//put app and network together
	    
	    public void connect_client() throws IOException{
			connection = new PongClientConnection(this);
			
		}

	    public class establish_connection extends Thread{
	    	
	    	String in_str="10";
	    	 establish_connection() {
				// TODO Auto-generated constructor stub
	    		System.out.println("connection to be establish\n");
	    	    ////establish connection
	            	try {
	    				connect_client();
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    			
	    	    }
	
			}
	  
	////connection	

		public static void createAndShowGUI() {
			
			   System.out.println("Created GUI on EDT? "+
				        SwingUtilities.isEventDispatchThread());

	        JFrame f = new PongWorker(getOrientation());
	        
	       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

	        f.pack();

        f.setVisible(true);
	        return;
	    }
		public static String getOrientation() {
			String orientation=PongFrame.orientation;
			return orientation;
		}
		public static void setOrientation(String orientation) {
			PongFrame.orientation = orientation;
		}

	    
}
		

