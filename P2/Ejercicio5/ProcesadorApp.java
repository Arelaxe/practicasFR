import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.File;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.ArrayList;

public class ProcesadorApp extends Thread{
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private InputStream inputStream;
	// stream de escritura (por aquí se envía los datos al cliente)
	private OutputStream outputStream;
	
	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;
	
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public ProcesadorApp(Socket socketServicio) {
		this.socketServicio=socketServicio;
		random=new Random();
	}

	enum Estado{
		INICIO, INICIO_SESION_NOMBRE, INICIO_SESION_CONTRASENA, REGISTRO_NOMBRE, REGISTRO_CONTRASENA, ELEGIR_ACCION,
		SUBIR_NOMBRE, SUBIR_PRECIO, COMPRAR, SALIR
	}
	String usuario = null;
	String nombre_producto = null;

	public Estado estado = Estado.INICIO;
	
	
	// Aquí es donde se realiza el procesamiento realmente:
	@Override
	public void run(){
		
		
		try {
			// Obtiene los flujos de escritura/lectura
			PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(),true);
			BufferedReader inReader= new BufferedReader (new InputStreamReader(socketServicio.getInputStream()));
			
			do{
				String cadenaRecibida = inReader.readLine();
		
				String respuesta=funcionApp(cadenaRecibida);
			
				outPrinter.println(respuesta);
			} while (true);

			

		} catch (IOException e) {
			System.err.println("Error al obtener los flujos de entrada/salida.");
		}

	}

	String funcionApp(String cad){
		String respuesta = "";
		if (estado == Estado.INICIO){
			if ("0".equals(cad)){
				respuesta = "Has elegido registro, introduce un nombre de usuario ";
				estado = Estado.REGISTRO_NOMBRE;
			}
			else if ("1".equals(cad)){
				respuesta = "Has elegido inicio de sesión, introduce tu nombre de usuario ";
				estado = Estado.INICIO_SESION_NOMBRE;
			}
			else{
				respuesta = "Salir";
				estado = Estado.SALIR;
			}
		}
		else if (estado == Estado.REGISTRO_NOMBRE){
			File archivo = null;
			boolean existe = false;
			FileReader fr = null;
			BufferedReader br = null;

			try{
				archivo = new File ("usuarios.txt");
				fr = new FileReader(archivo);
				br = new BufferedReader(fr);
				String linea;
			
				while((linea=br.readLine())!=null){
					if (linea.equals(cad)){
						existe = true;
					}
				}
			} catch (Exception ex){
				ex.printStackTrace();
			} finally{
				try {
					if (null != fr){
						fr.close();
						br.close();
					}
				} catch (Exception ex2){
					System.out.println("Fallo de fichero\n");
				}
			}

			if (existe){
				respuesta = "Este nombre ya existe, elige otro ";
			}
			else{
				FileWriter escribir = null;
				PrintWriter pw = null;
				try{
					escribir = new FileWriter("usuarios.txt",true);
					pw = new PrintWriter(escribir);
					pw.println(cad);
				}catch (Exception ex2){
					System.out.println("Fallo de fichero\n");
				}finally{
					try{
						if (null!=escribir)
							escribir.close();
							pw.close();
					} catch (Exception e2){
						e2.printStackTrace();
					}
				}
				respuesta = "Introduce una contraseña ";
				usuario = cad;
				estado = Estado.REGISTRO_CONTRASENA;
			}
		}
		else if (estado == Estado.REGISTRO_CONTRASENA){
			File archivo = null;
			Scanner s = null;
			FileWriter escribir = null;
			PrintWriter pw = null;
			try{
				escribir = new FileWriter("usuarios.txt",true);
				pw = new PrintWriter(escribir);
				archivo = new File ("usuarios.txt");
				s = new Scanner(archivo);
				String linea = null;
			
				while(s.hasNextLine()){
					linea = s.nextLine();
					if (linea.equals(usuario)){
						pw.print(":"+cad+"\n");
					}
				}
			}catch (Exception ex2){
				System.out.println("Fallo de fichero\n");
			}finally{
				try{
					if (null!=escribir){
						escribir.close();
						pw.close();
						s.close();
					}
						
				} catch (Exception e2){
					e2.printStackTrace();
				}
			}
			respuesta = "Registrado! 0: Subir producto - 1: Ver productos a la venta - Otra tecla: Salir";
			estado = Estado.ELEGIR_ACCION;
		}
		else if (estado == Estado.INICIO_SESION_NOMBRE){
			File archivo = null;
			FileReader fr = null;
			BufferedReader br = null;
			boolean existe = false;

			try{
				archivo = new File ("usuarios.txt");
				fr = new FileReader (archivo);
				br = new BufferedReader(fr);
				
				String linea;
			
				while((linea=br.readLine())!=null){
					if (linea.equals(cad)){
						existe = true;
					}
				}
			} catch (Exception ex){
				System.out.println("Fallo de fichero\n");
			} finally{
				try {
					if (fr!=null){
						br.close();
						fr.close();
					}
				} catch (Exception ex2){
					System.out.println("Fallo de fichero\n");
				}
			}

			if (existe){
				respuesta = "Introduce la contraseña ";
				estado = Estado.INICIO_SESION_CONTRASENA;
				usuario = cad;
			}
			else{
				respuesta = "Este nombre no existe, prueba otra vez ";
			}
		}
		else if(estado == Estado.INICIO_SESION_CONTRASENA){
			File archivo = null;
			Scanner s = null;
			boolean correcto = false;
			try{
				archivo = new File ("usuarios.txt");
				s = new Scanner(archivo);
				String linea = null;
			
				while(s.hasNextLine() && !correcto){
					linea = s.nextLine();
					if (linea.equals(usuario)){
						linea = s.nextLine();
						if (linea.equals(":"+cad))
							correcto = true;
					}
				}
			}catch (Exception ex2){
				System.out.println("Fallo de fichero\n");
			}finally{
				try{
					if (null!=s){
						s.close();
					}
						
				} catch (Exception e2){
					System.out.println("Fallo de fichero\n");
				}
			}
			if (correcto){
				respuesta = "Inicio de sesión correcto! 0: Subir producto - 1: Ver productos a la venta - Otra tecla: Salir ";
				estado = Estado.ELEGIR_ACCION;
			}
			else{
				respuesta = "Contraseña incorrecta, prueba otra vez";
				estado = Estado.INICIO_SESION_CONTRASENA;
			}
		}
		else if(estado == Estado.ELEGIR_ACCION){
			if (cad.equals("0")){
				respuesta = "Has elegido subir. Introduce el nombre de tu producto ";
				estado = Estado.SUBIR_NOMBRE;
			}
			else if (cad.equals("1")){
				File archivo = null;
				FileReader fr = null;
				BufferedReader br = null;
			
			try{
				archivo = new File ("productos.txt");
				fr = new FileReader(archivo);
				br = new BufferedReader(fr);

				String linea;
				while((linea=br.readLine())!=null){
					String [] p = linea.split(":");
					if (!p[1].equals(usuario)){
						respuesta += "Artículo: "+p[0]+", Vendedor: "+p[1]+", Precio: "+p[2]+" - ";
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(null!=fr){
						fr.close();
						br.close();
					}
				}catch (Exception e2){
					e2.printStackTrace();
				}
			}
			estado = Estado.COMPRAR;
			respuesta += " Escribe el nombre del artículo que quieras comprar - 0: Volver al menú ";
			}
			else{
				respuesta = "Salir";
				estado = Estado.SALIR;
			}
		}
		else if(estado == Estado.SUBIR_NOMBRE){
			File archivo = null;
			boolean existe = false;
			FileReader fr = null;
			BufferedReader br = null;

			try{
				archivo = new File ("productos.txt");
				fr = new FileReader(archivo);
				br = new BufferedReader(fr);
				String linea;
			
				while((linea=br.readLine())!=null && !existe){
					String [] p = linea.split(":");
				
					if (p[0].equals(cad)){
						existe = true;
					}
				}
			} catch (Exception ex){
				ex.printStackTrace();
			} finally{
				try {
					if (null != fr){
						fr.close();
						br.close();
					}
				} catch (Exception ex2){
					System.out.println("Fallo de fichero\n");
				}
			}
			if (existe){
				respuesta = "Ya existe un producto con este nombre, ponle otro por favor";
			}
			else{
				nombre_producto = cad;
			FileWriter escribir = null;
			PrintWriter pw = null;
			try{
				escribir = new FileWriter("productos.txt",true);
				pw = new PrintWriter(escribir);
				pw.print(cad+":"+usuario);
			}catch (Exception ex2){
				System.out.println("Fallo de fichero\n");
			}finally{
				try{
					if (null!=escribir)
						escribir.close();
						pw.close();
				} catch (Exception e2){
					e2.printStackTrace();
				}
			}
			respuesta = "Introduce el precio del producto";
			estado = Estado.SUBIR_PRECIO;
			}
			
		}
		else if(estado == Estado.SUBIR_PRECIO){
			File archivo = null;
			Scanner s = null;
			FileWriter escribir = null;
			PrintWriter pw = null;
			try{
				escribir = new FileWriter("productos.txt",true);
				pw = new PrintWriter(escribir);
				archivo = new File ("productos.txt");
				s = new Scanner(archivo);
				String linea = null;
			
				while(s.hasNextLine()){
					linea = s.nextLine();
					if (linea.equals(nombre_producto+":"+usuario)){
						pw.print(":"+cad+"\n");
					}
				}
			}catch (Exception ex2){
				System.out.println("Fallo de fichero\n");
			}finally{
				try{
					if (null!=escribir){
						escribir.close();
						pw.close();
						s.close();
					}
						
				} catch (Exception e2){
					e2.printStackTrace();
				}
			}
			respuesta = "Producto subido! 0:Subir producto - 1:Ver productos a la venta - Otra tecla: Salir";
			estado = Estado.ELEGIR_ACCION;
		}
		else if (estado==Estado.COMPRAR){
			if (cad.equals("0")){
				respuesta = "0: Subir producto 1: Ver productos a la venta - Otra tecla: Salir";
				estado = Estado.ELEGIR_ACCION;
			}
			else{
				boolean compra = false;
				File archivo = null;
				FileReader fr = null;
				BufferedReader br = null;
				ArrayList<String> texto = new ArrayList<String>();
			
			try{
				archivo = new File ("productos.txt");
				fr = new FileReader(archivo);
				br = new BufferedReader(fr);

				String linea;
				String linea_compra;
				
				while((linea=br.readLine())!=null){
					String [] p = linea.split(":");
					if (p[0].equals(cad)){
						compra = true;
						linea_compra = linea;
					}
					else{
						texto.add(linea);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(null!=fr){
						fr.close();
						br.close();
					}
				}catch (Exception e2){
					e2.printStackTrace();
				}
			}

			if (compra){
				FileWriter fichero = null;
				PrintWriter pw1 = null;
				try{
					fichero = new FileWriter("productos.txt");
					pw1 = new PrintWriter(fichero);
					pw1.print("");
				} catch (Exception e){
					e.printStackTrace();
				} finally{
					try {
						if (null!=fichero){
							fichero.close();
							pw1.close();
						} 
					}catch (Exception e2){
						e2.printStackTrace();
					}
				}
				try{
					fichero = new FileWriter("productos.txt");
					pw1 = new PrintWriter(fichero);
					for (String linea : texto){
						pw1.println(linea);
					}
				} catch (Exception e){
					e.printStackTrace();
				} finally{
					try {
						if (null!=fichero){
							fichero.close();
							pw1.close();
						} 
					}catch (Exception e2){
						e2.printStackTrace();
					}
				}
				respuesta = "Compra realizada con éxito! 0: Subir producto - 1: Ver productos a la venta - Otra tecla: Salir";
				estado = Estado.ELEGIR_ACCION;
			}
			else{
				respuesta = "Ese artículo no existe ";
			}

			}
		}
		
		return (respuesta);
	}
}
