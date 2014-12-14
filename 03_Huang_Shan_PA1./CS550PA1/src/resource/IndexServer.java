package resource;

import java.net.*;
import  java.io.*;


/**
 * This interface is Index-Server. inplementing multi-thread.
 * @author shanhuang
 *
 */


@SuppressWarnings("serial")
public class IndexServer implements Serializable{
	
	
	
	int PortNumber;
	String location;
	Socket socket;
	
	//constructor
	public IndexServer(int PortNumber, String location){
		this.PortNumber=PortNumber;
		this.location=location;
		
	}
	
    public void ConnectionAccept(){
	ServerSocket serverlisten;
	try {
		serverlisten = new ServerSocket(PortNumber);
	
		while(true)	{
		socket = serverlisten.accept();
		ConnectionHandler(socket,location);
		}

	}catch(Exception e){
		System.out.println("Socket Binds unsuccessfully!  "+ e);
	}
	
    }
	
	
	public void ConnectionHandler(Socket handler, String location) throws Exception{
		new Thread(new HandleConnection(handler,location)).start();
	}
	

}
