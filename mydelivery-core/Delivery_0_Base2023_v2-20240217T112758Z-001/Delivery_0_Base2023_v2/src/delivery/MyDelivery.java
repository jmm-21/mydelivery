package delivery;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.LinkedList;
import java.util.List;


import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.util.ColoresConsola;
import pcd.util.Traza;

public class MyDelivery {
	
	public MyDelivery () {
		// para facilitar las trazas
		Traza.setNivel(Config.modoTraza);
		
		// Creando los restaurantes
		CadenaRestaurantes cadenaRestaurantes = null;
		cadenaRestaurantes = new CadenaRestaurantes (Config.numeroRestaurantes);
		cadenaRestaurantes.crearRestaurantes();

		// CARGAR PEDIDOS DE FICHERO
		// Obtenemos una lista de pedidos
		List<Pedido> lp;
		lp = new LinkedList<>();
		
		
		// Tambi�n puedes crear tus propios pedidos usando el m�todo generaPedidos de la clase Pedido. 
		// En la clase Pedido tambi�n tienes un m�todo para volcar esos pedidos a un fichero.
		
		// LANZAR PEDIDOS
		// Los estamos lanzando secuencialmente
		long initialTime = new Date().getTime();
		LinkedList<Restaurante> listaRestaurantes = cadenaRestaurantes.getRestaurantes();
		ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		switch(Config.executor ) {
			case 0: //forma antigua
				lp = Pedido.pedidosDesdeFichero ("C:/fsfig/pedidos7.bin");
				Traza.traza(ColoresConsola.RED_BOLD, 2, "Lanzando pedidos de la forma tradicional");
				for (Pedido p:lp){
					Tramitador t1;
					t1= new Tramitador (p, listaRestaurantes.get(p.getRestaurante()));
					Thread t2= new Thread(t1);
					t2.start();
				}
		break;
		
			case 1: //executor
				lp = Pedido.pedidosDesdeFichero ("C:/fsfig/pedidos7.bin");
				Traza.traza(ColoresConsola.RED_BOLD, 2, "Lanzando pedidos con executor");
				for (Pedido p:lp){
					Tramitador t1;
					t1= new Tramitador (p, listaRestaurantes.get(p.getRestaurante()));
					executor.execute(t1);
		}
		break; 
		
			case 2: //streams 
			lp = Pedido.pedidosDesdeFichero ("C:/fsfig/pedidos7.bin");
			Traza.traza(ColoresConsola.RED_BOLD, 2, "Lanzando pedidos con streams");
			lp.stream().parallel() 
			  .forEach(p-> listaRestaurantes.get(p.getRestaurante()).tramitarPedido(p));
		break; 

			case 3: //observables
				lp = Pedido.pedidosDesdeFichero ("C:/fsfig/pedidos7.bin");
				Traza.traza(ColoresConsola.RED_BOLD, 2, "Lanzando pedidos con observables");
				Observable<Pedido> obs; 
				obs= Pedido.pedidosDesdeFicherosObservable("C:/fsfig/pedidos7.bin");
				obs.flatMap(p1-> Observable.just(p1)
				   .subscribeOn(Schedulers.computation())
				   .doOnNext(p2-> listaRestaurantes.get(p2.getRestaurante()).tramitarPedido(p2)))
				   .subscribe();
						
			break;
		}
		
		//Pedidos mayores de 12 euros
		
		List<Pedido> lp12Euros = new ArrayList<>();
		Above12Euros g = new Above12Euros(lp, lp12Euros);
		ForkJoinPool pool = new ForkJoinPool();
		Traza.traza(ColoresConsola.CYAN, 2, "Lanzando fork/join");
		pool.execute(g);
		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.DAYS);
		}catch (InterruptedException e){e.printStackTrace();}
		try {
			Traza.traza(ColoresConsola.CYAN, 2, "Lista de pedidos más de 12€: ");
			for (Pedido p:lp12Euros)
			System.out.println ("Pedido "+ p.getId()+ " de precio "+ p.getPrecioPedido());
			
		}catch (Exception e) {e.printStackTrace();}
		
		//PEDIDOS MENOR 7 EUROS
		Traza.traza(ColoresConsola.CYAN, 2, "Versión 7.a) Pedidos con importe < 7 para fichero7.bin ");
		List<Pedido> l7 = lp.stream()
							.parallel()
							.filter(d->d.getPrecioPedido()<7)
							//.map(Pedido::getId)
							.collect(Collectors.toList()); //los almacena en una lista
		for (int i=0; i<l7.size(); i++) {
			Pedido p = l7.get(i);
			System.out.println(p.getId()+ ",  "+ p.getPrecioPedido());
		}
		
		
		//PEDIDO MÁS CARO
		Traza.traza(ColoresConsola.CYAN, 2, "Uso de Streams paralelos: ");
		Optional<Double> mayorPrecio = lp.stream()
										  .parallel()
										  .map(Pedido::getPrecioPedido)
										  .reduce((a,b)-> a<b ? b : a);
		System.out.println ("Pedido más caro: ");
		System.out.println(mayorPrecio.get());
		
		//OBSERVADORES
		Traza.traza(ColoresConsola.CYAN, 2, "Uso de Observables: ");
		Observable <Pedido> obsPedidos = Observable.fromIterable (lp);
		//Imprime aquellos pedidos cuyo importe sea mayor que 12
		obsPedidos.subscribeOn(Schedulers.computation())
				  .filter(p-> p.getPrecioPedido()>12)
				  .subscribe(p-> System.out.println(p.getId()+ " Current Thread: "+ Thread.currentThread()));		
		//Calcula la suma de todos los pedidos
		obsPedidos.subscribeOn(Schedulers.computation())
		          .reduce(0.0 ,(total, p)-> total+ p.getPrecioPedido())
		          .subscribe(total->System.out.println("Suma de todos los pedidos: "+ total));
		
		try {
			Thread.sleep(5000); 
		} catch (InterruptedException e) {e.printStackTrace();}
		
		// AUDITOR�AS
		listaRestaurantes.parallelStream()
		 				 .forEach(r-> System.out.print ("\nAuditor�a Restaurante "+r.getNombre()+" "+r.getBalance()));
				
		System.out.println ("\nAuditoria Cadena: "+ cadenaRestaurantes.getBank().audit(0, Config.numeroRestaurantes));
		
		System.out.println ("Tiempo total invertido en la tramitaci�n: "+(new Date().getTime() - initialTime));
	try {
		CallableMaxPedido MaxPedido = new CallableMaxPedido(lp);
		Future<Pedido> resultado= executor.submit(MaxPedido);
		Pedido MayorPedido= resultado.get();
		Traza.traza(ColoresConsola.CYAN, 2, "Uso de Callable: ");
		System.out.println ("Pedido más caro: "+ MayorPedido.getId()  + ", costando "+ MayorPedido.getPrecioPedido());
	}catch (Exception e) { e.printStackTrace();}
	
	//ENCONTRAR PEDIDOS DADA UNA DIRECCIÓN
	String Dir;
	Dir= "Berna, 11";
	Traza.traza(ColoresConsola.CYAN, 2, "Encontrando pedidos dada una dirección: ");
	if (lp.stream()
		  .anyMatch(d->d.getDireccion().equals(Dir))) {
		System.out.println ("Dirección "+ Dir +" encontrada");
	}
}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException{
		new MyDelivery();
	}
}
