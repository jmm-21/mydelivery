package delivery;

public class RobotLechuga implements Runnable{
		bufferLechuga bLechuga;
		
	public RobotLechuga(bufferLechuga _bLechuga) {
		bLechuga= _bLechuga;
	}
	public void run() {
		while(true) {
				bLechuga.poner();
		}
	}
}
