package delivery;

public class Moteros extends Thread{
	ControlMoteros cm;
	SemaforoTodos t;
	Cyclic c;
	int id;
	Restaurante r;
	public Moteros(ControlMoteros _cm, SemaforoTodos _t, Cyclic _c,int _id, Restaurante _r) {
		cm= _cm;
		t= _t;
		id= _id;
		r= _r;
		c= _c;
	}
	public void run(){
		//Pedido p;
		while (true) {
			if(Config.Cyclic) {
				c.estamosTodos();
			}else {
				t.estamosTodos();
			}
			
			Pedido p = cm.getPedido();
			System.out.println("Motero "+ id +" del restaurante "+ r.getNombre() +" repartiendo pedido: "+p.getId());
			cm.regresa();
		}
	}
}

