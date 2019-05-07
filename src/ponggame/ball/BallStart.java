package ponggame.ball;

import java.io.IOException;


public class BallStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		 System.out.println("Ball up and running...");
		    new Ball(1,1).start();
	}

}
