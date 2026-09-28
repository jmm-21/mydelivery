package delivery;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import bank.*;
import pcd.util.ColoresConsola;
import pcd.util.Traza;

 public class Restaurante {
	private String nombre;					// nombre del restaurante
	private Account account;				// cuenta bancaria para registrar la recaudaci�n
	private Cocina cocina; 					// la cocina de este restaurante
	private ControlMoteros controlMoteros;  // los moteros de este restaurante
	

	
	public Restaurante (Account _ac, String _nombre, int _numeroMoteros) {
		account = _ac;
		nombre = _nombre;
		controlMoteros = new ControlMoteros (this, Config.numeroMoteros);
		cocina = new Cocina (this);
		Traza.traza(ColoresConsola.GREEN_BOLD_BRIGHT, 1,"Creando restaurante: "+nombre);
	}
	
	public String getNombre () {
		return nombre;
	}
	
	public double getBalance (){
		return account.getBalance();
	}
	
	public Account getAccount () {
		return account;
	}
	
	public boolean pagarPedido(Pedido p) { //Cliente
		String respuesta = null;
		try {
			Socket s = new Socket ("localhost",10000);
			ObjectOutputStream salida = new ObjectOutputStream (s.getOutputStream());
			BufferedReader entrada = new BufferedReader (new InputStreamReader (s.getInputStream()));
			
			// ENVIAR
			salida.writeObject(p);
			
			// RECIBIR
			respuesta = entrada.readLine ();
				
			s.close();			
		} catch (IOException e) {e.printStackTrace();}
		
		return respuesta.equals("OK");
	}

	
	public void pedidoNoPagados(String id, double precio){ //Cliente
		DatosPagoPedido p = new DatosPagoPedido(id, precio);
		InetAddress serverIPAddress;
		int serverPort;
		DatagramSocket s;
		DatagramPacket paqueteEnviado;
		byte[] enviarData = new byte[1024]; 
	    ByteArrayOutputStream baos;
		ObjectOutputStream oos;
		String serverName="localhost";
		serverPort=10000;
		try {
			serverIPAddress= InetAddress.getByName(serverName);
			s= new DatagramSocket();
			
			baos = new ByteArrayOutputStream ();
			oos = new ObjectOutputStream (baos);
			oos.writeObject(p);
			
			enviarData = new byte[baos.toByteArray().length];
			enviarData= baos.toByteArray();
			
			//enviamos el paquete
			paqueteEnviado = new DatagramPacket(enviarData, enviarData.length, serverIPAddress, serverPort); 

			s.send(paqueteEnviado);
					
			s.close();
		} catch (IOException e) { e.printStackTrace();
			System.exit(-1);
		}	
		
	}
	
	public void tramitarPedido (Pedido _p) {
		Pedido p =_p;
		// Tramitar un pedido es:
		
		if(Config.serversTCP_UDP) {
			boolean respuesta;
			respuesta = pagarPedido(p); //llamamos al pagar pedido	
			if(respuesta){
				System.out.println("Pedido aceptado");
				
				account.deposit(p.getPrecioPedido());	// a�adir la cantidad abonada a la cuenta del banco
				controlMoteros.moteroLibre();
				cocina.cocinar(p);						// mandar el pedido a cocina
				controlMoteros.enviarPedido(p);			// una vez cocinado, mandarlo a los moteros para que uno lo coja
				
			}else {
				Traza.traza(ColoresConsola.PURPLE_BOLD, 2,"El Pedido "+ p.getId()+ " No se ha pagado");
				pedidoNoPagados(p.getId(), p.getPrecioPedido());
				
			}
		}else {
			account.deposit(p.getPrecioPedido());	// a�adir la cantidad abonada a la cuenta del banco
			controlMoteros.moteroLibre();
			cocina.cocinar(p);						// mandar el pedido a cocina
			controlMoteros.enviarPedido(p);			// una vez cocinado, mandarlo a los moteros para que uno lo coja
		}
	}
}
