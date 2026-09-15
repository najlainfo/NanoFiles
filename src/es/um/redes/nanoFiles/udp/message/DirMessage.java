package es.um.redes.nanoFiles.udp.message;
import es.um.redes.nanoFiles.util.FileInfo;




/**
 * Clase que modela los mensajes del protocolo de comunicación entre pares para
 * implementar el explorador de ficheros remoto (servidor de ficheros). Estos
 * mensajes son intercambiados entre las clases DirectoryServer y
 * DirectoryConnector, y se codifican como texto en formato "campo:valor".
 * 
 * @author rtitos
 *
 */
public class DirMessage {
	public static final int PACKET_MAX_SIZE = 65507; 

	private static final char DELIMITER = ':'; 
	private static final char END_LINE = '\n'; 

	/**
	 * Nombre del campo que define el tipo de mensaje (primera línea)
	 */
	private static final String FIELDNAME_OPERATION = "operation";
	/*
	 * TODO: (Boletín MensajesASCII) Definir de manera simbólica los nombres de
	 * todos los campos que pueden aparecer en los mensajes de este protocolo
	 * (formato campo:valor)
	 */

		private static final String FIELDNAME_PROTOCOL = "protocol";
		private static final String FIELDNAME_PORT = "port";
		private static final String FIELDNAME_FILES = "files";
		private static final String FIELDNAME_FILENAME = "filename";
		private static final String FIELDNAME_PEERS = "peers";
		private static final String FIELDNAME_NICKNAME = "nickname";

	/**
	 * Tipo del mensaje, de entre los tipos definidos en PeerMessageOps.
	 */
	private String operation = DirMessageOps.OPERATION_INVALID;
	/**
	 * Identificador de protocolo usado, para comprobar compatibilidad del directorio.
	 */
	private String protocolId;
	/*
	 * TODO: (Boletín MensajesASCII) Crear un atributo correspondiente a cada uno de
	 * los campos de los diferentes mensajes de este protocolo.
	 */


	private int port;
	private FileInfo[] files;
	private java.net.InetSocketAddress[] serverList;

    public java.net.InetSocketAddress[] getServerList() {
        return serverList;
    }

    public void setServerList(java.net.InetSocketAddress[] serverList) {
        this.serverList = serverList;
    }
	private String filename;
	private String nickname;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public DirMessage(String op) {
		operation = op;
	}

	/*
	 * TODO: (Boletín MensajesASCII) Crear diferentes constructores adecuados para
	 * construir mensajes de diferentes tipos con sus correspondientes argumentos
	 * (campos del mensaje)
	 */

	public String getOperation() {
		return operation;
	}

	/*
	 * TODO: (Boletín MensajesASCII) Crear métodos getter y setter para obtener los
	 * valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public void setProtocolID(String protocolIdent) {
		protocolId = protocolIdent;
	}

	public String getProtocolId() {
		return protocolId;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setFiles(FileInfo[] files) {
		this.files = files;
	}

	public FileInfo[] getFiles() {
		return files;
	}
	public void setFilename(String name) {
		this.filename = name;
	}

	public String getFilename() {
		return filename;
	}



	/**
	 * Método que convierte un mensaje codificado como una cadena de caracteres, a
	 * un objeto de la clase PeerMessage, en el cual los atributos correspondientes
	 * han sido establecidos con el valor de los campos del mensaje.
	 * 
	 * @param message El mensaje recibido por el socket, como cadena de caracteres
	 * @return Un objeto PeerMessage que modela el mensaje recibido (tipo, valores,
	 *         etc.)
	 */
	public static DirMessage fromString(String message) {
		/*
		 * TODO: (Boletín MensajesASCII) Usar un bucle para parsear el mensaje línea a
		 * línea, extrayendo para cada línea el nombre del campo y el valor, usando el
		 * delimitador DELIMITER, y guardarlo en variables locales.
		 */

		// System.out.println("DirMessage read from socket:");
		// System.out.println(message);
		String[] lines = message.split(END_LINE + "");
		DirMessage m = null;



		for (String line : lines) {
			if (line.trim().isEmpty()) continue; 
			int idx = line.indexOf(DELIMITER); 
			if (idx == -1) continue; 
			String fieldName = line.substring(0, idx).toLowerCase(); 
			String value = line.substring(idx + 1).trim();

			switch (fieldName) {
			case FIELDNAME_OPERATION:
				assert (m == null);
				m = new DirMessage(value);
				break;

			case FIELDNAME_PROTOCOL:
				if (m != null) m.setProtocolID(value);
				break;

			case FIELDNAME_PORT:
				if (m != null) m.setPort(Integer.parseInt(value));
				break;

			case FIELDNAME_FILENAME:
				if (m != null) m.setFilename(value);
				break;

			case FIELDNAME_FILES:
				if (m != null) {
					if (value.equals("0") || value.trim().isEmpty()) {
						m.setFiles(new FileInfo[0]);
					} else {
						String[] fileTokens = value.split("\\?");
						FileInfo[] parsedFiles = new FileInfo[fileTokens.length];
						
						for (int i = 0; i < fileTokens.length; i++) {
							String[] fData = fileTokens[i].split("\\*");
							if (fData.length >= 3) {
								String hash = fData[0];
								String name = fData[1];
								long size = Long.parseLong(fData[2]);
								parsedFiles[i] = new FileInfo(hash, name, size, "");
							}
						}
						m.setFiles(parsedFiles);
					}
				}
				break;
			case FIELDNAME_PEERS:
				if (m != null) {
					if (value.equals("0") || value.trim().isEmpty()) {
						m.setServerList(new java.net.InetSocketAddress[0]);
					} else {
						String[] peerTokens = value.split(",");
						java.net.InetSocketAddress[] parsedPeers = new java.net.InetSocketAddress[peerTokens.length];
						for (int i = 0; i < peerTokens.length; i++) {
							String peerStr = peerTokens[i].trim();
							if (peerStr.startsWith("/")) peerStr = peerStr.substring(1);
							String[] parts = peerStr.split(":");
							parsedPeers[i] = new java.net.InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
						}
						m.setServerList(parsedPeers);
					}
				}
				break;
			case FIELDNAME_NICKNAME:
				if (m != null) m.setNickname(value);
				break;

			default:
				System.err.println("PANIC: DirMessage.fromString - message with unknown field name " + fieldName);
				System.err.println("Message was:\n" + message);
				System.exit(-1);
			} 
		} 

		return m;
	}	

	/**
	 * Método que devuelve una cadena de caracteres con la codificación del mensaje
	 * según el formato campo:valor, a partir del tipo y los valores almacenados en
	 * los atributos.
	 * 
	 * @return La cadena de caracteres con el mensaje a enviar por el socket.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); 

		if (operation.equals(DirMessageOps.OPERATION_PING)) {
			if (protocolId != null) {
				sb.append(FIELDNAME_PROTOCOL + DELIMITER + protocolId + END_LINE);
			}
		} 
		else if (operation.equals(DirMessageOps.OPERATION_REGISTER)) {
			sb.append(FIELDNAME_PORT + DELIMITER + port + END_LINE);
		}

		if (files != null) {
			if (files.length > 0) {
				sb.append(FIELDNAME_FILES + DELIMITER);
				for (int i = 0; i < files.length; i++) {
					FileInfo f = files[i];
					sb.append(f.fileHash + "*" + f.fileName + "*" + f.fileSize);
					if (i < files.length - 1) {
						sb.append("?"); 
					}
				}
				sb.append(END_LINE);
			} else {
				sb.append(FIELDNAME_FILES + DELIMITER + "0" + END_LINE);
			}
		}
		
		if (filename != null) {
			sb.append(FIELDNAME_FILENAME + DELIMITER + filename + END_LINE);
		}
		
		if (serverList != null) {
            sb.append(FIELDNAME_PEERS + DELIMITER);
            for (int i = 0; i < serverList.length; i++) {
                sb.append(serverList[i].toString());
                if (i < serverList.length - 1) sb.append(",");
            }
            sb.append(END_LINE);
        }	
		if (nickname != null) {
			sb.append(FIELDNAME_NICKNAME + DELIMITER + nickname + END_LINE);
		}

		sb.append(END_LINE); 
		
		return sb.toString();
	}
}

