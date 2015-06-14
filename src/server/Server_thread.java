package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import publicTools.PublicTools;
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
	
	public Server_thread(Socket _socket){
		
		socket = _socket;
		
		resultByteArr = new byte[CHAR_MAX_LENGTH];
		
		try{
		
			reader = new DataInputStream(socket.getInputStream());
			writer = new DataOutputStream(socket.getOutputStream());
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
	}
	
	public void sendData(int _id,String[] _strVec) throws Exception{
		
		if(isDisconnected){
			
			return;
		}
		
		int length = 4;
		
		for(int i = 0 ; i < _strVec.length ; i++){
			
			length = length + 2 + _strVec[i].length();
		}
		
//		System.out.println("准备发包  长度:" + length + " 包编号:" + _id);
		
		writer.writeShort(length);
		
		writer.writeShort(_id);
		
		writer.writeShort(_strVec.length);
		
		for(int i = 0 ; i < _strVec.length ; i++){
			
			String str = _strVec[i];
			
			writer.writeShort(str.length());
			
			writer.writeBytes(str);
		}
		
//		System.out.println("发包  长度:" + writer.size());
		
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
					
					int result = reader.read(resultByteArr, pos, length);
					
					pos = pos + result;
					
					if(result < length){
						
						length = length - result;
						
					}else{
						
						pos = 0;
						
						int id = PublicTools.byteArrayToShort(resultByteArr, pos);
						
						pos = pos + 2;
						
						length = PublicTools.byteArrayToShort(resultByteArr, pos);
						
						pos = pos + 2;
						
						String[] strVec = new String[length];
						
						for(int i = 0 ; i < length ; i++){
							
							int tmpLength = PublicTools.byteArrayToShort(resultByteArr, pos);
							
							pos = pos + 2;
							
							strVec[i] = new String(resultByteArr, pos, tmpLength);
							
							pos = pos + tmpLength;
						}
						
						pos = 0;
					
						length = -1;
						
						if(service != null){
						
							service.process("getData", id, strVec);
							
						}else{
							
							if(id == 0 && strVec.length == 2){
							
								service = DB_user.login(strVec[0], strVec[1]);
								
								if(service != null){
									
									if(service.hasThread()){
										
										service.process("kick", this);
										
									}else{
									
										service.process("connect", this);
									}
									
								}else{
									
									sendData(1, new String[]{"false"});
								}
								
							}else{
								
								sendData(999, new String[]{"Please login first"});
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
