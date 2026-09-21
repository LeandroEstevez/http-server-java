import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static String flag;
    public static String rootDir;

    public static void main(String[] args) {

        if (args.length >= 2) {
            Main.flag = args[0];
            Main.rootDir = args[1];
        }

        try {
            ServerSocket serverSocket = new ServerSocket(4221);

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket client = serverSocket.accept();  // Wait for connection from client.

                Thread thread = new Thread(() -> {
                    handleConection(client);
                });
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void handleConection(Socket client) {
        try {
            InputStream inputStream = client.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            PrintWriter out = new PrintWriter(client.getOutputStream());

            // Read bytes from socket
            List<String> lineList = new ArrayList<>();
            String line;
            while (true) {
                line = bufferedReader.readLine();

                if (line == null || line.isEmpty()) {
                    break;
                }

                lineList.add(line);
            }

            // Parse and handle request
            HttpRequest request = new HttpRequest(lineList);
            RequestHandler requestHandler = new RequestHandler(request);
            String response = requestHandler.handleRequest();
            out.print(response);

            out.flush();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}

