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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class ClienteApp {

	public static void main(String[] args) {
		
		byte []buferEnvio;
		byte []buferRecepcion=new byte[256];
		int bytesLeidos=0;
		
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		
		// Socket para la conexión TCP
		Socket socketServicio;

		Scanner teclado = new Scanner(System.in);
		
		try {
			socketServicio = new Socket (host, port);

			PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(),true);
			BufferedReader inReader= new BufferedReader (new InputStreamReader(socketServicio.getInputStream()));
			
			System.out.print("Elige qué hacer\n0: Registro\n1: Inicio de sesión\nOtra tecla: Salir \n");
			String cadenaRecibida = "";

			do{
				String mensaje = teclado.nextLine();
			
				outPrinter.println(mensaje);

				outPrinter.flush();
				
				cadenaRecibida = inReader.readLine();
				System.out.println(cadenaRecibida);
			} while(!cadenaRecibida.equals("Salir"));
			
			socketServicio.close();
			
			// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
