package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server_main extends Thread {

	private ServerSocket serverSocket;
	
	private ExecutorService threadPool;
	
	public void init(int _port) throws Exception{
		
		serverSocket = new ServerSocket(_port);     
		
		threadPool = Executors.newFixedThreadPool(4);
	}
	
	public void run(){
		
		try{
			
			while(true){
		
				Socket socket = serverSocket.accept();
				
				System.out.println("ÕìÌýµ½Á¬½Ó");
			
				threadPool.execute(new Server_thread(socket));
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
	}
	
}
