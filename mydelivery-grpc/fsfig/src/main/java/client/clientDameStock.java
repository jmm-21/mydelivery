package client;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import grpc.StockServiceGrpc;
import grpc.Stock.DameStockRequest;
import grpc.Stock.VentaRequest;
import grpc.StockServiceGrpc.StockServiceStub;

// Importaciones necesarias para la funcionalidad del cliente gRPC.

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import pcd.util.Ventana;

// Clase principal del cliente.
public class clientDameStock {

	// Canal de comunicación con el servidor y ventana de interfaz de usuario para
	// mostrar mensajes.
	static ManagedChannel channel;
	static Ventana v;

	// Constructor del cliente, inicializa la ventana de la interfaz de usuario.
	public clientDameStock() {
		v = new Ventana("Cliente Bajo Stock · gRPC", 200, 200);
	}

	// Método para iniciar el canal de comunicación con el servidor especificando IP
	// y puerto.
	public synchronized void iniciarCanal(String ip, int port) {
		// Construye y establece el canal de comunicación. Usa texto plano para la
		// conexión.
		channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build();

	}

	// Método para cerrar el canal de comunicación con el servidor.
	public synchronized void cerrarCanal() {
		channel.shutdown();
	}

	/* BIDIRECTIONAL */
	// Método para realizar una comunicación Bidireccional, donde tanto el cliente
	// como el servidor pueden enviar mensajes.
	public void dameStock() {
		// Mostrar inicio de la operación bidireccional en la interfaz de usuario.
		v.addText("##########################################################################");
		v.addText("   Dame Stock ");
		v.addText("##########################################################################\n");

		// Creación de un stub no bloqueante, adecuado para streaming bidireccional.
		StockServiceStub stub = StockServiceGrpc.newStub(channel);
		
		// pedidos de los que se pedirá el stock
		ArrayList<String> lPedidos = new ArrayList<>();
		lPedidos.add("1");
		lPedidos.add("4");

		// Preparar un CountDownLatch para esperar hasta que el servidor haya terminado
		// de enviar mensajes.
		CountDownLatch latch = new CountDownLatch(1);

		// Iniciar el stream bidireccional y manejar las respuestas del servidor con un
		// StreamObserver.
		StreamObserver<DameStockRequest> respuestaClientObserver = stub.dameStock(new StreamObserver<VentaRequest>() {
			
			@Override
			public void onNext(VentaRequest ventaRequest) {
				// Mostrar cada mensaje recibido del servidor en la interfaz de usuario.
				v.addText("   [>>> Cliente]: Stock del producto " + ventaRequest.getIdProducto() + " con  cantidad: " + ventaRequest.getCantidad());
			}

			@Override
			public void onError(Throwable t) {
				// En caso de error, imprimir el stack trace y disminuir el latch.
				t.printStackTrace();
				latch.countDown();
			}

			@Override
			public void onCompleted() {
				// Al completarse la comunicación por parte del servidor, mostrar el fin de la
				// operación en la interfaz de usuario.
				v.addText("\n  ¡Fin!");
				latch.countDown();
			}
		});

		// Enviar varios mensajes al servidor. Aquí se envían los productos de los que queremos saber su stock

		v.addText("	 Pedir stock de:");
		lPedidos.forEach(a->{
			respuestaClientObserver.onNext(DameStockRequest.newBuilder().setIdProducto(a).build());
			v.addText("   [ Cliente >>> ]: Producto " + a);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		

		// Indicar al servidor que el cliente ha terminado de enviar mensajes.
		respuestaClientObserver.onCompleted();

		try {
			// Esperar hasta que el servidor haya terminado de enviar mensajes.
			latch.await(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Método principal que ejecuta el cliente.
	public static void main(String[] args) {

		// Crear una instancia del cliente.
		clientDameStock cliente = new clientDameStock();

		// Iniciar el canal de comunicación con el servidor especificando la IP y el
		// puerto.
		cliente.iniciarCanal("localhost", 50051);

		// Realizar las distintas llamadas al servidor.
		cliente.dameStock();

		// Cerrar el canal de comunicación con el servidor una vez finalizadas las
		// operaciones.
		cliente.cerrarCanal();
	}
}
