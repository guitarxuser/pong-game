package ponggame.client;


import java.awt.Color;
import java.util.Vector;
import java.util.concurrent.Exchanger;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.DisplayMode;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

//import ponggame.ball.Ball.UseString;
//import ponggame.ball.Ball.getPaddleMove_left;




import java.awt.image.BufferStrategy;




public class PongItems extends JFrame{

	
	public static Dimension currentWindowSize;
	public static  String ball_xy_remote_str;
	float ball_x=10, ball_y=100;
	float remote_ball_x=10, remote_ball_y=100;
	public static int left_score, right_score;
	public  static int local_player_x=10,local_player_y=100,remote_player_x=250,remote_player_y=100;
	int offset_local_player=3,offset_remote_player=3;
	float speed=3,direction_x=1,direction_y=1;
	
	private int y_old_1, y_old_2;
	private ConcurrentLinkedQueue<Integer> racket;
	private ConcurrentLinkedQueue<String> Bal_coordinates;
	private String orientation;
	
	BufferStrategy bufferStrategy;
	public static int remote_player_y_received=111;
	
	protected static Vector<Integer> v= new Vector<Integer>(); 
	udp_cli udp_client;
		
	public PongItems(Vector<Integer> v, 
			         String orientation ,
    		         ConcurrentLinkedQueue<Integer> racket_in)	
			          {
		
	    this.orientation=orientation;
	    this.racket=racket_in;

		PongItems.v=v;  
  
		if (orientation=="right")
		{
			local_player_x=250;
			remote_player_x=10;
		}
		if (orientation=="left")
		{
			local_player_x=10;
			remote_player_x=250;
		}
		try {
			udp_client= new udp_cli(v);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        udp_client.start();
	}

	

	public Dimension getPreferredSize() {
		return new Dimension(280,250);
	}



	public class Ball extends Figure implements Runnable{
		
		String ball_telegram_remote_str ;
		String sub_str;
		String ball_x_remote_str;
		String ball_y_remote_str;
		String ball_xy_remote_str ;
		String left_score_str;
		String right_score_str;
		
		public Ball(int x, int y, Color color) {
			super(x, y, color);
			ball_xy_remote_str=PongItems.ball_xy_remote_str; //faster than getter/setter
		//	ball_xy_remote_str=udp_cli.getResponse();
			// TODO Auto-generated constructor stub
		}
		
//		public void positionBall(int x, int y){
//			ball_x=x;
//			ball_y=y;
//		}
		
//		public void ballMovement (Vector<Integer> v, ConcurrentLinkedQueue<String> Bal_coordinates )
		public void ballMovement (Vector<Integer> v)
		{   
			     
			synchronized(v){
				try {
					v.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	

                
				ball_telegram_remote_str=udp_cli.response;

			//	System.out.println("ball_telegram_remote_str is "+ball_telegram_remote_str);
				         
	
				int pos = ball_telegram_remote_str.indexOf( ";" );
			//	System.out.println("ball_telegram_remote_str.substring(0,pos) is "+ball_telegram_remote_str.substring(0,pos));
		    //  System.out.println("ball_telegram_remote_str.substring(pos+1) is "+ball_telegram_remote_str.substring(pos+1));
				ball_x_remote_str=ball_telegram_remote_str.substring(0,pos);
				sub_str=ball_telegram_remote_str.substring(pos+1);
				pos = sub_str.indexOf(";");
				ball_y_remote_str=sub_str.substring(0,pos);
				
				sub_str=sub_str.substring(pos+1);
				pos = sub_str.indexOf(";");
				left_score_str =sub_str.substring(0,pos);
			//	System.out.println("left_score_str"+left_score_str);
			    left_score= Integer.parseInt(left_score_str);
			    
			    right_score_str=sub_str.substring(pos+1);
			//    System.out.println("right_score"+right_score_str);
			    right_score= Integer.parseInt(right_score_str);
			    
				
				ball_xy_remote_str=ball_x_remote_str+ball_y_remote_str;
				
			//	System.out.println("ball_x_remote_str is"+ball_x_remote_str);
			//	System.out.println("ball_y_remote_str is"+ball_y_remote_str);
				
				
		         
				if(pos >= 0)
				{
					remote_ball_x=Float.parseFloat(ball_x_remote_str);
					remote_ball_y=Float.parseFloat(ball_y_remote_str);
				}
				v.clear();
			}

		}


		@Override
		void drawInternal(Graphics2D g) {
			// TODO Auto-generated method stub
			// Draw ball
			// L O C A L / R E M O T E
			g.setColor(Color.RED);
			g.fillOval((int)remote_ball_x, (int)remote_ball_y, 8,8);
//			g.fillOval((int)ball_x, (int)ball_y, 8,8);



		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}

	}
//////////////////////////////////////////////////////////////////////////////////
	/*            Class local_Player begins her             */

	public class local_Player extends Figure {

		public local_Player(int x, int y,Color color) {
			super(x, y,color);
			// TODO Auto-generated constructor stub
		}


		// redraw player 1 on Position X Y 
		public void positionPlayer_local(int x, int y){
			local_player_x=x;
			local_player_y=y;

			//System.out.println("y for racket "+y);
			if(y_old_1!=y)
			{  		 
				racket.offer(y);
				PongFrame.connection.sendMove(racket.element());
			
				//   		PongApplet.connection.sendYOURTURN();
				//System.out.println(racket1);
				System.out.println("sending"+racket.element());
			}
			
			
			y_old_1=y;
			//cleanup the que
			if(racket.size()>=2)
			{
				racket.remove();
			}
			// drawPlayer(); 
			//repaint();
			//ex_string=String.valueOf(y);

		}
		
		
		
		// Move player 1
		public void Player_movement_local()
		{
			if (PongWorker.local_player_up == false && local_player_y <= (getPreferredSize().height-25))
				local_player_y=local_player_y + offset_local_player;

			if (PongWorker.local_player_down == false && local_player_y >= 0)
				local_player_y = local_player_y - offset_local_player;
			    positionPlayer_local(local_player_x, local_player_y);
			//   drawPlayer();
		}
		
		
		@Override
		void drawInternal(Graphics2D g) {

			//g.setColor(Color.blue);
			g.fillRect(local_player_x,local_player_y, 10, 26);			
			// TODO Auto-generated method stub

		}	
	}


	/* Class local_Player end */
///////////////////////////////////////////////////////////////////////////////
	public class remote_Player extends Figure {

		public remote_Player(int x, int y,Color color) {
			super(x, y,color);
			// TODO Auto-generated constructor stub
		}


		
		
		// redraw player 2 on pos X Y  
		public void positionPlayer_remote(int x, int y){
			remote_player_x=x;
			remote_player_y=y;
			//drawPlayer();
			//repaint();
		}
		// Move player 1
		
		
		public void Player_movement_remote(int y_cord)
		{
			remote_player_y=y_cord; 
			positionPlayer_remote(remote_player_x, remote_player_y);
		}

		@Override
		void drawInternal(Graphics2D g) {

			//g.setColor(Color.blue);
		
			g.fillRect(remote_player_x,remote_player_y, 10, 26);
			// TODO Auto-generated method stub

		}	
	}


	/* Class remote_Player end */


	public class score extends Figure implements Runnable{
		private String str;
	
		public score(int x, int y,Color color) {
			super(x, y,color);

		}
		public void score_dynam(int score_count,int x,int y,Color color)
		{
			// scores		
			str=String.valueOf(score_count);
	//		System.out.println("score_count is "+score_count);
	//		str=String.valueOf(score_count);
	//		System.out.println("str is "+str);
		}
		@Override
		void drawInternal(Graphics2D g) {
			g.setColor(Color.cyan);
			g.drawString(str,x,y);
			// TODO Auto-generated method stub

		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}	
	}


/* Class score end */
	
	abstract class Figure {
		Color color;

		int x;
		int y;


		public Figure(int x, int y, Color color) {
			this.color = color;
			this.x = x;
			this.y = y;
		}

		public void drawOn(Graphics2D g) {
			Color oldColor = g.getColor();
			g.setColor(color);
			drawInternal(g);
			g.setColor(oldColor);
		}

		abstract void drawInternal(Graphics2D g);
	}
	

		
}



