//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;


//
// Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
// ¡Podríamos realizar un procesado concurrente! 
//
public class ProcesadorYodafy {
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private DatagramSocket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private InputStream inputStream;
	// stream de escritura (por aquí se envía los datos al cliente)
	private OutputStream outputStream;

	private DatagramPacket paquete_envio;
	private DatagramPacket paquete_recepcion;
	InetAddress direccion;
	int port;
	
	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;
	
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public ProcesadorYodafy(DatagramSocket socketServicio) {
		this.socketServicio=socketServicio;
		random=new Random();
	}
	
	
	// Aquí es donde se realiza el procesamiento realmente:
	void procesa(){
		
		// Como máximo leeremos un bloque de 1024 bytes. Esto se puede modificar.
		byte [] datosRecibidos=new byte[1024];
		int bytesRecibidos=0;
		
		// Array de bytes para enviar la respuesta. Podemos reservar memoria cuando vayamos a enviarka:
		byte [] datosEnviar;
		
		
		
		paquete_recepcion = new DatagramPacket(datosRecibidos, datosRecibidos.length);
		try{
			socketServicio.receive(paquete_recepcion);
		}catch (IOException e1) {
			System.err.println("Error al recibir el paquete.");
		}
			
			
			datosEnviar = paquete_recepcion.getData();
			direccion = paquete_recepcion.getAddress();
			port = paquete_recepcion.getPort();
			
			// Yoda hace su magia:
			// Creamos un String a partir de un array de bytes 
			String peticion=new String(datosEnviar,0,datosEnviar.length);
			// Yoda reinterpreta el mensaje:
			String respuesta=yodaDo(peticion);
			// Convertimos el String de respuesta en una array de bytes:
			datosEnviar=respuesta.getBytes();
			
			
			paquete_envio = new DatagramPacket(datosEnviar,datosEnviar.length,direccion,port);
		
		try {	
			socketServicio.send(paquete_envio);
		} catch (IOException e2) {
			System.err.println("Error al enviar el paquete.");
		}

	}

	// Yoda interpreta una frase y la devuelve en su "dialecto":
	private String yodaDo(String peticion) {
		// Desordenamos las palabras:
		String[] s = peticion.split(" ");
		String resultado="";
		
		for(int i=0;i<s.length;i++){
			int j=random.nextInt(s.length);
			int k=random.nextInt(s.length);
			String tmp=s[j];
			
			s[j]=s[k];
			s[k]=tmp;
		}
		
		resultado=s[0];
		for(int i=1;i<s.length;i++){
		  resultado+=" "+s[i];
		}
		
		return resultado;
	}
}
