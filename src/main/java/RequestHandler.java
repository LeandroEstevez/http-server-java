import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private String[] endpoints = {"/echo/{str}", "/user-agent", "/files/{filename}", "/"};
    private HttpRequest request;
    private int endPointIndex = -1;
    private Map<String, String> pathVariables = new HashMap<>();
    private HttpResponse httpResponse;

    public RequestHandler(HttpRequest request) {
        this.request = request;

        this.endPointIndex = this.matchPath();
    }

    public void handleRequest() {
        this.httpResponse = new HttpResponse();
        httpResponse.setEncodingHeader(this.request.getHeaders().get("Accept-Encoding"));

        if (this.request.getHeaders().get("Connection") != null) {
            httpResponse.getHeaders().put("Connection", "Close");
        }

        if (this.endPointIndex == -1) {
            httpResponse.setResponseCode("404");
            httpResponse.setReasonPhrase("Not Found");
        } else {
            if (this.endPointIndex == 0) {
                httpResponse.setResponseCode("200");
                httpResponse.setReasonPhrase("OK");
                httpResponse.setOriginalBody(this.pathVariables.get("str"));
                httpResponse.getHeaders().put("Content-Type", "text/plain");
                httpResponse.getHeaders().put("Content-Length", String.valueOf(httpResponse.getOriginalBody().length()));
            } else if (this.endPointIndex == 1) {
                String userAgent = request.getHeaders().get("User-Agent");
                httpResponse.setResponseCode("200");
                httpResponse.setReasonPhrase("OK");
                httpResponse.setOriginalBody(userAgent);
                httpResponse.getHeaders().put("Content-Type", "text/plain");
                httpResponse.getHeaders().put("Content-Length", String.valueOf(httpResponse.getOriginalBody().length()));
            } else if (this.endPointIndex == 2) {
                File file = new File(Main.rootDir + this.pathVariables.get("filename"));

                if (this.request.getMethod().equals("GET")) {
                    if (file.exists() == false) {
                        httpResponse.setResponseCode("404");
                        httpResponse.setReasonPhrase("Not Found");

                        httpResponse.buildHeaders();

                        return;
                    }

                    httpResponse.getHeaders().put("Content-Type", "application/octet-stream");

                    StringBuilder stringBuilder = new StringBuilder();

                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line + "\n");
                        }
                    } catch (IOException e) {
                        System.out.println("Error opening the file: " + e.getMessage());
                    }

                    httpResponse.setResponseCode("200");
                    httpResponse.setReasonPhrase("OK");
                    httpResponse.setOriginalBody(stringBuilder.toString());
                    httpResponse.getHeaders().put("Content-Length", String.valueOf(file.length()));
                } else if (this.request.getMethod().equals("POST")) {
                    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                        file.createNewFile();

                        bufferedWriter.write(this.request.getBody());

                        bufferedWriter.flush();

                        httpResponse.setResponseCode("201");
                        httpResponse.setReasonPhrase("Created");

                        httpResponse.buildHeaders();
                    } catch (IOException e) {
                        System.out.println("Error creating file: " + e.getMessage());
                    }
                }
            } else if (this.endPointIndex == 3) {
                httpResponse.setResponseCode("200");
                httpResponse.setReasonPhrase("OK");
            }

        }
        httpResponse.buildHeaders();
    }

    public int matchPath() {
        String requestTarget = this.request.getRequestTarget();

        if (requestTarget != null && requestTarget.length() != 0) {
            String[] requestTargetSplit = requestTarget.split("/");

            if (requestTargetSplit.length == 0) {
                return 3;
            }

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
                            this.pathVariables.put(variableName, requestTargetSplit[i]);
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

    public Map<String, String> getpathVariables() {
        return pathVariables;
    }

    public void setpathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }
}
