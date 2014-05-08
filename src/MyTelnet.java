import java.io.*;
import java.net.Socket;


public class MyTelnet {
	public static void main(String args[]) {
		String serverName;
		int port = 0;	
		if (args.length < 1)
			serverName = "localhost";
		else{
			serverName = args[0];
			port = Integer.parseInt(args [1]);
			}

		System.out.println("Tu's MyClient.\n");
		/* printLocalAddress (); */
		System.out.println("Using server: " + serverName + ", Port: 2540");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			String name;
			String host = null;
			do {
				System.out.print("Enter a message to server, (quit) to end: ");
				System.out.flush();
				//System.out.flush();
				name = in.readLine();
				//host = in.readLine();
				if (name.indexOf("quit") < 0)
					printMessageToServer(name, serverName , port , host);
			} while (name.indexOf("quit") < 0);
			System.out.println("Cancelled by user request.");
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	static void printMessageToServer(String name, String serverName , int port , String host) {
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		
		String outputData =
				  "GET /elliott/dog.txt HTTP/1.1 /" +		  
				  "Host: " + "condor.depaul.edu:80" + "\r\n" +
				  "Connection: keep-alive" + "\r\n" +
				  "\r\n\r\n";

		try {
			/*
			 * Open our connection to server port, choose your own port number..
			 */
			sock = new Socket(serverName, port);

			// Create filter I/O streams for the socket:
			fromServer = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
			toServer = new PrintStream(sock.getOutputStream());
								
			// Send machine name or IP address to server:
			/*toServer.append(name + "\n");
			toServer.append(host + "\n");
			toServer.append("connection: keep-alive \n");
			toServer.append("Date: " + new Date() + "\n");
			toServer.append("\n\n");
			toServer.flush();*/
			
			toServer.println(name);
			toServer.flush();

			// Read two or three lines of response from the server,
			// and block while synchronously waiting:
			for (int i = 1; i <= 10; i++) {
				textFromServer = fromServer.readLine();
				if (textFromServer != null)
					System.out.println(textFromServer);
			}

			sock.close();
		} catch (IOException x) {
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}
}