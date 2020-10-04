//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class YodafyClienteUDP {

	public static void main(String[] args) {
		
		byte []buferEnvio = new byte[256];
		byte []buferRecepcion=new byte[1024];
		int bytesLeidos=0;
		
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		
		// Socket para la conexión UDP
		DatagramSocket socketServicio;

		InetAddress direccion;
		DatagramPacket paquete_envio, paquete_recepcion;
		
		try {
			socketServicio = new DatagramSocket();
			direccion = InetAddress.getByName("localhost");

			
			// Si queremos enviar una cadena de caracteres por un OutputStream, hay que pasarla primero
			// a un array de bytes:
			buferEnvio="Al monte del volcán debes ir sin demora".getBytes();
			
			paquete_envio = new DatagramPacket(buferEnvio, buferEnvio.length,direccion,port);
			socketServicio.send(paquete_envio);

			paquete_recepcion = new DatagramPacket(buferRecepcion, buferRecepcion.length);
			socketServicio.receive(paquete_recepcion);
			buferRecepcion = paquete_recepcion.getData();
			paquete_recepcion.getAddress();
			paquete_recepcion.getPort();
			
			
			// MOstremos la cadena de caracteres recibidos:
			System.out.println("Recibido: ");
			for(int i=0;i<buferRecepcion.length;i++){
				System.out.print((char)buferRecepcion[i]);
			}
			
			socketServicio.close();
			
			// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
