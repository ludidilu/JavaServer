package timer;

import java.util.HashMap;
import java.util.Iterator;

import superService.SuperService;

public class TimerService extends Thread{

	private static TimerService instance;
	
	public static TimerService getInstance(){
		
		if(instance == null){
			
			instance = new TimerService();
		}
		
		return instance;
	}
	
	private HashMap<SuperService, HashMap<String, TimerServiceUnit>> dic;
	private boolean isRunning;
	
	public TimerService(){
		
		dic = new HashMap<>();
		
		isRunning = true;
		
		start();
	}
	
	public void stopTimer(){
		
		synchronized (this) {
		
			isRunning = false;
		}
	}
	
	public void addTimerCallBack(SuperService _service,long _delay,String _methodName,Object... arg){
		
		TimerServiceUnit unit = new TimerServiceUnit();
		
		unit.service = _service;
		unit.methodName = _methodName;
		unit.arg = arg;
		unit.delay = _delay;
		unit.lastTime = System.currentTimeMillis();
		
		synchronized (this) {
			
			if(dic.containsKey(_service)){
				
				HashMap<String, TimerServiceUnit> map = dic.get(_service);
				
				map.put(unit.methodName, unit);
				
			}else{
				
				HashMap<String, TimerServiceUnit> map = new HashMap<>();
				
				map.put(unit.methodName, unit);
				
				dic.put(_service, map);
			}
		}
	}
	
	public void removeTimerCallBack(SuperService _service,String _methodName){
		
		synchronized (this) {
			
			if(dic.containsKey(_service)){
				
				HashMap<String, TimerServiceUnit> map = dic.get(_service);
				
				if(map.containsKey(_methodName)){
					
					map.remove(_methodName);
					
					if(map.isEmpty()){
						
						dic.remove(_service);
					}
				}
			}
		}
	}
	
	public void run(){
		
		try{
		
			while(true){
				
				long nowTime = System.currentTimeMillis();
				
				synchronized (this) {
					
					if(!isRunning){
						
						return;
					}
					
					Iterator<HashMap<String, TimerServiceUnit>> iter = dic.values().iterator();
					
					while(iter.hasNext()){
						
						HashMap<String, TimerServiceUnit> map = iter.next();
						
						Iterator<TimerServiceUnit> iter2 = map.values().iterator();
						
						while(iter2.hasNext()){
							
							TimerServiceUnit unit = iter2.next();
						
							if(nowTime - unit.lastTime > unit.delay){
								
								unit.service.process(unit.methodName, unit.arg);
								
								unit.lastTime = nowTime;
							}
						}
					}
				}
				
				sleep(1000);
			}
			
		}catch(Exception e){
			
			
		}
	}
}
