import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Iperfer {
    public static void main(String[] args) {
 
        if (args.length > 0 && args[0].equals("-s")) {
            server(args);
        }
    }

    public static void client() {


        return;
    }

    public static void server(String[] args) {
        // command line checks
        if (args.length != 3) {
            System.out.println("Error: missing or additional arguments");
            System.exit(1);
        }
        int port = Integer.parseInt(args[2]);
        if (port < 1024 || port > 65535) {
            System.out.println("Error: port number must be in the range 1024 to 65535");
            System.exit(1);
        }

        // open server-side socket
        try ( 
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            InputStream in = clientSocket.getInputStream();
        ) 
        {
            //inside try block
            int rv;
            int bytes_read = 0;
            while((rv = in.read(new byte[1000])) != -1) {
                bytes_read += rv;
                // Ask TA: if connection is closed, won't I/O error prevent rv from updating?
            }
            

        } catch (IOException e) {
            System.exit(1);
        }

        return;
    }
}
