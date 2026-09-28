package delivery;

import java.net.ServerSocket;
import java.net.Socket;

import pcd.util.Ventana;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;

public class PasarelaPago implements Runnable{
	Socket s = null;
	static Ventana v =  new Ventana ("Servidor TCP", 200, 200);
	static int contPedidosEnviados= 0;
	public PasarelaPago(Socket _s) {
		this.s = _s; 
	}
	
	public void run() {
	try {
		ObjectInputStream entrada = new ObjectInputStream(s.getInputStream());
		BufferedWriter salida = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		
		Pedido p = (Pedido) entrada.readObject();
		
		if(p.getPrecioPedido()>=12) {
			synchronized(v) {
				contPedidosEnviados++;
				v.addText ("Pedido recibido: " + p.getId() + " (" + String.format("%.2f", p.getPrecioPedido()) + " €): ");
				v.addText(" >>> Pedidos Totales: "+ contPedidosEnviados);
			}

			salida.write("OK\n");
			salida.flush();			
		}
		else {
			salida.write("KO\n");
			salida.flush();
		}
		}catch (Exception e) {e.printStackTrace();}
	} 
	public static void main (String args[]) {
		v.addText("--------- Server TCP creado ---------");
		ServerSocket ss = null;
		Socket s2 = null;
		try {
			ss = new ServerSocket(10000);
			v.addText("Servidor abierto correctamente");
			//ss.setSoTimeout(5000);
			boolean bucle = false;
			while (!bucle) {
				s2 = ss.accept();
				//v.addText("Se ha recibido un pedido");
				new Thread (new PasarelaPago(s2)).start();
			}
			s2.close();
			ss.close(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}