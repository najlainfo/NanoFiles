package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PeerMessageTest {

	public static void main(String[] args) throws IOException {
		String nombreArchivo = "peermsg.bin";
		DataOutputStream fos = new DataOutputStream(new FileOutputStream(nombreArchivo));

		/*
		 * TODO: Probar a crear diferentes tipos de mensajes (con los opcodes válidos
		 * definidos en PeerMessageOps), estableciendo los atributos adecuados a cada
		 * tipo de mensaje. Luego, escribir el mensaje a un fichero con
		 * writeMessageToOutputStream para comprobar que readMessageFromInputStream
		 * construye un mensaje idéntico al original.
		 */
		
		PeerMessage msgOut = new PeerMessage(PeerMessageOps.OPCODE_QUERY_FILES);
		msgOut.writeMessageToOutputStream(fos);
		
		PeerMessage msgOutDownload = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FILE, "hash_prueba_123");
		msgOutDownload.writeMessageToOutputStream(fos);
		
		byte[] datosPrueba = {10, 20, 30, 40, 50};
		PeerMessage msgOutData = new PeerMessage(PeerMessageOps.OPCODE_FILE_DATA, datosPrueba);
		msgOutData.writeMessageToOutputStream(fos);

		fos.close(); 

		DataInputStream fis = new DataInputStream(new FileInputStream(nombreArchivo));
		
		PeerMessage msgIn = PeerMessage.readMessageFromInputStream((DataInputStream) fis);
		PeerMessage msgInDownload = PeerMessage.readMessageFromInputStream(fis);
		PeerMessage msgInData = PeerMessage.readMessageFromInputStream(fis);
		
		fis.close();

		/*
		 * TODO: Comprobar que coinciden los valores de los atributos relevantes al tipo
		 * de mensaje en ambos mensajes (msgOut y msgIn), empezando por el opcode.
		 */
		System.out.println("Comprobando mensajes...");
		
		if (msgOut.getOpcode() != msgIn.getOpcode()) {
			System.err.println("Opcode does not match!");
		} else {
			System.out.println("Mensaje base OK.");
		}
		
		if (msgOutDownload.getOpcode() != msgInDownload.getOpcode()) {
			System.err.println("Opcode does not match for DOWNLOAD_FILE!");
		} else if (!msgOutDownload.getHash().equals(msgInDownload.getHash())) {
			System.err.println("Hash does not match!");
		} else {
			System.out.println("Mensaje DOWNLOAD_FILE OK.");
		}
		
		if (msgOutData.getOpcode() != msgInData.getOpcode()) {
			System.err.println("Opcode does not match for FILE_DATA!");
		} else if (!Arrays.equals(msgOutData.getData(), msgInData.getData())) {
			System.err.println("Data array does not match!");
		} else {
			System.out.println("Mensaje FILE_DATA OK.");
		}
	}

}