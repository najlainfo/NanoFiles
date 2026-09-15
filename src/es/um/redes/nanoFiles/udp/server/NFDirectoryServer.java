package es.um.redes.nanoFiles.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros publicados, servidores
	 * registrados, etc.
	 */

		private HashMap<String, InetSocketAddress> registeredServers;
		private HashMap<String, LinkedList<FileInfo>> publishedFiles;

	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability) throws SocketException {
		messageDiscardProbability = corruptionProbability;
		/*
		 * TODO: (Boletín SocketsUDP) Inicializar el atributo socket: Crear un socket
		 * UDP ligado al puerto especificado por el argumento directoryPort en la
		 * máquina local,
		 */
		socket = new DatagramSocket(DIRECTORY_PORT);
		/*
		 * TODO: (Boletín SocketsUDP) Inicializar atributos que mantienen el estado del
		 * servidor de directorio: ficheros, etc.)
		 */
		registeredServers = new HashMap<>();
		publishedFiles = new HashMap<>();


		if (NanoFiles.testModeUDP) {
			if (socket == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
		FileInfo[] ficherosPrecargados = FileInfo.loadFilesFromFolder("dir-shared");
		if (ficherosPrecargados != null && ficherosPrecargados.length > 0) {
			java.util.LinkedList<FileInfo> listaFicheros = new java.util.LinkedList<>();
			for (FileInfo f : ficherosPrecargados) {
				listaFicheros.add(f);
			}
			publishedFiles.put("Directorio", listaFicheros);
			System.out.println("* Directory loaded " + ficherosPrecargados.length + " files from dir-shared");
		}
	}

	public DatagramPacket receiveDatagram() throws IOException {
		DatagramPacket datagramReceivedFromClient = null;
		boolean datagramReceived = false;
		while (!datagramReceived) {
			/*
			 * TODO: (Boletín SocketsUDP) Crear un búfer para recibir datagramas y un
			 * datagrama asociado al búfer (datagramReceivedFromClient)
			 */
			byte[] receptionBuffer = new byte[DirMessage.PACKET_MAX_SIZE];
			datagramReceivedFromClient = new DatagramPacket(receptionBuffer, receptionBuffer.length);
			/*
			 * TODO: (Boletín SocketsUDP) Recibimos a través del socket un datagrama
			 */
			socket.receive(datagramReceivedFromClient);


			if (datagramReceivedFromClient == null) {
				System.err.println("[testMode] NFDirectoryServer.receiveDatagram: code not yet fully functional.\n"
						+ "Check that all TODOs have been correctly addressed!");
				System.exit(-1);
			} else {
				// Vemos si el mensaje debe ser ignorado (simulación de un canal no confiable)
				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					System.err.println(
							"Directory ignored datagram from " + datagramReceivedFromClient.getSocketAddress());
				} else {
					datagramReceived = true;
					System.out
							.println("Directory received datagram from " + datagramReceivedFromClient.getSocketAddress()
									+ " of size " + datagramReceivedFromClient.getLength() + " bytes.");
				}
			}

		}

		return datagramReceivedFromClient;
	}

	public void runTest() throws IOException {

		System.out.println("[testMode] Directory starting...");

		System.out.println("[testMode] Attempting to receive 'ping' message...");
		DatagramPacket rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);

		System.out.println("[testMode] Attempting to receive 'ping&PROTOCOL_ID' message...");
		rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);
	}

	private void sendResponseTestMode(DatagramPacket pkt) throws IOException {
		/*
		 * TODO: (Boletín SocketsUDP) Construir un String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración.
		 */
		String messageFromClient = new String(pkt.getData(), 0, pkt.getLength());
		System.out.println("Data received: " + messageFromClient);
		String responseString;
		/*
		 * TODO: (Boletín SocketsUDP) Después, usar la cadena para comprobar que su
		 * valor es "ping"; en ese caso, enviar como respuesta un datagrama con la
		 * cadena "pingok". Si el mensaje recibido no es "ping", se informa del error y
		 * se envía "invalid" como respuesta.
		 */
		
		/*
		 * TODO: (Boletín Estructura-NanoFiles) Ampliar el código para que, en el caso
		 * de que la cadena recibida no sea exactamente "ping", comprobar si comienza
		 * por "ping&" (es del tipo "ping&PROTOCOL_ID", donde PROTOCOL_ID será el
		 * identificador del protocolo diseñado por el grupo de prácticas (ver
		 * NanoFiles.PROTOCOL_ID). Se debe extraer el "protocol_id" de la cadena
		 * recibida y comprobar que su valor coincide con el de NanoFiles.PROTOCOL_ID,
		 * en cuyo caso se responderá con "welcome" (en otro caso, "denied").
		 */
		if (messageFromClient.equals("ping")) {
			responseString = "pingok";
		} else if (messageFromClient.startsWith("ping&")) {
			String[] parts = messageFromClient.split("&");
			if (parts.length > 1 && parts[1].equals(NanoFiles.PROTOCOL_ID)) {
				responseString = "welcome";
			} else {
				responseString = "denied";
			}
		} else {
			System.err.println("Error: Mensaje inesperado recibido (" + messageFromClient + ")");
			responseString = "invalid"; // [cite: 149]
		}

				byte[] responseBytes = responseString.getBytes();
				DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, pkt.getSocketAddress());
				socket.send(responsePacket);



	}

	public void run() throws IOException {

		System.out.println("Directory starting...");

		while (true) { 
			DatagramPacket rcvDatagram = receiveDatagram();

			sendResponse(rcvDatagram);

		}
	}

	private void sendResponse(DatagramPacket pkt) throws IOException {
		/*
		 * TODO: (Boletín MensajesASCII) Construir String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración. Después, usar la cadena para construir un objeto
		 * DirMessage que contenga en sus atributos los valores del mensaje. A partir de
		 * este objeto, se podrá obtener los valores de los campos del mensaje mediante
		 * métodos "getter" para procesar el mensaje y consultar/modificar el estado del
		 * servidor.
		 */
		String messageFromClient = new String(pkt.getData(), 0, pkt.getLength());
		System.out.println("Mensaje recibido en el servidor:\n" + messageFromClient);
				

		DirMessage receivedMsg = DirMessage.fromString(messageFromClient);
		/*
		 * TODO: Una vez construido un objeto DirMessage con el contenido del datagrama
		 * recibido, obtener el tipo de operación solicitada por el mensaje y actuar en
		 * consecuencia, enviando uno u otro tipo de mensaje en respuesta.
		 */
		String operation = receivedMsg.getOperation();
		DirMessage msgToSend = null;
		/*
		 * TODO: (Boletín MensajesASCII) Construir un objeto DirMessage (msgToSend) con
		 * la respuesta a enviar al cliente, en función del tipo de mensaje recibido,
		 * leyendo/modificando según sea necesario el "estado" guardado en el servidor
		 * de directorio (atributos files, etc.). Los atributos del objeto DirMessage
		 * contendrán los valores adecuados para los diferentes campos del mensaje a
		 * enviar como respuesta (operation, etc.)
		 */



		switch (operation) {
		case DirMessageOps.OPERATION_PING: {
			/*
			 * TODO: (Boletín MensajesASCII) Comprobamos si el protocolId del mensaje del
			 * cliente coincide con el nuestro.
			 */
			String clientProtocol = receivedMsg.getProtocolId();
			if (clientProtocol != null && clientProtocol.equals(NanoFiles.PROTOCOL_ID)) {

			/*
			 * TODO: (Boletín MensajesASCII) Construimos un mensaje de respuesta que indique
			 * el éxito/fracaso del ping (compatible, incompatible), y lo devolvemos como
			 * resultado del método.
			 */
				msgToSend = new DirMessage(DirMessageOps.OPERATION_WELCOME);
			} else {
				msgToSend = new DirMessage(DirMessageOps.OPERATION_DENIED);
			}
			/*
			 * TODO: (Boletín MensajesASCII) Imprimimos por pantalla el resultado de
			 * procesar la petición recibida (éxito o fracaso) con los datos relevantes, a
			 * modo de depuración en el servidor
			 */
			System.out.println("Resultado PING: " + msgToSend.getOperation());


			break;
		}
		
		case DirMessageOps.OPERATION_REGISTER: {
			int port = receivedMsg.getPort();
			InetSocketAddress serverAddr = new InetSocketAddress(pkt.getAddress(), port);
			
			String serverKey = receivedMsg.getNickname();
			registeredServers.put(serverKey, serverAddr);
			System.out.println("Nuevo servidor registrado en el directorio.");
			FileInfo[] filesFromClient = receivedMsg.getFiles();
			if (filesFromClient != null) {
				LinkedList<FileInfo> filesList = new LinkedList<>();
				for (FileInfo f : filesFromClient) {
					filesList.add(f);
				}
				publishedFiles.put(serverKey, filesList);
				System.out.println("Ficheros asociados al servidor correctamente.");
				} else {
				System.out.println("El servidor se registró pero no envió ningún fichero.");
			}
			msgToSend = new DirMessage(DirMessageOps.OPERATION_ACK);
			break;
		}
		case DirMessageOps.OPERATION_GETSERVERS: {
			String searchName = receivedMsg.getFilename();
			System.out.println("Procesando petición GETSERVERS...");
			msgToSend = new DirMessage(DirMessageOps.OPERATION_GETSERVERS);
			
			LinkedList<InetSocketAddress> serversWithFile = new LinkedList<>();
			
			for (String nickname : publishedFiles.keySet()) {
				LinkedList<FileInfo> filesOfThisServer = publishedFiles.get(nickname);
				boolean hasFile = false;
				
				if (filesOfThisServer != null) {
					for (FileInfo f : filesOfThisServer) {
						if (f.fileName != null && f.fileName.contains(searchName)) {
							hasFile = true;
							break; 
						}
					}
				}
				if (hasFile) {
					InetSocketAddress addr = registeredServers.get(nickname);
					if (addr != null) {
						serversWithFile.add(addr);
					}
				}
			}
			msgToSend.setServerList(serversWithFile.toArray(new InetSocketAddress[0]));
			System.out.println("Se encontraron " + serversWithFile.size() + " servidores con el fichero.");
			
			break;
		}
		case DirMessageOps.OPERATION_UNREGISTER: {
			String keyToRemove = null;
			for (String key : registeredServers.keySet()) {
				if (registeredServers.get(key).getAddress().equals(pkt.getAddress())) {
					keyToRemove = key;
					break;
				}
			}
			if (keyToRemove != null) {
				registeredServers.remove(keyToRemove);
				publishedFiles.remove(keyToRemove);
				System.out.println("Servidor [" + keyToRemove + "] dado de baja.");
			}
			msgToSend = new DirMessage(DirMessageOps.OPERATION_ACK);
			break;
		}
		case DirMessageOps.OPERATION_PEERLIST: {
			System.out.println("Petición de lista de peers recibida.");
			msgToSend = new DirMessage(DirMessageOps.OPERATION_PEERLIST);
			InetSocketAddress[] activePeers = registeredServers.values().toArray(new InetSocketAddress[0]);
			msgToSend.setServerList(activePeers);
			String listaNombres = String.join(",", registeredServers.keySet());
			msgToSend.setNickname(listaNombres);
			
			System.out.println("Enviando lista con " + activePeers.length + " peers activos.");
			break;
		}
		
		case DirMessageOps.OPERATION_GETSERVERINFO: {
			System.out.println("Petición de info de un peer recibida.");
			msgToSend = new DirMessage(DirMessageOps.OPERATION_GETSERVERINFO);
			
			String nicknameBuscado = receivedMsg.getNickname(); 
			
			if (nicknameBuscado != null && registeredServers.containsKey(nicknameBuscado)) {
				InetSocketAddress addr = registeredServers.get(nicknameBuscado);
				msgToSend.setServerList(new InetSocketAddress[]{addr});
				System.out.println("Peer " + nicknameBuscado + " encontrado. Devolviendo su IP.");
			} else {
				msgToSend.setServerList(new InetSocketAddress[0]); 
				System.out.println("Error: El peer " + nicknameBuscado + " no está registrado.");
			}
			break;
		}
		
		case DirMessageOps.OPERATION_FILELIST: {
			System.out.println("Petición de lista de TODOS los ficheros recibida.");
			msgToSend = new DirMessage(DirMessageOps.OPERATION_FILELIST);
			LinkedList<FileInfo> allFiles = new LinkedList<>();
			for (LinkedList<FileInfo> list : publishedFiles.values()) {
				if (list != null) {
					allFiles.addAll(list);
				}
			}
			msgToSend.setFiles(allFiles.toArray(new FileInfo[0]));
			
			System.out.println("Enviando un total de " + allFiles.size() + " ficheros.");
			break;
		}

		default:
			System.err.println("Unexpected message operation: \"" + operation + "\"");
			System.exit(-1);
		}
		

		/*
		 * TODO: (Boletín MensajesASCII) Convertir a String el objeto DirMessage
		 * (msgToSend) con el mensaje de respuesta a enviar, extraer los bytes en que se
		 * codifica el string y finalmente enviarlos en un datagrama
		 */

		if (msgToSend != null) {
			String responseString = msgToSend.toString();
			byte[] responseBytes = responseString.getBytes();
			DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, pkt.getSocketAddress());
			socket.send(responsePacket);
		}

	}
}
