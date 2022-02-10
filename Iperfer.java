import java.io.DataInputStream;
import java.io.DataOutputStream;
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
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        )
        {
            long seconds = Integer.parseInt(args[6]);
            long counter = 0;
            long end = System.currentTimeMillis() + (seconds*1000);

            byte[] send = new byte[1000];
            while (System.currentTimeMillis() < end) {
                dos.write(send,0,1000);
                counter++;
            }

            double mbps = (counter * 8 * 1024) / (seconds * 1000000); 
            // kilobytes * 1024 --> bytes * 8 --> bits / 1000000 --> megabits / seconds --> megabits/sec
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
            InputStream in = new DataInputStream(clientSocket.getInputStream());
        ) 
        {
            //inside try block
            int rv;
            long bytes_read = 0;
            long start = System.currentTimeMillis();

            byte[] chunk = new byte[1000];
            while((rv = in.read(chunk)) > 0) {
                bytes_read += rv;
            }

            long end = System.currentTimeMillis();
            long duration = (end - start) / 1000;
            double megabits = (bytes_read * 8) / 1000000;
            double mbps = megabits / duration;
            double KB_read = bytes_read / 1024;

            System.out.println("recieved=" + (int) KB_read + " KB rate=" + mbps + " Mbps");

        } catch (IOException e) {
            System.exit(1);
        }

        return;
    }
}
