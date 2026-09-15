package es.um.redes.nanoFiles.tcp.server;

import java.net.Socket;

public class NFServerThread extends Thread {
	/*
	 * TODO: Esta clase modela los hilos que son creados desde NFServer y cada uno
	 * de los cuales simplemente se encarga de invocar a
	 * NFServer.serveFilesToClient con el socket retornado por el método accept
	 * (un socket distinto para "conversar" con un cliente)
	 */

		private Socket clientSocket;
		public NFServerThread(Socket socket) {
			this.clientSocket = socket;
		}

		// El método que se ejecuta cuando hacemos clientThread.start()
		@Override
		public void run() {
			//System.out.println("[Hilo-Secundario] Iniciando atención al cliente en un nuevo hilo...");
			NFServer.serveFilesToClient(clientSocket);
			//System.out.println("[Hilo-Secundario] Conexión con el cliente finalizada. Cerrando hilo.");
		}


}
