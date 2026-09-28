package delivery;

import java.util.concurrent.Semaphore;
public class SemaforoTodos {
	int cuantos;
	Semaphore totalMot;
	Semaphore exMutua;
	
	public SemaforoTodos() {
		cuantos = 0;
		totalMot= new Semaphore(0);
		exMutua= new Semaphore(1);
	}
	
	public void estamosTodos() {
		try {
			exMutua.acquire();
			cuantos++;
		}catch (Exception e) {e.printStackTrace();}	
		if(cuantos< Config.numeroMoteros) {
			try {
				exMutua.release();
				totalMot.acquire();
			}catch (Exception e) {e.printStackTrace();}
		}
		else{
			totalMot.release();
			exMutua.release();
		}
		
	}
}