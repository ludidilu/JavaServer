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
	
	public void call(String _methodName,Object..._arg) throws Exception{
		
		synchronized (this) {
			
			addMethod(_methodName,_arg);
			
			if(inQueue){
				
				if(!locker.isLocked()){
					
					//走到这里说明这个service已经在线程池中提出了申请  但是还没有被派发线程进行处理  那就立即处理这个service  让线程池之后派发的线程无事可做吧
					locker.lock();
				}
				
			}else{
				
				//走到这里说明这个service之前没有在线程池中提出过申请  那就不需要申请了  直接处理这个service
				inQueue = true;
				
				locker.lock();
			}
		}
		
		if(locker.isHeldByCurrentThread()){
			
			SuperThreadUnit.doService(this);
			
		}else{
			
			locker.lock();
			
			locker.unlock();
		}
	}
	
	public void process(String _methodName,Object..._arg){
		
		synchronized (this) {
			
			addMethod(_methodName,_arg);
			
			if(!inQueue){
				
				inQueue = true;
				
				SuperThread.process(this);
			}
		}
	}
	
	private void addMethod(String _methodName,Object[] _arg){
		
		Map<String, Method> methodMap = getMethodMap();
		
		if(methodMap == null){
			
			System.err.println("methodMap is null! Class = " + this.getClass().toString());
		}
		
		Method method = methodMap.get(_methodName);
		
		if(method == null){
			
			System.err.println("Method is null. Method Class = " + this.getClass().toString() + "  name = " + _methodName);
		}
		
		methodArr.add(method);
		argArr.add(_arg);
	}
}
