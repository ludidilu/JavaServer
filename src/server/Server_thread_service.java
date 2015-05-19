package server;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import panel.Panel;
import superService.SuperService;
import data.dataCsv.connect.Csv_connect;
import data.dataDB.user.DB_user_unit;

public class Server_thread_service extends SuperService{

	protected static Map<String, Method> methodMap;
	
	private static HashMap<String, Class<?>> clsMap;
	
	protected Map<String, Method> getMethodMap(){
		
		return methodMap;
	}
	
	public static void init() throws Exception{
		
		methodMap = new HashMap<String, Method>();
		
		methodMap.put("getData", Server_thread_service.class.getDeclaredMethod("getData", String[].class));
		methodMap.put("connect", Server_thread_service.class.getDeclaredMethod("connect",Server_thread.class));
		methodMap.put("disconnect", Server_thread_service.class.getDeclaredMethod("disconnect"));
		methodMap.put("kick", Server_thread_service.class.getDeclaredMethod("kick",Server_thread.class));
		
		clsMap = new HashMap<>();
		
		clsMap.put("str", String.class);
		clsMap.put("int", int.class);
		clsMap.put("boo", boolean.class);
	}
	
	
	
	
	
	private Server_thread thread;
	
	private Server_thread processingThread;//记录服务正在处理的任务由哪一个socket线程发起 如果回包时发现socket线程已经被更改了 则不再回这个包  如果这个对象是空则表示服务没有在处理任何任务
	
	private int processingNum;//当一个客户端下线或者被另外一个客户端踢下线时  必须要等到在处理的任务全部完成才行  这个计数就是记录还有多少任务仍然在处理中
	
	private boolean isWaittingForLogin;//是否有一个客户端正在请求连接
	
	public DB_user_unit user;
	
	public Server_thread_service(DB_user_unit _user){
		
		user = _user;
	}
	
	public boolean hasThread(){
		
		return thread != null;
	}
	
	public void connect(Server_thread _thread) throws Exception{
		
		Panel.show("玩家登陆:" + user.name);
		
		thread = _thread;
		
		if(processingNum > 0){
			
			isWaittingForLogin = true;
			
		}else{
			
			thread.sendData("1\ntrue");
		}
	}
	
	public void kick(Server_thread _thread) throws Exception{
		
		Panel.show("玩家被踢下线:" + user.name);
		
		if(thread != null){
		
			sendData(-1);
			
			thread.kick();
		}
		
		connect(_thread);
	}
	
	protected void addProcessingNum(){
		
		processingNum++;
	}
	
	protected void delProcessingNum() throws Exception{
		
		processingNum--;
		
		if(processingNum == 0){
			
			processingOver();
		}
	}
	
	private void processingOver() throws Exception{
		
		if(isWaittingForLogin){
			
			isWaittingForLogin = false;
			
			if(thread != null){
			
				thread.sendData("1\ntrue");
			}
		}
	}
	
	public void disconnect(){

		Panel.show("玩家下线:" + user.name);
		
		thread = null;
	}
	
	public void getData(String[] strVec) throws Exception{
		
		if(processingThread != null){
			
			throw new Exception("getData error processingThread is not null!");
		}
		
		int index = Integer.parseInt(strVec[0]);
		
		Csv_connect csv_connect = Csv_connect.dic.get(index);
		
		if(csv_connect.method == null){
			
			sendData(999, "getData error no handler:" + csv_connect.id);
			
			return;
		}
		
		addProcessingNum();
		
		processingThread = thread;
		
		Object[] objVec = new Object[csv_connect.arg.length];
		
		for(int i = 0 ; i < csv_connect.arg.length ; i++){
			
			int length = csv_connect.arg[i].length();
			
			String str = csv_connect.arg[i].substring(0, 3);
			
			int times = (length - 3) / 2;
			
			objVec[i] = split(strVec[i + 1], times, str);
		}
		
		csv_connect.method.invoke(this, objVec);
	}

	public void sendData(int _id,Object... objVec) throws Exception{
		
		Csv_connect csv_connect = Csv_connect.dic.get(_id);
		
		if(csv_connect.type == 1){
			
			if(processingThread != null){
				
				if(processingThread == thread){
					
					sendDataReal(_id, csv_connect, objVec);
				}
				
				processingThread = null;
				
			}else{
				
				throw new Exception("sendData error id:" + csv_connect.id + " type:" + csv_connect.type);
			}
			
			delProcessingNum();
			
		}else{
			
			if(thread != null){
			
				sendDataReal(_id, csv_connect, objVec);
			}
		}
	}
	
	private void sendDataReal(int _id, Csv_connect csv_connect, Object[] objVec) throws Exception{
		
		String str = String.valueOf(_id);
		
		if(csv_connect.arg.length > 0){
		
			str = str + "\n";
		
			for(int i = 0 ; i < csv_connect.arg.length ; i++){
				
				int length = csv_connect.arg[i].length();
				
				int times = (length - 3) / 2;
				
				if(i < csv_connect.arg.length - 1){
				
					str = str + concat(objVec[i], times) + "\n";
					
				}else{
					
					str = str + concat(objVec[i], times);
				}
			}
		}
			
		thread.sendData(str);
	}
	
	private static Object split(String _str,int _times,String _type){
		
		if(_str.equals("null")){
			
			return null;
		}
		
		if(_times == 0){
			
			switch(_type){
			
			case "int":
				
				return Integer.parseInt(_str);
				
			case "str":
				
				return _str;
				
			default:
			
				return Boolean.parseBoolean(_str);
			}
		}
		
		int index = _str.indexOf(":");
		
		int num = Integer.valueOf(_str.substring(0, index));
		
		_str = _str.substring(index + 1);
		
		String[] strV;
		
		if(num != 0){
			
			String splitStr = "";
			
			for(int i = 0 ; i < _times ; i++){
				
				splitStr = splitStr + "\\$";
			}
			
			strV = _str.split(splitStr);
			
		}else{
			
			strV = new String[0];
		}
		
		Class<?> cls = clsMap.get(_type);
		
		for(int i = 1 ; i < _times ; i++){
			
			cls = Array.newInstance(cls, 0).getClass();
		}
		
		Object o = Array.newInstance(cls, strV.length);
		
		for(int i = 0 ; i < strV.length ; i++){
			
			Array.set(o, i, split(strV[i], _times - 1, _type));
		}
		
		return o;
	}
	
	private static String concat(Object _obj,int _times){
		
		if(_obj == null){
			
			return "null";
		}
		
		if(_times == 0){
			
			return String.valueOf(_obj);
		}
		
		int length = Array.getLength(_obj);
		
		String str = length + ":";
		
		if(length == 0){
			
			return str;
		}
		
		String concatStr = "";
		
		for(int i = 0 ; i < _times ; i++){
			
			concatStr = concatStr + "$";
		}
		
		for(int i = 0 ; i < length - 1 ; i++){
			
			str = str + concat(Array.get(_obj, i), _times - 1) + concatStr;
		}
		
		str = str + concat(Array.get(_obj, length - 1), _times - 1);
		
		return str;
	}
}
