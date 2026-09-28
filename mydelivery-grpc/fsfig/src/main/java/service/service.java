package service;

import java.util.concurrent.ConcurrentHashMap;

import grpc.Stock.DameStockRequest;
import grpc.Stock.ProductosReply;
import grpc.Stock.RestoProductoReply;
import grpc.Stock.StockRequest;
import grpc.Stock.VentaRequest;
import grpc.StockServiceGrpc.StockServiceImplBase;
import io.grpc.stub.StreamObserver;

import pcd.util.Ventana;

public class service extends StockServiceImplBase {
    
    // Instancia de Ventana para la interfaz de usuario, usada para mostrar mensajes.
    Ventana v;
    ConcurrentHashMap<String, Integer> stock2;
    
    // Constructor que recibe una instancia de Ventana para utilizar en la clase.
    public service (Ventana v_, ConcurrentHashMap<String, Integer> stock2_) {
        v = v_;
        stock2 = stock2_;
    }
    
    public void registrarVenta(VentaRequest ventaRequest, StreamObserver<RestoProductoReply> respuestaObserver) {
        // Muestra mensajes en la interfaz de usuario indicando el inicio de la operación unaria.
        v.addText("##########################################################################");
        v.addText("   Registrar Ventas ");
        v.addText("##########################################################################\n");
        
        // Muestra el mensaje recibido del cliente.
        v.addText("   [>>>Servidor]: Recibido producto "+ ventaRequest.getIdProducto()+ " con cantidad: " + ventaRequest.getCantidad());        
         
        String idP = ventaRequest.getIdProducto();
        int resto= stock2.compute(idP, (Key, Value)-> {
        					if(Value != null) {
        						return Value- ventaRequest.getCantidad();
        					}
        					else {
        						return 100- ventaRequest.getCantidad();
        					}
        });
        RestoProductoReply restoProductoReply = RestoProductoReply.newBuilder().setResto(resto).build();

        
        // Envía el stock al cliente.
        respuestaObserver.onNext(restoProductoReply);
        
        
        // Notifica que el resto ha sido completamente enviado.
        respuestaObserver.onCompleted();
      
        // Muestra la respuesta enviada al cliente en la interfaz de usuario.  
        
        stock2.forEach((Key, Value)-> {
        	v.addText("Producto "+ Key+ " con stock "+ Value);
        });
        
        v.addText("  [ Servidor >>> ]:  " + resto);
        v.addText("  ------------------------------- ");
//        v.addText("\n  ¡Fin!");
        v.addText("\n\n");
    }
    
    // Método de streaming del servidor. Responde al cliente con una secuencia de mensajes.
    public void bajoStock(StockRequest stockRequest, StreamObserver<ProductosReply> respuestaServerObserver) {
        // Muestra mensajes en la interfaz de usuario indicando el inicio del streaming del servidor.
        v.addText("##########################################################################");
        v.addText("   Bajo Stock");
        v.addText("##########################################################################\n");
        
        // Muestra el mensaje recibido del cliente
        v.addText("   [>>> Servidor]: Umbral mínimo de productos ( " + stockRequest.getUmbral()+ " )");
        v.addText("");
        
        /*
        Envía varios mensajes al cliente, uno por cada carácter de la solicitud.
        	Convierte el mensaje en un flujo de caracteres.
        	Convierte cada entero a su caracter correspondiente.
        	Para cada caracter, crea una respuesta y la envía al cliente:
        		- Muestra el caracter enviado al cliente.
        		- Espera un poco antes de enviar el siguiente mensaje.
        */     	
         
        	int umbral= stockRequest.getUmbral();
        	stock2.forEach((Key, Value)-> {
        		if(Value < umbral) {
        			ProductosReply productosReply = ProductosReply.newBuilder().setIdProducto(Key).build(); // tambien se puede usar String.valueOf(i)
            		respuestaServerObserver.onNext(productosReply);
            		
            		v.addText("El producto " + Key + ", con sock: "+ umbral+ "Está por debajo del umbral");
            		
                	try {
        				Thread.sleep(100);
        			} catch (InterruptedException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        	});
        
        	
        // Una vez enviados todos los caracteres, completa la comunicación.
        respuestaServerObserver.onCompleted();
        
        v.addText("  ------------------------------- ");
//        v.addText("\n  ¡Fin!");
        v.addText("\n\n");
    }   

 // Método bidireccional. Permite una comunicación en dos direcciones entre el cliente y el servidor.
    @Override
    public  StreamObserver<DameStockRequest> dameStock (StreamObserver<VentaRequest> respuestaObserver) {
        v.addText("##########################################################################");
        v.addText("   Dame Stock ");
        v.addText("##########################################################################\n");
        
        /*
        Retorna un nuevo observador de solicitud. Este manejará los mensajes recibidos del cliente.
        	(1) Sin atributos
        	(2) Implementar los métodos:
        		- onNext:  
        			* Muestra el mensaje recibido y envía una respuesta transformada (en mayúsculas) al cliente.
        			* Transforma el mensaje recibido a mayúsculas para la respuesta.
        			* Muestra la respuesta enviada en la interfaz de usuario.
        		- onError (opcional)
        		- onCompleted: Completa la comunicación después de recibir todos los mensajes del cliente.
        */         
        
        // Retorna un observador de solicitud. 
        return new StreamObserver<DameStockRequest>() {
        	
            @Override
            public void onNext(DameStockRequest dameStockRequest) {
            	String  productoPedido = dameStockRequest.getIdProducto();
            	v.addText("  [>>> Servidor]: Mostrando stock del pedido "+ productoPedido);
    
            	VentaRequest ventaRequest = VentaRequest.newBuilder().setIdProducto(productoPedido).setCantidad(stock2.get(productoPedido)).build();	
            	respuestaObserver.onNext(ventaRequest);
            	v.addText("  [Servidor >>> ]: "+ventaRequest.getCantidad());
          

            	v.addText("");
            }

            @Override
            public void onError(Throwable t) {
                // Manejo de errores en la comunicación.
            }

            @Override
            public void onCompleted() {
            	respuestaObserver.onCompleted();
                v.addText("\n  ¡Fin!");   
                v.addText("\n\n");             	
            }
        };        
     
    } 
}

