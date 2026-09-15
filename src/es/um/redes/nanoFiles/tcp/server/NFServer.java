package es.um.redes.nanoFiles.tcp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFServer implements Runnable {

	public static final int PORT = 10000;

	private ServerSocket serverSocket = null;
	private boolean isRunning = false;

	public NFServer() throws IOException {
		/*
		 * TODO: (Boletín SocketsTCP) Crear una direción de socket a partir del puerto
		 * especificado (PORT)
		 */
		/*
		 * TODO: (Boletín SocketsTCP) Crear un socket servidor y ligarlo a la dirección
		 * de socket anterior
		 */
		try {
			serverSocket = new ServerSocket(PORT); 
		} catch (IOException e) {
			serverSocket = new ServerSocket(0);    
		}
	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación.
	 * 
	 */
	public void test() {
		if (serverSocket == null || !serverSocket.isBound()) {
			System.err.println(
					"[fileServerTestMode] Failed to run file server, server socket is null or not bound to any port");
			return;
		} else {
			System.out
					.println("[fileServerTestMode] NFServer running on " + serverSocket.getLocalSocketAddress() + ".");
		}

		while (true) {
			/*
			 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
			 * otros peers que soliciten descargar ficheros.
			 */
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				//System.out.println("[Servidor] Cliente conectado desde: " + clientSocket.getInetAddress());
			/*
			 * TODO: (Boletín SocketsTCP) Tras aceptar la conexión con un peer cliente, la
			 * comunicación con dicho cliente para servir los ficheros solicitados se debe
			 * implementar en el método serveFilesToClient, al cual hay que pasarle el
			 * socket devuelto por accept.
			 */
				serveFilesToClient(clientSocket);

			} catch (IOException e) {
				System.err.println("[Servidor] Error aceptando la conexión.");
				e.printStackTrace();
				break;
			}
		}
	}

	/**
	 * Método que ejecuta el hilo principal del servidor en segundo plano, esperando
	 * conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		isRunning = true;
		while (isRunning) {
			try {
		/*
		 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
		 * otros peers que soliciten descargar ficheros
		 */
				Socket clientSocket = serverSocket.accept();
		/*
		 * TODO: (Boletín SocketsTCP) Al establecerse la conexión con un peer, la
		 * comunicación con dicho cliente se hace en el método
		 * serveFilesToClient(socket), al cual hay que pasarle el socket devuelto por
		 * accept
		 */
		/*
		 * TODO: (Boletín TCPConcurrente) Crear un hilo nuevo de la clase
		 * NFServerThread, que llevará a cabo la comunicación con el cliente que se
		 * acaba de conectar, mientras este hilo vuelve a quedar a la escucha de
		 * conexiones de nuevos clientes (para soportar múltiples clientes). Si este
		 * hilo es el que se encarga de atender al cliente conectado, no podremos tener
		 * más de un cliente conectado a este servidor.
		 */
				NFServerThread clientThread = new NFServerThread(clientSocket);
				clientThread.start();

			} catch (IOException e) {
				if (isRunning) {
					System.err.println("[Servidor] Error en accept.");
				}
				break;
			}	
		}
	}
		
	/*
	 * TODO: (Boletín SocketsTCP) Añadir métodos a esta clase para: 1) Arrancar el
	 * servidor en un hilo nuevo que se ejecutará en segundo plano 2) Detener el
	 * servidor (stopserver) 3) Obtener el puerto de escucha del servidor etc.
	 */

	public void startServer() {
		new Thread(this).start();
	}

	public void stopServer() {
		isRunning = false;
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getServerPort() {
		if (serverSocket != null) {
			return serverSocket.getLocalPort();
		}
		return -1;
	}

	/**
	 * Método de clase que implementa el extremo del servidor del protocolo de
	 * transferencia de ficheros entre pares.
	 * 
	 * @param socket El socket para la comunicación con un cliente que desea
	 *               descargar ficheros.
	 */
	public static void serveFilesToClient(Socket socket) {
		/*
		 * TODO: (Boletín SocketsTCP) Crear dis/dos a partir del socket
		 */
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		/*
		 * TODO: (Boletín SocketsTCP) Mientras el cliente esté conectado, leer mensajes
		 * de socket, convertirlo a un objeto PeerMessage y luego actuar en función del
		 * tipo de mensaje recibido, enviando los correspondientes mensajes de
		 * respuesta.
		 */
			while (true) {
				PeerMessage request = PeerMessage.readMessageFromInputStream(dis);
				if (request == null) break;

				if (request.getOpcode() == PeerMessageOps.OPCODE_QUERY_FILES) {
					FileInfo[] misFicheros = NanoFiles.db.getFiles();
					PeerMessage response = new PeerMessage(PeerMessageOps.OPCODE_FILE_LIST);
					response.setFiles(misFicheros); 
					response.writeMessageToOutputStream(dos);
				}
				else if (request.getOpcode() == PeerMessageOps.OPCODE_DOWNLOAD_FILE) {
					String requestedHash = request.getHash();
		/*
		 * TODO: (Boletín SocketsTCP) Para servir un fichero, hay que localizarlo a
		 * partir de su hash (o subcadena) en nuestra base de datos de ficheros
		 * compartidos. Los ficheros compartidos se pueden obtener con
		 * NanoFiles.db.getFiles(). Los métodos lookupHashSubstring y
		 * lookupFilenameSubstring de la clase FileInfo son útiles para buscar ficheros
		 * coincidentes con una subcadena dada del hash o del nombre del fichero. El
		 * método lookupFilePath() de FileDatabase devuelve la ruta al fichero a partir
		 * de su hash completo.
		 */
					FileInfo[] ficherosCompartidos = NanoFiles.db.getFiles();
					java.util.ArrayList<FileInfo> encontrados = new java.util.ArrayList<>();
					for (FileInfo f : ficherosCompartidos) {
						if (f.fileHash.contains(requestedHash)) {
							encontrados.add(f);
						}
					}
					FileInfo[] coincidentes = encontrados.toArray(new FileInfo[0]);
					if (coincidentes.length == 1) {
						String path = NanoFiles.db.lookupFilePath(coincidentes[0].fileHash);
						File file = new File(path);
						
						RandomAccessFile raf = new RandomAccessFile(file, "r");
						byte[] data = new byte[(int) raf.length()];
						raf.readFully(data);
						raf.close();

						PeerMessage response = new PeerMessage(PeerMessageOps.OPCODE_FILE_DATA, data);
						response.writeMessageToOutputStream(dos);
					} else {
						PeerMessage response = new PeerMessage(PeerMessageOps.OPCODE_FILE_NOT_FOUND);
						response.writeMessageToOutputStream(dos);
					}
				}
			}
		} catch (EOFException e) {
			//System.out.println("[Servidor] El cliente ha cerrado la conexión (Fin de flujo).");
		} catch (IOException e) {
			System.err.println("[Servidor] Error comunicándose con el cliente.");
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}