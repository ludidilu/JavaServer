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
					
					//�ߵ�����˵�����service�Ѿ����̳߳������������  ���ǻ�û�б��ɷ��߳̽��д���  �Ǿ������������service  ���̳߳�֮���ɷ����߳����¿�����
					locker.lock();
				}
				
			}else{
				
				//�ߵ�����˵�����service֮ǰû�����̳߳������������  �ǾͲ���Ҫ������  ֱ�Ӵ������service
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
