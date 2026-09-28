package delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Above12Euros extends RecursiveTask <List<Pedido>>{
	private List<Pedido> lp;
	private List<Pedido> lpAux1;
	private List<Pedido> lpAux2;
	private List<Pedido> lpFinal;
	
	public Above12Euros(List <Pedido> _lp, List<Pedido> _lpFinal) {
		lp= _lp;
		lpFinal= _lpFinal;
	}
	public List<Pedido> compute(){
		double precio;
		if(lp.size()<10) {
			int i;
			for (i=0; i<lp.size(); i++) {
				precio= lp.get(i).getPrecioPedido();
				if(precio> 12) {
					lpFinal.add(lp.get(i));
				}
			}
		}else {
			int middle= lp.size()/2;
			lpAux1 = new ArrayList<>();
			lpAux2 = new ArrayList<>();
			Above12Euros  g1 = new Above12Euros(lp.subList(0, middle), lpAux1);
			Above12Euros  g2 = new Above12Euros(lp.subList(middle+1, lp.size()), lpAux2);
			invokeAll(g1,g2);
			
			try {
				lpFinal.addAll(g1.get());
				lpFinal.addAll(g2.get());
			}catch (Exception e) {e.printStackTrace();}
		}
		
		
		return lpFinal;
	}
}
