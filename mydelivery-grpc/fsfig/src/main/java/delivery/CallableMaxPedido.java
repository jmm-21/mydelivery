package delivery;

import java.util.concurrent.Callable;
import java.util.List;

public class CallableMaxPedido implements Callable<Pedido>{
	private List<Pedido> listaP;
	double valorMax= 0;
	
	public CallableMaxPedido(List<Pedido> _listaP) {
		listaP= _listaP;
	}
	public Pedido call() throws Exception{
		Pedido p= listaP.get(0);
		Pedido maxValP= listaP.get(0);
		valorMax= p.getPrecioPedido();
		for (int i=0; i<listaP.size();i++) {
			p= listaP.get(i);
			if(p.getPrecioPedido()>valorMax) {
				valorMax= p.getPrecioPedido();
				maxValP= p;
			}	
		}
		return maxValP;
	}
}


 