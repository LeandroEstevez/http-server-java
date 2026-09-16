import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    try {
       ServerSocket serverSocket = new ServerSocket(4221);

       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);

       Socket client = serverSocket.accept(); // Wait for connection from client.
       System.out.println("accepted new connection");

       InputStream inputStream = client.getInputStream();
       InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
       BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

       List<String> lineList = new ArrayList<>();
       String line = null;
       while (true) {
           line = bufferedReader.readLine();

           if (line == null || line.isEmpty()) {
               break;
           }

           lineList.add(line);
       }

       String requestLine = lineList.get(0);
       String[] requestLineSplit = requestLine.split(" ");

       String method = requestLineSplit[0];
       String requestTarget = requestLineSplit[1];
       String httpVersion = requestLineSplit[2];

       PrintWriter out = new PrintWriter(client.getOutputStream());

       if (requestTarget.equals("/")) {
           out.print("HTTP/1.1 200 OK\r\n\r\n");
       } else {
           out.print("HTTP/1.1 404 Not Found\r\n\r\n");
       }
       out.flush();

    } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
    }
  }
}
