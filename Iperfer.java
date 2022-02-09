import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Iperfer {
    public static void main(String[] args) {
 
        if (args.length > 0 && args[0].equals("-s")) {
            server(args);
        }

        else if (args.length > 0 && args[0].equals("-c")) {
            client(args);
        }

        return;
    }

    public static void client(String[] args) {

        if (args.length != 7) {
            System.out.println("Error: missing or additional arguments");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[4]);
        if (portNumber < 1024 || portNumber > 65535) {
            System.out.println("Error: port number must be in the range 1024 to 65535");
            System.exit(1);
        }

        String hostname = args[2];

        try (
            Socket socket = new Socket(hostname, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        )
        {
            long seconds = Integer.parseInt(args[6]);
            long counter = 0;
            long end = System.currentTimeMillis() + (seconds*1000);

            while (System.currentTimeMillis() < end) {
                out.print(new byte[1000]);
                counter++;
            }

            double mbps = (counter * 8) / (seconds * 1000); 
            // kilobytes * 1000 --> bytes * 8 --> bits / 1000000 --> megabits / seconds --> megabits/sec
            System.out.println("sent=" + counter + " KB rate=" + mbps + " Mbps");
        }

        catch (IOException e) { System.exit(1); }

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
            long bytes_read = 0;
            long start = System.currentTimeMillis();
            while((rv = in.read(new byte[1000])) != -1) {
                bytes_read += rv;
                // Ask TA: if connection is closed, won't I/O error prevent rv from updating?
            }
            long end = System.currentTimeMillis();
            long duration = (end - start) / 1000;
            double megabits = (bytes_read * 8) / 1000000;
            double mbps = megabits / duration;
            double KB_read = bytes_read / 1024;

            System.out.println("recieved=" + KB_read + " KB rate=" + mbps + " Mbps");
            

        } catch (IOException e) {
            System.exit(1);
        }

        return;
    }
}
