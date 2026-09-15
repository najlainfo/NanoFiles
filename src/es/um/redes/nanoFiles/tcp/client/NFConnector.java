package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileDigest;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor
public class NFConnector {
	private Socket socket;
	private InetSocketAddress serverAddr;
	
	private DataInputStream dis;
	private DataOutputStream dos;


	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		serverAddr = fserverAddr;
		/*
		 * TODO: (Boletín SocketsTCP) Se crea el socket a partir de la dirección del
		 * servidor (IP, puerto). La creación exitosa del socket significa que la
		 * conexión TCP ha sido establecida.
		 */
		socket = new Socket(serverAddr.getAddress(), serverAddr.getPort());
		/*
		 * TODO: (Boletín SocketsTCP) Se crean los DataInputStream/DataOutputStream a
		 * partir de los streams de entrada/salida del socket creado. Se usarán para
		 * enviar (dos) y recibir (dis) datos del servidor.
		 */
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());


	}
	public void test() {
		/*
		 * TODO: (Boletín SocketsTCP) Enviar entero cualquiera a través del socket y
		 * después recibir otro entero, comprobando que se trata del mismo valor.
		 */

		try {
			String archivoDePrueba = "bir"; 
			System.out.println("Iniciando peticion de fichero...");
			PeerMessage request = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FILE, archivoDePrueba);
			request.writeMessageToOutputStream(dos);
			PeerMessage response = PeerMessage.readMessageFromInputStream(dis);
			
			if (response.getOpcode() == PeerMessageOps.OPCODE_FILE_DATA) {
				byte[] datos = response.getData();
				System.out.println(" Recibidos " + datos.length + " bytes.");

				FileOutputStream fos = new FileOutputStream("descarga_" + archivoDePrueba + ".dat");
				fos.write(datos);
				fos.close();
				System.out.println("Fichero guardado.");
			} else {
				System.out.println("Fichero no encontrado.");
			}
			
			dis.close();
			dos.close();
			socket.close();
			
		} catch (IOException e) {
			System.err.println("Error en la comunicación.");
		}
	}
	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

	
	public es.um.redes.nanoFiles.util.FileInfo[] getFileList() {
		try {
			PeerMessage request = new PeerMessage(PeerMessageOps.OPCODE_QUERY_FILES);
			request.writeMessageToOutputStream(dos);
			PeerMessage response = PeerMessage.readMessageFromInputStream(dis);
			
			if (response != null && response.getOpcode() == PeerMessageOps.OPCODE_FILE_LIST) {
				/*
				 * TODO: Si tu PeerMessage tuviera el método getFiles() funcionando, 
				 * aquí deberías devolver response.getFiles(). 
				 * 
				 * Como medida de emergencia para que el comando 'download' no de 
				 * NullPointerException, devolvemos una lista con un objeto FileInfo 
				 * inicializado.
				 */
				
				return response.getFiles();
			}
				
		} catch (IOException e) {
			System.err.println("Error al obtener la lista de ficheros del peer.");
		}
		return new es.um.redes.nanoFiles.util.FileInfo[0];
	}

public boolean downloadFile(String targetHash, File file) {
	boolean success = false;
	try {
		PeerMessage request = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FILE, targetHash);
		request.writeMessageToOutputStream(dos);
		PeerMessage response = PeerMessage.readMessageFromInputStream(dis);
		if (response.getOpcode() == PeerMessageOps.OPCODE_FILE_DATA) {
			byte[] datos = response.getData();
			File outputFile = new File(NanoFiles.sharedDirname, file.getName());
			FileOutputStream fos = new FileOutputStream(outputFile);
			fos.write(datos);
			fos.close();		
			success = true;
		} else {
			System.err.println("Fallo en la descarga: fichero no encontrado o hash incorrecto.");		}
	} catch (IOException e) {
		System.err.println("Error de red durante la descarga del fichero.");
	}
	return success;
}


public void close() {
	try {
		if (dis != null) dis.close();
		if (dos != null) dos.close();
		if (socket != null && !socket.isClosed()) socket.close();
		//System.out.println("Conexión cerrada perfectamente.");
	} catch (IOException e) {
		System.err.println("Error al cerrar el socket.");
	}
}
}


