package es.um.redes.nanoFiles.shell;

public class NFCommands {
	/**
	 * Códigos para todos los comandos soportados por el shell
	 */
	public static final byte COM_INVALID = 0;
	public static final byte COM_QUIT = 1;
	public static final byte COM_PING = 2;
	public static final byte COM_FILELIST = 4;
	public static final byte COM_DIRFILES = 5;
	public static final byte COM_MYFILES = 6;
	public static final byte COM_PEERS = 7;
	public static final byte COM_PEERFILES = 8;
	public static final byte COM_SERVE = 11;
	public static final byte COM_DOWNLOAD = 25;
	public static final byte COM_DIRDL = 26;
	public static final byte COM_PEERDL = 27;
	public static final byte COM_UPLOAD = 30;
	public static final byte COM_NICK = 32;
	public static final byte COM_HELP = 50;
	public static final byte COM_SOCKET_IN = 100;
	

	
	/**
	 * Códigos de los comandos válidos que puede
	 * introducir el usuario del shell. El orden
	 * es importante para relacionarlos con la cadena
	 * que debe introducir el usuario y con la ayuda
	 */
	private static final Byte[] _valid_user_commands = { 
			COM_QUIT,
			COM_PING,
			COM_FILELIST,
			COM_DIRFILES,
			COM_MYFILES,
			COM_PEERS,
			COM_PEERFILES,
			COM_SERVE,
			COM_DIRDL,
			COM_PEERDL,
			COM_NICK,
			COM_HELP,
			COM_SOCKET_IN
			};	
	/**
	 * cadena exacta de cada orden
	 */
	private static final String[] _valid_user_commands_str = {
			"quit",
			"ping",
			"filelist",
			"dirfiles",
			"myfiles",
			"peers",
			"peerfiles",
			"serve",
			"dirdl",
			"peerdl",
			"nick",
			"help"
		};

	/**
	 * Mensaje de ayuda para cada orden
	 */
	private static final String[] _valid_user_commands_help = {
			"quit the application",
			"ping directory to check protocol compatibility",
			"show list of files tracked by the directory",
			"show list of files in the directory",
			"show contents of local folder (files that may be served)",
			"show list of active peers registered in directory",
			"show list of files available in a specific peer",
			"run file server and publish served files to directory",
			"download file from directory",
			"download file from a specific peer",
			"change local nickname before serving files",
			"shows this information"
			};

	/**
	 * Transforma una cadena introducida en el código de comando correspondiente
	 */
	public static byte stringToCommand(String comStr) {
		for (int i = 0;
		i < _valid_user_commands_str.length; i++) {
			if (_valid_user_commands_str[i].equalsIgnoreCase(comStr)) {
				return _valid_user_commands[i];
			}
		}
		return COM_INVALID;
	}

	public static String commandToString(byte command) {
		for (int i = 0;
		i < _valid_user_commands.length; i++) {
			if (_valid_user_commands[i] == command) {
				return _valid_user_commands_str[i];
			}
		}
		return null;
	}

	/**
	 * Imprime la lista de comandos y la ayuda de cada uno
	 */
	public static void printCommandsHelp() {
		System.out.println("List of commands:");
		for (int i = 0; i < _valid_user_commands_str.length; i++) {
			System.out.println(String.format("%1$15s", _valid_user_commands_str[i]) + " -- "
					+ _valid_user_commands_help[i]);
		}		
	}
}	

