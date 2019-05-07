package ponggame.server;

import java.io.IOException;


class PongServer {
  public static void main(String args[]) {
    System.out.println("Pong server up and running...");
    try {
		new PongDaemon().start();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
