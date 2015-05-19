package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import data.dataDB.user.DB_user;

public class Server_thread extends Thread {

	private Socket socket;
	
	private BufferedReader reader;
	private DataOutputStream writer;
	
	private ArrayList<String> strVec;
	private Server_thread_service service;
	
	private static String end = "end";
	
	private boolean isDisconnected;
	
	public Server_thread(Socket _socket){
		
		socket = _socket;
		
		strVec = new ArrayList<>();
		
		try{
		
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new DataOutputStream(socket.getOutputStream());
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
	}
	
	public void sendData(String _str) throws Exception{
		
		if(isDisconnected){
			
			return;
		}
		
		writer.writeInt(_str.length());
		
		writer.writeBytes(_str);
		
		writer.flush();
	}
	
	public void kick(){
		
		service = null;
	}
	
	public void run(){
		
		while(true){
			
			try{
				
				String str = reader.readLine();
				
				if(str == null){
					
					System.out.println("close!!!" + Thread.currentThread().getName());
					
					disconnect();
					
					break;
					
				}else{
					
					if(str.equals(end)){
						
						String[] strArr = new String[strVec.size()];
						
						strVec.toArray(strArr);
						
						strVec.clear();
						
						if(service != null){
						
							service.process("getData", (Object)strArr);
							
						}else{
							
							if(strArr.length == 3 && strArr[0].equals("0")){
							
								service = DB_user.login(strArr[1], strArr[2]);
								
								if(service != null){
									
									if(service.hasThread()){
										
										service.process("kick", this);
										
									}else{
									
										service.process("connect", this);
									}
									
								}else{
									
									sendData("1\nfalse");
								}
								
							}else{
								
								sendData("999\nPlease login first");
							}
						}
						
					}else{
						
						strVec.add(str);
					}
				}
				
			}catch(Exception e){
				
				e.printStackTrace();
				
				try{
				
					disconnect();
					
				}catch(Exception ee){
					
					ee.printStackTrace();
				}
				
				break;
			}
		}
	}
	
	private void disconnect() throws Exception{
		
		if(service != null){
			
			service.process("disconnect");
			
			service = null;
		}
		
		isDisconnected = true;
		
		reader.close();
		
		writer.close();
		
		socket.close();
	}
}
