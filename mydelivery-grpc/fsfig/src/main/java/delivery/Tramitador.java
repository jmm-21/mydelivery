package delivery;

public class Tramitador implements Runnable {
	Pedido p;
	Restaurante r;
	public Tramitador (Pedido _p, Restaurante _r) {
		p= _p;
		r= _r;
	}

	public void run() {
		r.tramitarPedido(p);
	}
}