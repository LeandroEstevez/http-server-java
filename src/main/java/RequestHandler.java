import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private String[] endpoints = {"/echo/{str}", "/user-agent", "/files/{filename}"};
    private HttpRequest request;
    private int endPointIndex = -1;
    private Map<String, String> variables = new HashMap<>();

    public RequestHandler(HttpRequest request) {
        this.request = request;

        this.endPointIndex = this.matchPath();
    }

    public String handleRequest() {
        String body = null;
        String contentTypeHeader = null;
        String contentLengthHeader = null;
        String responseLine = "HTTP/1.1 200 OK";

        if (this.request.getRequestTarget() != null && this.request.getRequestTarget().equals("/")) {
            responseLine = "HTTP/1.1 200 OK\r\n\r\n";
            return responseLine;
        } else if (this.endPointIndex == -1) {
            responseLine = "HTTP/1.1 404 Not Found\r\n\r\n";
            return responseLine;
        } else {
            if (this.endPointIndex == 0) {
                body = this.variables.get("str");
                contentTypeHeader = "Content-Type: text/plain";
                contentLengthHeader = "Content-Length: " + body.length();
            } else if (this.endPointIndex == 1) {
                String userAgent = request.getHeaders().get("User-Agent");
                body = userAgent;
                contentTypeHeader = "Content-Type: text/plain";
                contentLengthHeader = "Content-Length: " + body.length();
            } else if (this.endPointIndex == 2) {
                File file = new File(Main.rootDir + this.variables.get("filename"));

                if (file.exists() == false) {
                    responseLine = "HTTP/1.1 404 Not Found\r\n\r\n";
                    return responseLine;
                }

                contentTypeHeader = "Content-Type: application/octet-stream";

                StringBuilder stringBuilder = new StringBuilder();

                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }
                } catch (IOException e) {
                    System.out.println("Error opening the file: " + e.getMessage());
                }

                body = stringBuilder.toString();
                contentLengthHeader = "Content-Length: " + file.length();
            }

            return responseLine + HttpRequest.SEPARATOR + contentTypeHeader + HttpRequest.SEPARATOR + contentLengthHeader + HttpRequest.SEPARATOR + HttpRequest.SEPARATOR + body;
        }
    }

    public int matchPath() {
        String requestTarget = this.request.getRequestTarget();

        if (requestTarget != null && requestTarget.length() != 0) {
            String[] requestTargetSplit = requestTarget.split("/");

            int indexMatch = -1;

            for (int j = 0; j < endpoints.length; j++) {
                String endpoint = endpoints[j];

                String[] endpointSplit = endpoint.split("/");

                if (endpointSplit.length != requestTargetSplit.length) {
                    continue;
                }

                if (endpointSplit[0].equals(requestTargetSplit[0])) {
                    int i = 0;

                    while (i < endpointSplit.length && i < requestTargetSplit.length) {
                        if (endpointSplit[i].equals(requestTargetSplit[i])) {
                            i++;
                        } else if (endpointSplit[i].startsWith("{")) {
                            String variableName = endpointSplit[i].substring(1, endpointSplit[i].length() - 1);
                            this.variables.put(variableName, requestTargetSplit[i]);
                            i++;
                        } else {
                            break;
                        }
                    }

                    if (i == endpointSplit.length && i == requestTargetSplit.length) {
                        indexMatch = j;
                        break;
                    }
                }
            }

            return indexMatch;
        }

        return -1;
    }

    public String[] getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(String[] endpoints) {
        this.endpoints = endpoints;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public int getEndPointIndex() {
        return endPointIndex;
    }

    public void setEndPointIndex(int endPointIndex) {
        this.endPointIndex = endPointIndex;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
}
