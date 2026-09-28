package delivery;


import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import pcd.util.Ventana;
public class PedidoNoPagados {	
	static int contPedidosNoPagados= 0;
	public static void main (String [] args) {
		DatagramSocket s;
		DatagramPacket paqueteRecibido;
		int posicionVentana = 300;
		Ventana v;		
		v = new Ventana("Pedidos No Pagados ",posicionVentana, 200);
		try {
			v.addText("--------- Server UDP creado ---------");
			s= new DatagramSocket(10000);
			v.addText("Servidor abierto correctamente ");
			while(true) {
				byte[] recibeData = new byte[1024];
				paqueteRecibido = new DatagramPacket(recibeData, recibeData.length);
				
				//recivimos el paquete
				s.receive(paqueteRecibido);
				
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(recibeData));
				DatosPagoPedido pedido = (DatosPagoPedido) ois.readObject();
				synchronized (pedido) {
					v.addText ("Pedido recibido: " + pedido.getId() + " (" + String.format("%.2f", pedido.getImporte()) + " €): ");
					contPedidosNoPagados++;
					v.addText(" >>> Pedidos Totales: "+ contPedidosNoPagados);
				}
			}
			
		}catch (Exception e){e.printStackTrace();
		}
	}
}
