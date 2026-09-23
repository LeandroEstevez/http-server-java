import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static String flag;
    public static String rootDir;

    static void main(String[] args) {
        if (args.length >= 2) {
            Main.flag = args[0];
            Main.rootDir = args[1];
        }

        try {
            ServerSocket serverSocket = new ServerSocket(4221);

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
            PrintWriter out = new PrintWriter(client.getOutputStream());
            StringBuilder requestTextBuilder = new StringBuilder();
            HttpRequest request;
            byte[] buffer = new byte[1024];
            byte current = -1;
            byte previous = -1;
            byte previous1 = -1;
            byte previous2 = -1;
            boolean bodyStart = false;
            int startOfBodyIndex = -1;
            int bodyBytesRead = 0;

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                startOfBodyIndex = bytesRead;

                for (int i = 0; i < bytesRead; i++) {
                    current = buffer[i];
                    if (previous2 == '\r' && previous1 == '\n' && previous == '\r' && current == '\n') {
                        startOfBodyIndex = i + 1;
                        bodyStart = true;
                        break;
                    }
                    previous2 = previous1;
                    previous1 = previous;
                    previous = current;
                }

                String text = new String(buffer, 0, startOfBodyIndex, StandardCharsets.US_ASCII);
                requestTextBuilder.append(text);

                if (bodyStart) {
                    text = requestTextBuilder.toString();
                    System.out.println("Start of the request: " + text);
                    String[] textLines = text.split("\r\n");
                    request = new HttpRequest(textLines);

                    if (request.getMethod().equals("POST")) {
                        String contentLengthStr = request.getHeaders().get("Content-Length");
                        requestTextBuilder = new StringBuilder();
                        int contentLength = Integer.valueOf(contentLengthStr);
                        bodyBytesRead = bytesRead - startOfBodyIndex;
                        text = new String(buffer, startOfBodyIndex, bodyBytesRead, StandardCharsets.US_ASCII);
                        requestTextBuilder.append(text);

                        while (bodyBytesRead < contentLength) {
                            int bte = inputStream.read();

                            if (bte == -1) {
                                break;
                            }

                            bodyBytesRead += 1;
                            requestTextBuilder.append((char) bte);
                        }

                        String body = requestTextBuilder.toString();
                        request.setBody(body);
                    }

                    RequestHandler requestHandler = new RequestHandler(request);
                    String response = requestHandler.handleRequest();

                    out.print(response);

                    out.flush();
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        /*
        try {
            InputStream inputStream = client.getInputStream();
            boolean requestReceieved = false;
            byte[] buffer = new byte[1024];
            byte current = -1;
            byte previous = -1;
            byte previous1 = -1;
            byte previous2 = -1;
            boolean startOfBody = false;
            StringBuilder requestTextBuilder = new StringBuilder();
            HttpRequest request = new HttpRequest();

            while (!requestReceieved) {
                int bytesRead = inputStream.read(buffer);
                int startOfBodyIndex = bytesRead;

                for (int i = 0; i < bytesRead; i++) {
                    current = buffer[i];
                    if (previous2 == '\r' && previous1 == '\n' && previous == '\r' && current == '\n') {
                        startOfBodyIndex = i + 1;
                        startOfBody = true;
                        break;
                    }
                    previous2 = previous1;
                    previous1 = previous;
                    previous = current;
                }

                String text = new String(buffer, 0, startOfBodyIndex, StandardCharsets.US_ASCII);
                requestTextBuilder.append(text);

                if (startOfBody) {
                    String firstPart = requestTextBuilder.toString();
                    String[] lineList = firstPart.split("\r\n");
                    request.parseFirstPart(lineList);

                    request.getHeaders
                }
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            PrintWriter out = new PrintWriter(client.getOutputStream());

            // Read lines from socket for request line + headers
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
        */
    }
}

