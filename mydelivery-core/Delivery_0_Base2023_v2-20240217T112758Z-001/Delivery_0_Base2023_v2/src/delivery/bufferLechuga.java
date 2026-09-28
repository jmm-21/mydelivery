package delivery;
import java.util.concurrent.LinkedBlockingQueue;

public class bufferLechuga {
	int N = 2;
	Integer lechuga= 1;
	LinkedBlockingQueue<Integer> I;
	
	public bufferLechuga() {
		I = new LinkedBlockingQueue<Integer>(N);
		
	}
	void poner(){
		
		try {
			I.put(lechuga);
		}catch(Exception e) {e.printStackTrace();}
	}
	void coger(){
		try {
			lechuga= I.take();
		} catch (Exception e) {e.printStackTrace();}
	}
}
