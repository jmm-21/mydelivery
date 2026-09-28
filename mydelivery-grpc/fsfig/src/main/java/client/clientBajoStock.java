package client;

import grpc.StockServiceGrpc;
import grpc.Stock.StockRequest;
import grpc.StockServiceGrpc.StockServiceBlockingStub;

// Importaciones necesarias para la funcionalidad del cliente gRPC.

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pcd.util.ColoresConsola;
import pcd.util.Traza;
import pcd.util.Ventana;

// Clase principal del cliente.
public class clientBajoStock {

    // Canal de comunicación con el servidor y ventana de interfaz de usuario para mostrar mensajes.
   static ManagedChannel channel;
   static Ventana v;

    // Constructor del cliente, inicializa la ventana de la interfaz de usuario.
    public clientBajoStock() {
        v = new Ventana("Cliente Bajo Stock · gRPC", 200, 200);
    }
    
    // Método para iniciar el canal de comunicación con el servidor especificando IP y puerto.
    public synchronized void iniciarCanal(String ip, int port) {
        // Construye y establece el canal de comunicación. Usa texto plano para la conexión.
    	channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build();
    	
    }

    // Método para cerrar el canal de comunicación con el servidor.
    public synchronized void cerrarCanal() {
    	channel.shutdown();
    }

    /* SERVER-STREAM */
    // Método para realizar un Stream del Servidor, donde el servidor envía una secuencia de mensajes al cliente.
    public void bajoStock() {
        // Mostrar inicio de la operación de stream del servidor en la interfaz de usuario.
        v.addText("##########################################################################");
        v.addText("   Bajo Stock   ");
        v.addText("##########################################################################\n");
        
        // Creación de un stub bloqueante, adecuado para llamadas unarias o de streaming del servidor.
    	 StockServiceBlockingStub stub = StockServiceGrpc.newBlockingStub(channel);
        
        // Creación de la solicitud.
    	 
    	 int umbral= 81;
        StockRequest stockRequest = StockRequest.newBuilder().setUmbral(umbral).build();
        Traza.traza(ColoresConsola.YELLOW_BOLD, 3, "  [ Cliente >>> ]:  Umbral de productos establecido en   "+ umbral);
        v.addText("  [ Cliente >>> ]:  Umbral de productos establecido en   "+ umbral);
        
        
        // Llamada al método de stream del servidor y manejo de cada respuesta recibida.

        stub.bajoStock(stockRequest).forEachRemaining(response->{
        	v.addText("   [>>>Cliente]: Producto "+response.getIdProducto());
        });

        v.addText("\n  ¡Fin!");
        v.addText("\n\n");
    }
    
    // Método principal que ejecuta el cliente.
    public static void main(String[] args) {

        // Crear una instancia del cliente.
    	clientBajoStock cliente = new clientBajoStock();

        // Iniciar el canal de comunicación con el servidor especificando la IP y el puerto.
        cliente.iniciarCanal("localhost", 50051);
//        v.addText(">>>Canal Iniciado ");
        
        // Realizar las distintas llamadas al servidor.
        cliente.bajoStock();
        
        // Cerrar el canal de comunicación con el servidor una vez finalizadas las operaciones.
        cliente.cerrarCanal();
    }
}