package superService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SuperService {

	boolean inQueue = false;
	
	ReentrantLock locker = new ReentrantLock();
	
	ArrayList<Method> methodArr = new ArrayList<>();
	ArrayList<Object[]> argArr = new ArrayList<>();
	
	protected Map<String, Method> getMethodMap(){
		
		return null;
	}
	
	public Object call(String _methodName,Object..._arg) throws Exception{
		
		Map<String, Method> methodMap = getMethodMap();
		
		if(methodMap == null){
			
			System.err.println("methodMap is null! Class = " + this.getClass().toString());
		}
		
		Method method = methodMap.get(_methodName);
		
		if(method == null){
			
			System.err.println("Method is null. Method Class = " + this.getClass().toString() + "  name = " + _methodName);
		}
		
		locker.lock();
		
		Object result = method.invoke(this, _arg);
			
		locker.unlock();
		
		return result;
	}
	
	public void process(String _methodName,Object..._arg){
		
		Map<String, Method> methodMap = getMethodMap();
		
		if(methodMap == null){
			
			System.err.println("methodMap is null! Class = " + this.getClass().toString());
		}
		
		Method method = methodMap.get(_methodName);
		
		if(method == null){
			
			System.err.println("Method is null. Method Class = " + this.getClass().toString() + "  name = " + _methodName);
		}
		
		synchronized (this) {
			
			methodArr.add(method);
			argArr.add(_arg);
			
			if(!inQueue){
				
				inQueue = true;
				
				SuperThread.process(this);
			}
		}
	}
}
