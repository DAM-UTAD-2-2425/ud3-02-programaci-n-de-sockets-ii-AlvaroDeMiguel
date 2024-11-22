package cliente;


import java.io.*;
import java.net.*;

public class ClienteTCP {

    private Socket socketcliente;
    private BufferedReader entrada = null;
    private PrintWriter salida = null;

    /**
     * Constructor
     * Establece la conexión con el servidor en la IP y puerto especificados.
     */
    public ClienteTCP(String ip, int puerto) {
        try {
        	socketcliente = new Socket(ip, puerto);
        	 entrada = new BufferedReader(new InputStreamReader(socketcliente.getInputStream()));
             salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketcliente.getOutputStream())), true);
            System.out.println("Conexión establecida con el servidor en " + ip + ":" + puerto);
        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    /**
     * Envía la combinación de números al servidor y recibe la respuesta.
     * @param combinacion La combinación de números a enviar al servidor.
     * @return Respuesta recibida del servidor con el resultado del boleto.
     */
    public String comprobarBoleto(int[] combinacion) {
        try {
            // Convierto la combinación de números en un string separado por espacios
            StringBuilder combinacionStr = new StringBuilder();
            for (int num : combinacion) {
                combinacionStr.append(num).append(" ");
//                System.out.println(combinacionStr);
            }

            // Envio la combinación al servidor
            salida.println(combinacionStr.toString().trim()); // trim elimina el espacio extra al final

            // Leo la respuesta del servidor
            return entrada.readLine();
        } catch (IOException e) {
            System.err.println("Error al enviar la combinación o recibir la respuesta: " + e.getMessage());
            return "Error de comunicación con el servidor";
        }
    }

    /**
     * Finalizo la conexión con el servidor.
     */
    public void finSesion() {
    	try {
            salida.close();
            entrada.close();
            socketcliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-> Cliente Terminado");
    }
}

