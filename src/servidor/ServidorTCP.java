package servidor;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorTCP {

	private String[] respuesta;
	private int[] combinacion;
	private int reintegro;
	private int complementario;

	private ServerSocket socketservidor;
	private Socket socketcliente;
	private BufferedReader entrada;
	private PrintWriter salida;

	/**
	 * Constructor
	 */
	public ServidorTCP(int puerto) {
		this.respuesta = new String[9];
		this.respuesta[0] = "Boleto inválido - Números repetidos";
		this.respuesta[1] = "Boleto inválido - números incorrectos (1-49)";
		this.respuesta[2] = "6 aciertos";
		this.respuesta[3] = "5 aciertos + complementario";
		this.respuesta[4] = "5 aciertos";
		this.respuesta[5] = "4 aciertos";
		this.respuesta[6] = "3 aciertos";
		this.respuesta[7] = "Reintegro";
		this.respuesta[8] = "Sin premio";
		generarCombinacion();
		imprimirCombinacion();

		try {
			socketservidor = new ServerSocket(puerto);
			System.out.println("Servidor escuchando en el puerto " + puerto);
			socketcliente = socketservidor.accept();
			System.out.println("Cliente conectado.");
			entrada = new BufferedReader(new InputStreamReader(socketcliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketcliente.getOutputStream())), true);
		} catch (IOException e) {
			System.err.println("Error al iniciar el servidor: " + e.getMessage());
		}
	}

	/**
	 * Lee la combinación de números enviada por el cliente.
	 * 
	 * @return La combinación como cadena de texto.
	 */
	public String leerCombinacion() {
		try {
			System.out.println("Combinacion cliente: " + entrada.readLine());
			return entrada.readLine();
		} catch (IOException e) {
			System.err.println("Error al leer la combinación: " + e.getMessage());
			return "Error";
		}
	}

	/**
	 * Comprueba el boleto recibido contra la combinación ganadora.
	 * 
	 * @return Respuesta correspondiente al resultado del boleto.
	 */
	public String comprobarBoleto() {
		try {
			String combinacionCliente = leerCombinacion();
			String[] numeros = combinacionCliente.split(" ");
			
			int[] boleto = new int[6];
			for (int i = 0; i < 6; i++) {
				int num = Integer.parseInt(numeros[i]);
				if (num < 1 || num > 49)
					return respuesta[1];
			}

			int aciertos = 0;
			boolean tieneComplementario = false;
			for (int num : boleto) {
				if (Arrays.stream(combinacion).anyMatch(x -> x == num))
					aciertos++;
				if (num == complementario)
					tieneComplementario = true;
			}

			if (aciertos == 6)
				return respuesta[2];
			if (aciertos == 5 && tieneComplementario)
				return respuesta[3];
			if (aciertos == 5)
				return respuesta[4];
			if (aciertos == 4)
				return respuesta[5];
			if (aciertos == 3)
				return respuesta[6];
			if (Arrays.stream(boleto).anyMatch(x -> x == reintegro))
				return respuesta[7];
			return respuesta[8];

		} catch (Exception e) {
			System.err.println("Error al comprobar boleto: " + e.getMessage());
			return "Error en la validación del boleto";
		}
	}

	/**
	 * Envía la respuesta generada al cliente.
	 * 
	 * @param respuesta La respuesta que se enviará al cliente.
	 */
	public void enviarRespuesta(String respuesta) {
		salida.println(respuesta);
	}

	/**
	 * Cierra la conexión con el cliente y el servidor.
	 */
	public void finSesion() {
		try {
			salida.close();
			entrada.close();
			socketcliente.close();
			socketservidor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-> Servidor Terminado");
	}

	/**
	 * Método que genera una combinación ganadora aleatoria. NO MODIFICAR
	 */
	private void generarCombinacion() {
		Set<Integer> numeros = new TreeSet<>();
		Random aleatorio = new Random();
		while (numeros.size() < 6) {
			numeros.add(aleatorio.nextInt(49) + 1);
		}
		int i = 0;
		this.combinacion = new int[6];
		for (Integer elto : numeros) {
			this.combinacion[i++] = elto;
		}
		this.reintegro = aleatorio.nextInt(49) + 1;
		this.complementario = aleatorio.nextInt(49) + 1;
	}

	/**
	 * Método que imprime la combinación ganadora en la consola del servidor.
	 */
	private void imprimirCombinacion() {
		System.out.print("Combinación ganadora: ");
		for (Integer elto : this.combinacion)
			System.out.print(elto + " ");
		System.out.println("");
		System.out.println("Complementario:       " + this.complementario);
		System.out.println("Reintegro:            " + this.reintegro);
	}
}
