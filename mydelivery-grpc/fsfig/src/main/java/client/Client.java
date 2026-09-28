package client;

// Importaciones necesarias para la funcionalidad del cliente gRPC.

import delivery.Producto;
import grpc.Stock.RestoProductoReply;
import grpc.Stock.VentaRequest;
import grpc.StockServiceGrpc;
import grpc.StockServiceGrpc.StockServiceBlockingStub;
import pcd.util.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

// Clase principal del cliente.
public class Client {

    // Canal de comunicación con el servidor y ventana de interfaz de usuario para mostrar mensajes.
   static ManagedChannel channel;
   //static Ventana v;

    // Constructor del cliente, inicializa la ventana de la interfaz de usuario.
    public Client() {
   //     v = new Ventana("Cliente · gRPC", 200, 200);
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

    // Método para realizar una llamada de servicio Unaria.
    public static void mainUnary(Producto producto) {
        // Mostrar inicio de la operación unaria en la interfaz de usuario.
//        v.addText("##########################################################################");
//        v.addText("                                 UNARY");
//        v.addText("##########################################################################\n");
            	
    	// Creación de un stub bloqueante, adecuado para llamadas unarias o de streaming del servidor.
        StockServiceBlockingStub stub = StockServiceGrpc.newBlockingStub(channel);
      
        // Creación de la solicitud y envío del mensaje "mundo" al servidor.
        VentaRequest ventaRequest = VentaRequest.newBuilder().setIdProducto(producto.getId()).setCantidad(producto.getCantidad()).build();
        //v.addText("  [ Cliente >>> ]:  Producto de id: " + producto.getId()+ " con cantidad: " + producto.getCantidad()+ " recibido.");
        Traza.traza(ColoresConsola.BLACK_UNDERLINED, 2, "  [ Cliente >>> ]:  Producto de id: " + producto.getId()+ " con cantidad: " + producto.getCantidad()+ " recibido.");
        
        // Llamada al método unario del servidor y recepción de la respuesta.
        //Respuesta respuesta = stub.unary(solicitud);
        RestoProductoReply restoProductoReply = stub.registrarVenta(ventaRequest);
        

        // Mostrar la respuesta del servidor en la interfaz de usuario.
        //v.addText("Resto de stock del producto "+ producto.getId()+ ", " + restoProductoReply.getResto());
        Traza.traza(ColoresConsola.BLACK_BOLD_BRIGHT, 2, "Resto de stock del producto "+ producto.getId()+ ", " + restoProductoReply.getResto());

//        v.addText("\n  ¡Fin!");
//        v.addText("\n\n");
    }
    
    // Método principal que ejecuta el cliente.
    public synchronized static void iniciarCliente(Producto producto) {

        // Crear una instancia del cliente.
        Client cliente = new Client();

        // Iniciar el canal de comunicación con el servidor especificando la IP y el puerto.
        cliente.iniciarCanal("localhost", 50051);
//        v.addText(">>>Canal Iniciado ");
        
        // Realizar las distintas llamadas al servidor.
        Client.mainUnary(producto);
        
        // Cerrar el canal de comunicación con el servidor una vez finalizadas las operaciones.
        cliente.cerrarCanal();
    }
}
    
