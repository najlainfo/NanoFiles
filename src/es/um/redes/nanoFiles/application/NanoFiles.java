package es.um.redes.nanoFiles.application;

import es.um.redes.nanoFiles.logic.NFController;
import es.um.redes.nanoFiles.udp.client.DirectoryConnector;
import es.um.redes.nanoFiles.util.FileDatabase;

public class NanoFiles {

	public static final String DEFAULT_SHARED_DIRNAME = "nf-shared";
	/**
	 * Identificador único para cada grupo de prácticas. TODO: Establecer a un valor
	 * que combine los DNIs de ambos miembros del grupo de prácticas.
	 */
	public static final String PROTOCOL_ID = "35702654F_48841570N";
	private static final String DEFAULT_DIRECTORY_HOSTNAME = "localhost";
	public static String sharedDirname = DEFAULT_SHARED_DIRNAME;
	public static FileDatabase db;
	/**
	 * Flag para pruebas iniciales con UDP, desactivado una vez que la comunicación
	 * cliente-directorio está implementada y probada.
	 */
	public static boolean testModeUDP = false;
	/**
	 * Flag para pruebas iniciales con TCP, desactivado una vez que la comunicación
	 * cliente-servidor de ficheros está implementada y probada.
	 */
	public static boolean testModeTCP = false;
	public static String testTargetFileHash;
	
	/**
	 * Conector para la comunicación con el servidor de directorio vía UDP.
	 */
	public static DirectoryConnector directoryConnector;
	public static String nickname; 
	public static String UserNickname = es.um.redes.nanoFiles.util.NickGenerator.randomNickname();

	public static void main(String[] args) {
		nickname = "nanoFiles";
		if (args.length > 1) {
			System.out.println("Usage: java -jar NanoFiles.jar [<local_shared_directory>]");
			return;
		} else if (args.length == 1) {
			sharedDirname = args[0];
		}

		db = new FileDatabase(sharedDirname);

		NFController controller = new NFController(DEFAULT_DIRECTORY_HOSTNAME);

		if (testModeUDP) {
			controller.testCommunication();
		} else {
			do {
				controller.readGeneralCommandFromShell();
				controller.processCommand();
			} while (controller.shouldQuit() == false);
			System.out.println("Bye.");
		}
	}
}
