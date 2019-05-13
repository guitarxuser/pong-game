package ponggame.ball;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Exchanger;


public class Ball extends Thread implements Runnable{
	
	Exchanger<String> exgr = new Exchanger<String>();
	
	float ball_x, ball_y, field_height=250,field_width=280;
	public static Vector<Integer> v = new Vector<Integer>();
	private static SocketAction  connection =new SocketAction(); //establish a new instance of Socket connection
    echo_srv udp_serv= new echo_srv(v);
 /////////////////////////////////////////////////////////////
 // pad
 	private final int SPEED = 1;
 	private int padH = 10, padW = 40;
 	private int n_padH = 26, n_padW = 10;
 	
 	private int bottomPadX, topPadX;
 
 	
 	private int inset = 10;
 	
 	// ball
 	private double  velX = 1, velY = 1, ballSize = 8;
 	
 	// score
 	private int scoreRight, scoreLeft;
 /////////////////////////////////////////////////////////////   
	public Ball(float x, float y) throws IOException {  
		connection.start(); //establish connection to server
	    udp_serv.start();
		ball_x=x;
		ball_y=y;
		
	}

	float speed=(float) 0.1,direction_x=1,direction_y=1;
	public static int local_player_x=10,local_player_y=100,remote_player_x=250,remote_player_y=100;
	private int countPlayer_remote=0;
	private int countPlayer_local=0;
	private String [] s_array_L= {"X101"};
	private String [] s_array_R= {"X101"};
	private boolean first_call=true;
	
	public void run() {
		for(;;)
		{
			try {
				new UseString (exgr);
				new getPaddleMove_left(exgr);
				new getPaddleMove_right(exgr);
				
	//			ballMovement ();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	//exchanger for paddles coordinates LEFT

	class getPaddleMove_left implements Runnable {
		Exchanger<String> ex;


		String str;
//		s_array_R[1]="L101";
		getPaddleMove_left(Exchanger<String> c) throws IOException {
			ex = c;
			String s = connection.receive();
			s = s.trim();
			if(s.contains("L"))
			{		
				s_array_L=s.split("L", s.length());
			}
			new Thread(this).start();
		}

		public void run() {

			if(s_array_L.length != 0)
			{

				try {
					for(int i =0; i < s_array_L.length ; i++)
					{
						//System.out.println(s_array_L[i]);
						ex.exchange("L"+s_array_L[i]);
					}

				} catch (InterruptedException exc) {
					System.out.println(exc);
				}
			}
		}
	}

	//exchanger for paddles coordinates LEFT

	class getPaddleMove_right implements Runnable {
		Exchanger<String> ex;

	

		String str;

		getPaddleMove_right(Exchanger<String> c) throws IOException {
			ex = c;
			String s = connection.receive();
			s = s.trim();
			if(s.contains("R"))
			{
				s_array_R=s.split("R", s.length());
			}
			new Thread(this).start();
		}

		public void run() {


			if (s_array_R.length !=0)
			{
				try {
					for(int i =0; i < s_array_R.length ; i++)
					{
					//	System.out.println(s_array_R[i]);
						ex.exchange("R"+s_array_R[i]);
					}

				} catch (InterruptedException exc) {
					System.out.println(exc);
				}
			}
		}

		// paddle movement coordinates got from server left\right


	}
	  class UseString implements Runnable {
		  Exchanger<String> ex;
	//	  bool first_call;
		  private String str="RX111";
		  private String position_y="1";
		  private String position_y_between="1";
		  UseString(Exchanger<String> c) {
			  ex = c;
			  new Thread(this).start();
		  }

		  public void run() {

			  try {
				 // if(first_call == TRUE)
					if(first_call==true) //first run flag
    				{
						first_call=false;
						connection.send(str);
    				}
				 
				  str = ex.exchange(new String());
				  if(str.length() >= 3)
				  {
	//			  System.out.println("Got: "+str.toString()+"\n");
				//  Thread.sleep(2);	  
				  ballMovement(); 
				  
				  }
			  } catch (InterruptedException | IOException exc) {
				  System.out.println(exc);
			  }
		  }
		  
			public void ballMovement() throws IOException {

				{

					// top/bottom walls
					if (ball_y < 0 || ball_y > field_height - ballSize) {
						velY = -velY;
					}
					
					// side walls
					if (ball_x < 0) {
						velX = -velX;
						++ scoreLeft;
					}
					
					if (ball_x + ballSize > field_width) {
						velX = -velX;
						++ scoreRight;
					}
				
					// bottom pad NOW LEFT
					if (ball_x + ballSize >= field_width - n_padW - inset && velX > 0)
						if (ball_y + ballSize >=  local_player_y && ball_y <= local_player_y + n_padH)
						{
						//	System.out.println("boing right pad");
						//	System.out.println("ball_y= "+ball_y+" ball_x= "+ball_x+"!!!!\n");
							velX = -velX;
						}
					// top pad NOW RIGHT
					
					if (ball_x <= n_padW + inset && velX < 0)
						if (ball_y + ballSize >= remote_player_y && ball_y <= remote_player_y + n_padH)
						{
						//	System.out.println("boing left pad");
						//	System.out.println("ballY= "+ball_y+" ball_x "+ball_x+"\n");
							velX = -velX;
						}
					ball_x += velX*speed;
					ball_y += velY*speed;
					
					
				//	int player_pos = 10;
					if(str.contains("R"))
					{
						System.out.println(str);

						position_y=str.substring(2);
						int pos =position_y.indexOf("X");
						if(pos <= 0)
						{
							System.out.println(position_y +"  " +str.length()+ " "+ pos);
							local_player_y=new Integer(str.substring(2)).intValue();

						}

						System.out.println(position_y +"  " +str.length()+ " "+ pos);

						if((pos == 2) && (position_y.length() <7))
						{

							try {
								//	local_player_y=new Integer(str.substring(2,pos+2)).intValue();
								local_player_y=new Integer(str.substring(2)).intValue();
							} catch (Exception e) {
								System.out.println(e);
								// TODO: handle exception
							}
						}
						
						if(pos==3)
						{
							position_y_between=position_y.substring(4);
							System.out.println("position_y_between"+position_y_between);
							
							try {
						
								System.out.println("position_y_between"+position_y_between);
								local_player_y=new Integer(position_y_between).intValue();
							} catch (Exception e) {
								System.out.println(e);
								// TODO: handle exception
							}
						}
					}

				    if(str.contains("L"))
				    {
				    	remote_player_y=new Integer(str.substring(2)).intValue();
				    }


					synchronized(v){
						v.clear();

						int ball_x_pos=(int)(ball_x);
						int ball_y_pos=(int)(ball_y);
						String s_ball_x = (new Integer(ball_x_pos)).toString();
						String s_ball_y = (new Integer(ball_y_pos)).toString();	
						String ball_xy_str=s_ball_x+";"+s_ball_y;

				
						echo_srv.setS_ball_x_to_send(s_ball_x+";"+s_ball_y+";"+scoreLeft+";"+scoreRight);
				//		echo_srv.setS_ball_x_to_send(s_ball_x+";"+s_ball_y);
				//		System.out.println("ball_xy_str is "+  ball_xy_str);
						v.add(1);
						
		                v.notify();
					}
					
				}
	  }
	  }
}