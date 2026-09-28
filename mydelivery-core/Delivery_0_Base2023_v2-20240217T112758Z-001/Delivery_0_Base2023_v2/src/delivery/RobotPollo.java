package delivery;

public class RobotPollo implements Runnable{
	bufferPollo bPollo;
	//int elem;
	
	public RobotPollo(bufferPollo _bPollo) {
		bPollo= _bPollo;
	}
	public void run() {
		while(true) {
			try {
				bPollo.poner();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
}
