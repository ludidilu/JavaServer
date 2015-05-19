package publicTools;

import java.lang.reflect.Method;


public class DelayCall extends Thread{

	private int time;
	private Object target;
	private Method method;
	private Object[] args;
	
	public static void call(int _time,Object _target,Method _method,Object... _args){
		
		DelayCall unit = new DelayCall(_time, _target, _method,_args);
		
		unit.start();
	}
	
	public DelayCall(int _time,Object _target,Method _method,Object[] _args){
		
		time = _time;
		target = _target;
		method = _method;
		args = _args;
	}
	
	public void run(){
		
		try{
		
			Thread.sleep(time);
			
			synchronized (target) {
				
				method.invoke(target, args);
			}
			
		}catch(Exception e){
			
			System.out.println("error");
		}
	}
}
