package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import data.dataDB.user.DB_user;

public class Server_thread extends Thread {

	private static int CHAR_MAX_LENGTH = 1024;
	
	private Socket socket;
	
	private DataInputStream reader;
	private DataOutputStream writer;
	
	private Server_thread_service service;
	
	private boolean isDisconnected;
	
	private int length = -1;
	private int pos = 0;
	private byte[] resultByteArr;
	private byte[] tmpByteArr;
	
	public Server_thread(Socket _socket){
		
		socket = _socket;
		
		resultByteArr = new byte[CHAR_MAX_LENGTH];
		tmpByteArr = new byte[CHAR_MAX_LENGTH];
		
		try{
		
			reader = new DataInputStream(socket.getInputStream());
			writer = new DataOutputStream(socket.getOutputStream());
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
	}
	
	public void sendData(String _str) throws Exception{
		
		if(isDisconnected){
			
			return;
		}
		
		writer.writeShort(_str.length());
		
		writer.writeBytes(_str);
		
		writer.flush();
	}
	
	public void kick(){
		
		service = null;
	}
	
	public void run(){
		
		while(true){
			
			try{
				
				if(length == -1){
					
					length = reader.readShort();
					
					if(length > CHAR_MAX_LENGTH){
						
						disconnect();
						
						break;
					}
					
				}else{
					
					int result = reader.read(tmpByteArr, 0, length);
					
					for(int i = 0 ; i < result ; i++){
						
						resultByteArr[pos] = tmpByteArr[i];
						
						pos++;
					}
					
					if(result < length){
						
						length = length - result;
						
					}else{
						
						String str = new String(resultByteArr, 0, pos);

						pos = 0;
					
						length = -1;
						
						if(service != null){
						
							service.process("getData", str);
							
						}else{
							
							String[] strArr = str.split(Server_thread_service.REGEX_SPLIT);
							
							if(strArr.length == 3 && strArr[0].equals("0")){
							
								service = DB_user.login(strArr[1], strArr[2]);
								
								if(service != null){
									
									if(service.hasThread()){
										
										service.process("kick", this);
										
									}else{
									
										service.process("connect", this);
									}
									
								}else{
									
									sendData("1" + Server_thread_service.REGEX_CONCAT + "false");
								}
								
							}else{
								
								sendData("999" + Server_thread_service.REGEX_CONCAT + "Please login first");
							}
						}
					}
				}
				
			}catch(Exception e){
				
//				e.printStackTrace();
				
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
		
		length = -1;
		
		pos = 0;
	}
}
