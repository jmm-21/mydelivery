package delivery;
import java.util.concurrent.locks.*;

public class bufferPollo {
		int iPoner= 0;
		int iCoger= 0;
		int cima = 0;
		int N= 1;
		//int vector[] = new int[N];
		
		Lock monitor = new ReentrantLock();
		
		Condition lleno = monitor.newCondition();
		Condition vacio = monitor.newCondition();
		
		public void poner()throws  InterruptedException {
			monitor.lock();
			try {
				while (cima ==N)
					lleno.await();
				
				//vector[iPoner]= elemento;
				cima= cima+1;
				//iPoner= (iPoner +1)%N;
				
				vacio.signal();
				
			}finally {
				monitor.unlock();
			}
		}
		
		 public void coger()throws InterruptedException { //cambiado de int a void
			//int elemento;
			monitor.lock();
			try {
				while (cima ==0)
					vacio.await();
				
				//elemento= vector[iCoger];
				cima= cima-1;
				//iCoger= (iCoger +1)%N;
				
				lleno.signal();
				
				//return elemento;
			}finally {
				monitor.unlock();
			}
		}
	}
