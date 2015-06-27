package superService;

import java.lang.reflect.Method;

public class SuperThreadUnit extends Thread{

	private SuperService service;
	
	public SuperThreadUnit(SuperService _service){
		
		service = _service;
	}
	
	public void run(){
		
		doService(service);
	}
	
	static void doService(SuperService _service){
		
		while(true){
			
			Method method;
			Object[] arg;
			
			synchronized(_service){
				
				if(_service.locker.isLocked()){
					
					if(!_service.locker.isHeldByCurrentThread()){
					
						//只有一种情况会走到这里 就是这个service被call了  其他线程直接处理了这个service
						return;
					}
					
				}else{
					
					_service.locker.lock();
				}
				
				if(_service.methodArr.size() == 0){
					
					_service.locker.unlock();
					
					_service.inQueue = false;
					
					return;
				}
				
				method = _service.methodArr.remove(0);
				arg = _service.argArr.remove(0);
			}
			
			try{
				
				method.invoke(_service, arg);
				
			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
	}
}
