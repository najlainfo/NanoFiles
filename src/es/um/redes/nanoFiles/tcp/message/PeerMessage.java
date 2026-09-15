package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import es.um.redes.nanoFiles.util.FileInfo;

public class PeerMessage {

	private byte opcode;

	/*
	 * TODO: (Boletín MensajesBinarios) Añadir atributos u otros constructores
	 * específicos para crear mensajes con otros campos, según sea necesario
	 * 
	 */
	private String hash;       
	private byte[] data;       
	private FileInfo[] files;  



	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}
	
		public PeerMessage(byte op, String hash) {
			this.opcode = op;
			this.hash = hash;
		}
		
		public PeerMessage(byte op, byte[] data) {
			this.opcode = op;
			this.data = data;
		}

	/*
	 * TODO: (Boletín MensajesBinarios) Crear métodos getter y setter para obtener
	 * los valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public byte getOpcode() {
		return opcode;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getHash() {
		return hash;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void setFiles(FileInfo[] files) {
		this.files = files;
	}

	public FileInfo[] getFiles() {
		return files;
	}


	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: (Boletín MensajesBinarios) En función del tipo de mensaje, leer del
		 * socket a través del "dis" el resto de campos para ir extrayendo con los
		 * valores y establecer los atributos del un objeto DirMessage que contendrá
		 * toda la información del mensaje, y que será devuelto como resultado. NOTA:
		 * Usar dis.readFully para leer un array de bytes, dis.readInt para leer un
		 * entero, etc.
		 */
		

		byte opcode = dis.readByte();
		PeerMessage message = new PeerMessage(opcode);
		switch (opcode) {
		case PeerMessageOps.OPCODE_QUERY_FILES:
		case PeerMessageOps.OPCODE_FILE_NOT_FOUND:
			break;

		case PeerMessageOps.OPCODE_DOWNLOAD_FILE:
			int hashLen = dis.readInt();
			byte[] hashBytes = new byte[hashLen];
			dis.readFully(hashBytes);
			message.setHash(new String(hashBytes));
			break;

		case PeerMessageOps.OPCODE_FILE_DATA:
			int dataLen = dis.readInt();
			byte[] dataBytes = new byte[dataLen];
			dis.readFully(dataBytes);
			message.setData(dataBytes);
			break;
			
		case PeerMessageOps.OPCODE_FILE_LIST:
			int numFiles = dis.readInt(); 
			FileInfo[] list = new FileInfo[numFiles];
			for (int i = 0; i < numFiles; i++) {
				String fHash = dis.readUTF();
				String fName = dis.readUTF();
				long fSize = dis.readLong();
				list[i] = new FileInfo(fHash, fName, fSize, "");
			}
			message.setFiles(list);
			break;

		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO (Boletín MensajesBinarios): Escribir los bytes en los que se codifica el
		 * mensaje en el socket a través del "dos", teniendo en cuenta opcode del
		 * mensaje del que se trata y los campos relevantes en cada caso. NOTA: Usar
		 * dos.write para leer un array de bytes, dos.writeInt para escribir un entero,
		 * etc.
		 */
		

		dos.writeByte(opcode);
		switch (opcode) {
		case PeerMessageOps.OPCODE_QUERY_FILES:
		case PeerMessageOps.OPCODE_FILE_NOT_FOUND:
			break;

		case PeerMessageOps.OPCODE_DOWNLOAD_FILE:
			byte[] hashBytes = hash.getBytes();
			dos.writeInt(hashBytes.length);
			dos.write(hashBytes);
			break;

		case PeerMessageOps.OPCODE_FILE_DATA:
			dos.writeInt(data.length);
			dos.write(data);
			break;
			
		case PeerMessageOps.OPCODE_FILE_LIST:
			if (files == null) {
				dos.writeInt(0);
			} else {
				dos.writeInt(files.length); 
				for (FileInfo f : files) {
					dos.writeUTF(f.fileHash); 
					dos.writeUTF(f.fileName); 
					dos.writeLong(f.fileSize); 
				}
			}
			break;

		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}




}
