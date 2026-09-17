import java.util.List;

public class HttpRequest {
    private List<String> lineList;
    private String rawRequestLine;
    private String method;
    private String requestTarget;
    private String httpVersion;
    private static final String[] PATHS = {"/echo/*"};


    public HttpRequest(List<String> lineList) {
        this.lineList = lineList;

        this.parseRequest();
    }

    private void parseRequest() {
        this.rawRequestLine = this.lineList.get(0);
        String[] requestLineSplit = this.rawRequestLine.split(" ");

        this.method = requestLineSplit[0];
        this.requestTarget = requestLineSplit[1];
        this.httpVersion = requestLineSplit[2];
    }

    public String buildResponse(String body) {
        String contentType = "Content-Type: text/plain";
        String contentLength = "Content-Length: " + body.length();

        String responseLine = "HTTP/1.1 200 OK";

        String separator = "\r\n";

        return responseLine + separator + contentType + separator + contentLength + separator + separator + body;
    }

    public List<String> getLineList() {
        return lineList;
    }

    public void setLineList(List<String> lineList) {
        this.lineList = lineList;
    }

    public String getRawRequestLine() {
        return rawRequestLine;
    }

    public void setRawRequestLine(String rawRequestLine) {
        this.rawRequestLine = rawRequestLine;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public void setRequestTarget(String requestTarget) {
        this.requestTarget = requestTarget;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }
}
