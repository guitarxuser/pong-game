package ponggame.client;

import java.io.IOException;

import ponggame.ball.Ball;
import ponggame.server.PongDaemon;;

public class app_start {
//	PongDaemon pong = new PongDaemon();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		System.out.println("Pong server up and running...");
//	    try {
//			new PongDaemon().start();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 System.out.println("Ball up and running...");
//		    try {
//				new Ball(1,1).start();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		Mywin myser = new Mywin();    
		myser.setVisible(true); 

	}

}
