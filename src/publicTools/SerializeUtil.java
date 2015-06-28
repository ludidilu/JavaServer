package publicTools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {

	public static byte[] serialize(Object object) throws Exception{

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		
		oos.writeObject(object);
		
		byte[] bytes = baos.toByteArray();
		
		return bytes;
	}
		 
	public static Object unserialize(byte[] bytes) throws Exception {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		
		ObjectInputStream ois = new ObjectInputStream(bais);
		
		return ois.readObject();
	}
		
}
