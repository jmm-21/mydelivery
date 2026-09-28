package delivery;
public class Config {
	public final static int modoTraza = 2;				// nivel de profundidad de la traza
	public final static int numeroRestaurantes = 7;		// n�mero de restaurantes a crear
	public final static int numeroPedidos = 5;			// n�mero de pedidos por canal a crear
	public final static int numeroMoteros = 2;			// n�mero de moteros por restaurante a crear
	public final static int numeroProductos = 3; 		// l�mite de cantidad de productos a crear en pedido
	public final static int maximoIdProducto = 5; 		// n�mero m�ximo de id de producto
	public final static int maximoPrecioProducto = 5;	// precio m�ximo de cada producto. 
	public final static int executor = 2; //3= se lanzan los pedidos con Observables, 2= se lanza los pedidos con Streams,
										  //1= se lanzan los pedidos con Executor, 0= se lanzan con Threads
	public final static boolean Cyclic= true; //true= se usa Cyclic, false= se usa el semáforo
}
