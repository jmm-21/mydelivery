package delivery;

import java.util.concurrent.CyclicBarrier;

public class Cyclic {
	CyclicBarrier cb;
	
	public Cyclic (CyclicBarrier _cb) {
		cb= _cb;
	}
	public void estamosTodos() {
		try {
			cb.await();		
		} catch (Exception e) {e.printStackTrace();}
	}
}
