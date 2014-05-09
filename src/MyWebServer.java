import java.io.*; // Get the Input Output libraries 
import java.net.*;
import java.util.Date;

// Get the Java networking libraries 

class webWorker extends Thread { // Class definition
	Socket sock; // Class member, socket, local to webwebwebWorker.

	webWorker(Socket s) {
		sock = s;
	} // Constructor, assign arg s to local sock

	public void run() {
		// Get I/O streams from the socket:
		PrintStream out = null;
		BufferedReader in = null;
		
		try {
			out = new PrintStream(sock.getOutputStream());
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			PrintStream pout = new PrintStream(out);

			// read first line of request (ignore the rest)
			String request = in.readLine();

			// parse the line
			if (!request.startsWith("GET")
					|| request.length() < 14
					|| !(request.endsWith("HTTP/1.0") || request
							.endsWith("HTTP/1.1"))) {
				// bad request
				errorReport(pout, sock, "400", "Bad Request",
						"Your browser sent a request that "
								+ "this server could not understand.");
			} else {
				String req = request.substring(4, request.length() - 9).trim();
				String req2 = request.substring(5, request.length() - 9).trim();
				if (req.indexOf("..") != -1 || req.indexOf("/.ht") != -1
						|| req.endsWith("~")) {
					// evil hacker trying to read non-wwwhome or secret file
					errorReport(pout, sock, "403", "Forbidden",
							"You don't have permission to access the requested URL.");
				}else{
					File f = new File(req2);
					InputStream file = new FileInputStream(f);
					
					
					
					
					File f1 = new File ( "parentdir/" ) ;
				    
				    // Get all the files and directory under your diretcory
				    File[] strFilesDirs = f1.listFiles ( );
				    
				    for ( int i = 0 ; i < strFilesDirs.length ; i ++ ) {
				      if ( strFilesDirs[i].isDirectory ( ) ) 
					out.println ("<h1>Index of /" + f1 + "</h1>" +
								"<a href="+ strFilesDirs[i] + "></a> <br>") ;
				      else if ( strFilesDirs[i].isFile ( ) )
					out.println ( "<a href="+ strFilesDirs[i] + "></a> <br>") ;
				    }
					
					

					String outputData = "HTTP/1.1 200 OK" + "\r\n"
							+ "Content-Type: " + contentType(req) + "\r\n"
							+ "Content-Lenght: : " + file.available() + "\r\n" 
							+ "\r\n\r\n";

					out.println(outputData);
					System.out.println(req);
					
					sendFileToClient(file, out); // send raw file
					log(sock, "200 OK");
					out.flush();
					sock.close();
				}
			}

		} catch (IOException ioe) {
			System.out.println(ioe);
		}

	}
	private static void listFileAndDir(String req ,PrintStream out ){
		String filedir ;
	    // Create a file object for your root directory

	    // E.g. For windows:    File f1 = new File ( "C:\\temp" ) ;

	    // For Unix:
	    File f1 = new File ( "./" ) ;
	    
	    // Get all the files and directory under your diretcory
	    File[] strFilesDirs = f1.listFiles ( );
	    
	    for ( int i = 0 ; i < strFilesDirs.length ; i ++ ) {
	      if ( strFilesDirs[i].isDirectory ( ) ) 
		out.println ( "Directory: " + strFilesDirs[i] ) ;
	      else if ( strFilesDirs[i].isFile ( ) )
		out.println ( "File: " + strFilesDirs[i] + 
				     " (" + strFilesDirs[i].length ( ) + ")" ) ;
	    }
	}

	private static void log(Socket sock, String msg) {
		System.err.println(new Date() + " ["
				+ sock.getInetAddress().getHostAddress() + ":" + sock.getPort()
				+ "] " + msg);
	}

	static void returnMimeToBrowser(String name, PrintStream out) {
		out.println(name);

	}

	private static String contentType(String path) {
		if (path.endsWith(".html") || path.endsWith(".htm"))
			return "text/html";
		else if (path.endsWith(".txt") || path.endsWith(".java"))
			return "text/plain";
		else if (path.endsWith(".gif"))
			return "image/gif";
		else if (path.endsWith(".class"))
			return "application/octet-stream";
		else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
			return "image/jpeg";
		else
			return "text/plain";
	}

	private static void sendFileToClient(InputStream file, OutputStream out) {
		try {
			byte[] buffer = new byte[1000];
			while (file.available() > 0)
				out.write(buffer, 0, file.read(buffer));
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	private static void errorReport(PrintStream pout, Socket sock, String code,
			String title, String msg) {
		pout.print("HTTP/1.0 " + code + " " + title + "\r\n" + "\r\n"
				+ "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n"
				+ "<TITLE>" + code + " " + title + "</TITLE>\r\n"
				+ "</HEAD><BODY>\r\n" + "<H1>" + title + "</H1>\r\n" + msg
				+ "<P>\r\n" + "<HR><ADDRESS>FileServer 1.0 at "
				+ sock.getLocalAddress().getHostName() + " Port "
				+ sock.getLocalPort() + "</ADDRESS>\r\n" + "</BODY></HTML>\r\n");
		log(sock, code + " " + title);
	}

}

public class MyWebServer {

	public static boolean controlSwitch = true;

	public static void main(String args[]) throws IOException {
		int q_len = 6; /* Number of requests for OpSys to queue */
		int port = 2540;
		Socket sock;

		ServerSocket servsock = new ServerSocket(port, q_len);
		System.out.println("Tu's server starting up, listening at port .\n" + port);
		while (controlSwitch) {
			// wait for the next client sock:
			sock = servsock.accept();
			new webWorker(sock).start(); // Uncomment to see shutdown bug:
			// try{Thread.sleep(10000);} catch(InterruptedException ex) {}
		}

	}
}