package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * TODO: (Boletín MensajesASCII) Añadir aquí todas las constantes que definen
	 * los diferentes tipos de mensajes del protocolo de comunicación con el
	 * directorio (valores posibles del campo "operation").
	 */
	public static final String OPERATION_INVALID = "invalid_operation";
	public static final String OPERATION_PING = "ping";
	public static final String OPERATION_REGISTER = "register";
	public static final String OPERATION_UNREGISTER = "unregister";
	public static final String OPERATION_FILELIST = "filelist";
	public static final String OPERATION_GETSERVERS = "getservers";
	public static final String OPERATION_GETSERVERINFO = "getserverinfo";
	public static final String OPERATION_WELCOME = "welcome";
	public static final String OPERATION_DENIED = "denied";
	public static final String OPERATION_ACK = "ack"; 
	public static final String OPERATION_PEERLIST = "peerlist";
}
