package data.dataDB;

import panel.Panel;
import redis.clients.jedis.Jedis;
import data.dataDB.user.DB_user;

public class DB {
	
	public static Jedis jedis;
	
	public static void init(String _path, int _port, Class<?> _serviceClass, Class<?> _unitClass) throws Exception{
		
		jedis = new Jedis(_path, _port);
        
        Panel.show("�������ݿ�ɹ�");
        
        DB_user.init(_serviceClass,_unitClass);
	}
}
