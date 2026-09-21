import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private String[] endpoints = {"/echo/{str}", "/user-agent"};
    private HttpRequest request;
    private int endPointIndex = -1;
    private Map<String, String> variables = new HashMap<>();

    public RequestHandler(HttpRequest request) {
        this.request = request;

        this.endPointIndex = this.matchPath();
    }

    public String handleRequest() {
        String body = "";
        String contentTypeHeader = "Content-Type: text/plain";
        String contentLengthHeader = null;
        String responseLine = "HTTP/1.1 200 OK";

        if (this.endPointIndex == 0) {
            body = this.variables.get("str");
        } else if (this.endPointIndex == 1) {
            String userAgent = request.getHeaders().get("User-Agent");
            body = userAgent;
        }

        contentLengthHeader = "Content-Length: " + body.length();

        return responseLine + HttpRequest.SEPARATOR + contentTypeHeader + HttpRequest.SEPARATOR + contentLengthHeader + HttpRequest.SEPARATOR + HttpRequest.SEPARATOR + body;
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
