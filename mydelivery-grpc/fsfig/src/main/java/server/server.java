package server;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pcd.util.Ventana;
import service.service;

//Definición de la clase principal del servidor.
public class server {
	private static ConcurrentHashMap<String, Integer> stock2;
	
 public static void main(String[] args) throws IOException, InterruptedException {
     // Creación de una ventana para mostrar los mensajes de log del servidor.
     Ventana v = new Ventana ("Servidor · gRPC", 200, 200);
     stock2 = new ConcurrentHashMap<>();
     
     // Construcción del servidor gRPC en el puerto 50051 y añadiendo el servicio definido por service.     
     
     Server server = ServerBuilder.forPort(50051).addService(new service(v, stock2)).build();

     // Añade texto a la ventana para indicar el inicio de la lista de servicios y métodos disponibles.
     v.addText("##########################################################################");
     v.addText("   LISTADO DE SERVICIOS y MÉTODOS DISPONIBLES ");
     v.addText("##########################################################################\n");
     
     // Itera sobre todos los servicios definidos en el servidor y los muestra en la ventana.
     	// Muestra el nombre del servicio.
     	// Itera sobre todos los métodos del servicio y los muestra.
     server.getServices().forEach(s->{
     	v.addText("Servicio: "+ s.getServiceDescriptor().getName());
     	s.getServiceDescriptor().getMethods().forEach(m->{
     		v.addText("Método: "+m.getFullMethodName());
     	});
     });

     
     // Inicia el servidor gRPC y muestra un mensaje en la ventana indicando que el servidor está en funcionamiento.
     server.start();

     v.addText("");
     v.addText("Servidor iniciado y escuchando en puerto 50051");
     v.addText("...");
     
     // El servidor permanece activo esperando conexiones entrantes.
     server.awaitTermination();
 }
}

