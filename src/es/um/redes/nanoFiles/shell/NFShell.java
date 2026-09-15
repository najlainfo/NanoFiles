package es.um.redes.nanoFiles.shell;

import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

import es.um.redes.nanoFiles.application.NanoFiles;

public class NFShell {
	/**
	 * Scanner para leer comandos de usuario de la entrada estándar
	 */
	private Scanner reader;

	byte command = NFCommands.COM_INVALID;
	String[] commandArgs = new String[0];

	boolean enableComSocketIn = false;
	private boolean skipValidateArgs;

	/*
	 * Testing-related: print command to stdout (when reading commands from stdin)
	 */
	public static final String FILENAME_TEST_SHELL = ".nanofiles-test-shell";
	public static boolean enableVerboseShell = false;

	public NFShell() {
		reader = new Scanner(System.in);
		
		System.out.println("* Peer nickname: " + NanoFiles.UserNickname);

		System.out.println("NanoFiles shell");
		System.out.println("For help, type 'help'");
	}

	// devuelve el comando introducido por el usuario
	public byte getCommand() {
		return command;
	}

	// Devuelve los parámetros proporcionados por el usuario para el comando actual
	public String[] getCommandArguments() {
		return commandArgs;
	}

	// Espera hasta obtener un comando válido entre los comandos existentes
	public void readGeneralCommand() {
		boolean validArgs;
		do {
			commandArgs = readGeneralCommandFromStdIn();
			// si el comando tiene parámetros hay que validarlos
			validArgs = validateCommandArguments(commandArgs);
		} while (!validArgs);
	}

	public String chooseDirectory(String defaultDirectory) {
		char response;
		String directory = null;
		do {
			System.out.print(
					"Do you want to use '" + defaultDirectory + "' as location of the directory server? (y/n): ");
			String input = reader.nextLine().trim().toLowerCase();
			if (input.length() == 1) { // Verificar que la entrada es un solo carácter
				response = input.charAt(0);
				if (response == 'y') {
					directory = defaultDirectory;
				} else if (response == 'n') {
					System.out.print("Enter the directory hostname/IP:");
					directory = reader.nextLine().trim().toLowerCase();
				} else {
					System.out.println("Invalid key! Please, answer 'y' or 'n'.");
				}
			}
		} while (directory == null);
		System.out.println("Using directory location: " + directory);
		return directory;
	}

	private String[] readGeneralCommandFromStdIn() {
		String[] args = new String[0];
		Vector<String> vargs = new Vector<String>();
		while (true) {
			System.out.print("(nanoFiles@" + NanoFiles.sharedDirname + ") ");
			String input = reader.nextLine();
			StringTokenizer st = new StringTokenizer(input);
			if (st.hasMoreTokens() == false) {
				continue;
			}
			command = NFCommands.stringToCommand(st.nextToken());
			if (enableVerboseShell) {
				System.out.println(input);
			}
			skipValidateArgs = false;
						switch (command) {
						case NFCommands.COM_INVALID:
							System.out.println("Invalid command");
							continue;
						case NFCommands.COM_HELP:
							NFCommands.printCommandsHelp();
							continue;
						case NFCommands.COM_QUIT:
						case NFCommands.COM_FILELIST:
						case NFCommands.COM_MYFILES:
						case NFCommands.COM_SERVE:
						case NFCommands.COM_PING:
						case NFCommands.COM_DIRFILES:
						case NFCommands.COM_PEERS:
							break;
						case NFCommands.COM_DOWNLOAD:
						case NFCommands.COM_UPLOAD:
						case NFCommands.COM_DIRDL:     
						case NFCommands.COM_PEERFILES:  
						case NFCommands.COM_NICK: 
						case NFCommands.COM_PEERDL:    
							while (st.hasMoreTokens()) {
								vargs.add(st.nextToken());
							}
							break;
						default:
							skipValidateArgs = true;
							System.out.println("Invalid command");
							;
						}
			break;
		}
		return vargs.toArray(args);
	}

	private boolean validateCommandArguments(String[] args) {
		if (skipValidateArgs)
			return false;
		switch (this.command) {
		case NFCommands.COM_DIRDL:    
		case NFCommands.COM_PEERFILES: 
			if (args.length != 1) {
				System.out.println(
						"Correct use: " + NFCommands.commandToString(command) + " <argument>");
				return false;
			}
			break;
		case NFCommands.COM_PEERDL:    
			if (args.length != 2) {
				System.out.println(
						"Correct use: " + NFCommands.commandToString(command) + " <peer_nickname> <hash_substring>");
				return false;
			}
			break;
		case NFCommands.COM_NICK:
		    if (args.length != 1) {
		        System.out.println("Correct use:" + NFCommands.commandToString(command) +  "<nuevo_nombre>");
		        return false; 
		    }
		    break;
		default:
		}
		return true;
	}

	public static void enableVerboseShell() {
		enableVerboseShell = true;
	}
}
