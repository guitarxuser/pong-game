package ponggame.client;

import java.awt.Choice;
import java.awt.TextField;
import java.awt.Button;
import java.awt.event.*;
import java.awt.FlowLayout;	

import javax.swing.*;

import ponggame.ball.Ball;
import ponggame.server.PongDaemon;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class Mywin extends javax.swing.JFrame 
{
	private static final int SIZE_X = 650;
	private static final int SIZE_Y = 400 ;
	private static PongFrame mypong;
	private static boolean waiting=true;
	public static String orientation="left";
	private static Vector<Integer> v= new Vector<Integer>(); 
	TextField tfe1,tfe2;
	Button bt_write;
	public static Choice auswahl;
	public static String tcp_ip_str;

	//Button bt_read;
	public static JTextArea t1,t2 ;

	
	public final static String newline = "\n";
	public static JScrollPane scrollPane;
    
	
	public Mywin(){
		//geometry

		setLayout(new FlowLayout());
		setSize(SIZE_X,SIZE_Y);
		//window components
		t1 = new JTextArea("",15, 50 );
		//t2 = new JTextArea("",15, 30 );
		add(scrollPane= new JScrollPane(t1)); 	
		add(tfe1=new TextField(20)); //tcp ip address input 
		add(tfe2=new TextField(50));
		
		add(bt_write=new Button("Senden"));
		add(auswahl=new Choice() );
	
	
		//The  choice of paddles implementation
		auswahl.add("<choose the paddle >");
		auswahl.add("LEFT PADDLE");
		auswahl.add("RIGHT PADDLE");
		
		PongWin app = new  PongWin();
		app.start();

		ItemListener auswahl_act = new ItemListener(){

			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				System.out.println(auswahl.getSelectedItem());
				synchronized(v){
					v.clear();
					if( auswahl.getSelectedItem().equals("<choose the paddle >")){
						System.out.println("do noting");
					}
					if( auswahl.getSelectedItem().equals("LEFT PADDLE")){
						System.out.println("lefti");
						Mywin.orientation="left";		
						v.notify();
				
					}
					if( auswahl.getSelectedItem().equals("RIGHT PADDLE")){
						System.out.println("righti");
						Mywin.orientation="right";	
						v.notify();
					}
				}
			}
		};
			
		   tfe1.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                tfe1ActionPerformed(evt);
	            }

				private void tfe1ActionPerformed(ActionEvent evt) {
					// TODO Auto-generated method stub
					   tcp_ip_str=tfe1.getText();
					   tfe1.setText("");
					   new PongFrame(orientation);
					 
					
				}
	        });
		   
		   tfe2.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                tfe2ActionPerformed(evt);
	            }

				private void tfe2ActionPerformed(ActionEvent evt) {
					// TODO Auto-generated method stub
					
				}
	        });


		auswahl.addItemListener(auswahl_act);
		
		//exit assurance
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(final WindowEvent e){
				dispose();
				System.exit(0);
			}
		});
		
		
	
		
	}
		

	///////////////////////////////////////////////////////////////////////
	//Mywin end
	///////////////////////////////////////////////////////////////////////

	

	//put the scrollbar allways at the actually end of text area
	public static void put_bar_at_end()
	{
		final Point point = new Point( 0, (int)(Mywin.t1.getSize().getHeight()) );
		scrollPane.getViewport().setViewPosition( point );
	}
	
	public void actionPerformed(final ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public class PongWin extends Thread
	{
		public  void run ()
		{
			//while(true){
			synchronized(v){

				try {
					v.wait();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (Mywin.orientation=="left") {
				System.out.println("Pong server up and running...");
			    try {
					new PongDaemon().start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 System.out.println("Ball up and running...");
				    try {
						new Ball(1,1).start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					new PongFrame(orientation);
					v.clear();
				} catch (Exception e2) {
				//	System.exit(0);
					e2.printStackTrace();
					 JOptionPane.showMessageDialog(null,"Server Might Be Down!","Warning",JOptionPane.WARNING_MESSAGE);
					// TODO: handle exception
				}
				

			}
			//}
		}
	}
	
	

}


