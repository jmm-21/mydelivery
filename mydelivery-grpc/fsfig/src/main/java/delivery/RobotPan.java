package delivery;

public class RobotPan implements Runnable{
	bufferPan bPan;
	//int elem;
	
	public RobotPan(bufferPan _bPan) {
		bPan= _bPan;
	}
	public void run() {
		while(true) {
			try {
				bPan.poner();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
}

