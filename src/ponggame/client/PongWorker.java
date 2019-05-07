package ponggame.client;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
//import java.util.Timer;
//import java.util.TimerTask;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import ponggame.client.PongItems.Ball;
import ponggame.client.PongItems.local_Player;
import ponggame.client.PongItems.remote_Player;
import ponggame.client.PongItems.score;




public class PongWorker extends JFrame {
	
	 private Timer timer; 
	 
    
  //   private local_Player playerLocal= new PongItems.local_Player(100, 100,Color.blue);
  //   private remote_Player playerRemote;
  //   private Ball ball;
     public static final int DELAY = 15;
	
	// private static ConcurrentLinkedQueue<Integer> racket1;
    // private static ConcurrentLinkedQueue<Integer> racket2;
     protected static String orientation="";
     
 	 private local_Player  playerLocal;
     private remote_Player playerRemote;
     private Ball ball; 
     private score score_local;
     private score score_remote;
     public static boolean local_player_up,remote_player_up,local_player_down,remote_player_down;
     public static ConcurrentLinkedQueue<String> Bal_coordinates=new ConcurrentLinkedQueue<String>();
     public static ConcurrentLinkedQueue<Integer> racket = new ConcurrentLinkedQueue<Integer>();
 //	 protected static ConcurrentLinkedQueue<Integer> racket2 = new ConcurrentLinkedQueue<Integer>();
     public static Vector<Integer> v= new Vector<Integer>();
     
     private static Exchanger<String> ex = new Exchanger<String>();
//     public static Vector<Integer> v= new Vector<Integer>(); 
     
//     udp_cli udp_client;
//	 PongItems pong_items;
//	 Graphics2D g;
     
	 BufferStrategy bufferStrategy;
	 private PongItems pong_Items;
	
	 public PongWorker(String orientation) {
		
		 PongWorker.orientation=orientation;
	     setIgnoreRepaint(true);
	     setVisible(true);
	     createBufferStrategy(3);
		 bufferStrategy= getBufferStrategy();;
		 //pong_Items= new PongItems(orientation);
		 pong_Items= new PongItems(v,orientation,racket);
		
		 game_worker game=new game_worker();
		//    new PongItems();	
	       
	        
	      
	    	initPanel();
			bufferStrategy.show();
	       
	        
	        game.start();
	    }
	
	 private void initPanel() {
	

  		    playerLocal =  pong_Items.new local_Player(100, 100,Color.blue);
		    playerRemote = pong_Items.new remote_Player(100, 100,Color.blue);		    
            ball= pong_Items.new Ball(100, 100,Color.red);
     
       	    score_local=pong_Items.new score(30,50,Color.cyan);
       	    score_remote=pong_Items.new score(240,50,Color.cyan);

            setTitle(" Pong Game  paddle "+ PongWorker.orientation);	
    		
 
		    setBackground(Color.BLACK);
		    addKeyListener(new mykeyAdapter());
	        setFocusable(true);

	        setVisible(true);

	        
	    }
	 
	 public Dimension getPreferredSize() {
			return new Dimension(280,250);
		}  

	   private void drawScene() {
		   //global definition

		   //	    	Graphics2D g2d = (Graphics2D) g;
		   Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

		   //g=(Graphics2D) bufferStrategy.getDrawGraphics();
		   drawBackground(g2d);
		   drawBall(g2d);
		   drawPlayerLocal(g2d);
		   drawPlayerRemote(g2d);
		   drawScore_local(g2d);
		   drawScore_remote(g2d);
		   g2d.dispose();	  
		   Toolkit.getDefaultToolkit().sync();
		   bufferStrategy.show();
	   }

	   private void drawPlayerLocal(Graphics2D g) {
		   playerLocal.drawOn(g);
	   }
	   private void drawPlayerRemote(Graphics2D g) {
		   playerRemote.drawOn(g);
	   }
	   private void drawBall(Graphics2D g) {
		   ball.drawOn(g);
	   }
	   private void drawScore_local(Graphics2D g) {
		   score_local.drawOn(g);
	   }
	   private void drawScore_remote(Graphics2D g) {
		   score_remote.drawOn(g);
	   }

	   private void drawBackground(Graphics2D g) {

		   g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
	   }


	
	   private class mykeyAdapter extends KeyAdapter {
			public void keyPressed(KeyEvent evt)
			{

				switch(evt.getKeyCode())
				{
				// Move ship 1
				case KeyEvent.VK_W :
					local_player_up = true;
					break;
				case KeyEvent.VK_S : 
					local_player_down = true;
					break;

					// Move ship 2
				case KeyEvent.VK_UP:
					remote_player_up=true;
					System.out.println("up pressed\n");
					break;
				case KeyEvent.VK_DOWN:
					remote_player_down=true;
					break;
				}
			}
			public void keyReleased(KeyEvent evt)
			{
				switch(evt.getKeyCode())
				{
				// Mover Nave1
				case KeyEvent.VK_W :
					local_player_up = false;
					break;
				case KeyEvent.VK_S : 
					local_player_down = false;
					break;

					// Mover nave 2
				case KeyEvent.VK_UP:
					remote_player_up=false;
					break;
				case KeyEvent.VK_DOWN:
					remote_player_down=false;
					break;
				}
			}
		}
	
	
	public class UseString_from_paddle implements Runnable {

		private Exchanger<String> ex;

		private String str="111";


		public UseString_from_paddle(Exchanger<String> c) {
			ex = c;
			new Thread(this).start();
		}

		public void run() {

			try {

				str = ex.exchange(new String());
				if(str.length() != 0 && str != " ")
				{
					System.out.println("Got by paddle: "+str.toString()+"\n");

					//				  ballMovement();


					PongItems.remote_player_y_received=new Integer(str).intValue();

				}
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
		}
	}
	
	public class getPaddleMove_remote implements Runnable {
		
		Exchanger<String> ex;
		String [] s_array_X;
//		String s_out="111";


		//handling for multiplied sended y coordinates


		getPaddleMove_remote(Exchanger<String> c) throws IOException {
			ex = c;
			String s = PongFrame.connection.receive();
			if (s != "error")
			{
				s = s.trim();
				if(s.contains("X"))
				{
					s_array_X=s.split("X", s.length());

					new Thread(this).start();
				}
			}
			else {
				System.out.println("channel not readable");
			}
		}


		public void run() {

			try {
				for(int i =0; i < s_array_X.length ; i++)
				{
					//	System.out.println(s_array_L[i]);
					ex.exchange(s_array_X[i]);
				}

			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
		}

		// paddle movement coordinates got from server left\right

	}
	
	public class game_worker extends Thread {
	//PongItems items=new PongItems();
	private static final long FRAME_DELAY = 20; // 20ms. implies 50fps (1000/20) = 50
	
	private long  cycleTime;
	private int y_received_left=100;
	private int y_received_right=100;

//	Dimension currentWindowSize = PongWorker.getSize();
	
	
	
	public game_worker() {
		
		//Bal_coordinates=udp_cli.bal_coordinates;
	//	v=PongItems.v;
	
		// TODO Auto-generated constructor stub
	//	Exchanger<String> ex = new Exchanger<String>();
	}
	
	
	private void synchFramerate() {
		cycleTime = cycleTime + FRAME_DELAY;
		//System.out.println("cycleTime"+cycleTime);
		long difference = cycleTime - System.currentTimeMillis();
		//System.out.println("difference"+difference);
		try {
			Thread.sleep(Math.max(0, difference));
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void processKeys() {
		if (local_player_up) {

			playerLocal.y = PongItems.local_player_y;
		}

		if (local_player_down) {
			playerLocal.y  = PongItems.local_player_y;
		}

		
	}




		 
	public void run() {
		try {
			cycleTime = System.currentTimeMillis();
			while(true)
			{ 			

			    	new UseString_from_paddle(PongWorker.ex);
		     	    new getPaddleMove_remote(PongWorker.ex);		
	
//					ball.ballMovement(v,Bal_coordinates);
		     	    ball.ballMovement(v);
					playerLocal.Player_movement_local();
					playerRemote.Player_movement_remote(PongItems.remote_player_y_received);
					score_local.score_dynam(PongItems.left_score,30,50,Color.cyan);
     				score_remote.score_dynam(PongItems.right_score,240,50,Color.cyan);
					
					processKeys();						
					synchFramerate();
				    drawScene();
//					repaint();

			}
		}
		catch (Exception e) {
			System.out.println("Worker : "+e);
			e.printStackTrace();
			// TODO: handle exception
		}

	}

}

	
}

