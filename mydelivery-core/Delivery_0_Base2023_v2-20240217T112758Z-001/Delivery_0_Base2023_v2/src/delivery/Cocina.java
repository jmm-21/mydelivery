package delivery;

import pcd.util.ColoresConsola;
import pcd.util.Traza;
import java.util.List;

public class Cocina {
	bufferPan bPan;
	bufferPollo bPollo;
	bufferLechuga lechuga;
	Restaurante r;
	
	public Cocina (Restaurante _r) {// , bufferPan _bPan, bufferPollo _bPollo
		r= _r;
		bPan = new bufferPan();
		bPollo = new bufferPollo();
		RobotPan rPan = new RobotPan(bPan);
		RobotPollo rPollo = new RobotPollo(bPollo);
		lechuga = new bufferLechuga();
		RobotLechuga rLechuga = new RobotLechuga(lechuga); //hay que hacerle unn thread?
		Thread t1 = new Thread(rPan);
		Thread t2 = new Thread(rPollo);
		Thread t3 =new Thread(rLechuga);
		t1.start();
		t2.start();
		t3.start();
	}
	
	public void cocinar (Pedido p) {
		Traza.traza(ColoresConsola.GREEN, 2, "Cocinando el pedido: "+p.printConRetorno());
		try {
			List<Producto> prod;
			prod= p.getProductos();
			
			for(int i=0; i < prod.size(); i++){
				if( prod.get(i).getId().equals("0")) {
					System.out.println("Cocinando hamburguesa de pollo");
					bPan.coger();
					bPollo.coger();
					lechuga.coger();
				}

			}
		}
			catch (Exception e) {e.printStackTrace();}
}
}