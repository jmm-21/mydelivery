package delivery;

import pcd.util.Ventana;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;


public class ControlMoteros {
	int numeroMoteros;
	Restaurante r;
	Ventana v;
	static int posicionVentana = 10;
	Object oMoteros;
	Object oPedidos;
	List<Pedido> pRes;
	SemaforoTodos t;
	Cyclic c;
	CyclicBarrier cb;
	
	public ControlMoteros (Restaurante _r, int _numeroMoteros) {
		r=_r;
		numeroMoteros = _numeroMoteros;
		oMoteros= new Object();
		oPedidos= new Object();
		pRes = new ArrayList<Pedido>();
		t= new SemaforoTodos();
		cb = new CyclicBarrier(Config.numeroMoteros);
		c= new Cyclic(cb);
		for (int i = 1; i <= Config.numeroMoteros; i++) {
			Moteros m1;
			m1 = new Moteros(this, t, c, i, r);
			m1.start();
		}

		
		// Creamos una ventana para los mensajes de este objeto.
		v =  new Ventana ("Motero de Rest."+r.getNombre(), posicionVentana,10);
		posicionVentana+=250;
	}
	
	public Pedido getPedido() {
		synchronized(oPedidos){		
			try {
				while(pRes.size()==0) {
					System.out.println("Sin pedidos disponibles");
					oPedidos.wait();
				}
			}catch (Exception e) {e.printStackTrace();}
			System.out.println("Existen pedidos disponibles");
			return pRes.remove(0);
		}
	}
	
	public void regresa() {
		synchronized(oMoteros){
			System.out.println ("regresa motero");
			numeroMoteros++;
			oMoteros.notifyAll();
		}
	}
	
	public void moteroLibre() {
		synchronized (oMoteros) {
			while (numeroMoteros==0){
				try {
					System.out.println("Sin moteros disponibles");
					oMoteros.wait();
				}catch (Exception e) {e.printStackTrace();}
			}
			numeroMoteros--;
			System.out.println("Existen moteros disponibles en el restaurante "+r.getNombre());
		}
	}
	
	public void enviarPedido (Pedido p) {
		synchronized(oPedidos) {
			v.addText("REPARTIENDO PEDIDO : "+p.getId());
			pRes.add(p);
			try {
				Thread.sleep(500); // Simulamos que tarda 0,5 segundos en repartir
			} catch (InterruptedException e) {e.printStackTrace();}
			oPedidos.notifyAll();
		}
	}
}
