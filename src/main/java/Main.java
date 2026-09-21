import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(4221);

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            Socket client = serverSocket.accept(); // Wait for connection from client.

            InputStream inputStream = client.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            PrintWriter out = new PrintWriter(client.getOutputStream());

            // Read bytes from socket
            List<String> lineList = new ArrayList<>();
            String line = null;
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

            // Return appropriate response
            if (request.getRequestTarget() != null && request.getRequestTarget().equals("/")) {
                out.print("HTTP/1.1 200 OK\r\n\r\n");
            } else if (requestHandler.getEndPointIndex() == -1) {
                out.print("HTTP/1.1 404 Not Found\r\n\r\n");
            } else {
                String response = requestHandler.handleRequest();
                out.print(response);
            }

            out.flush();

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}

