import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private List<String> lineList;
    private String rawRequestLine;
    private String method;
    private String requestTarget;
    private String httpVersion;
    private static final String[] PATHS = {"/echo/*"};
    public static final String SEPARATOR = "\r\n";
    private Map<String, String> headers = new HashMap<>();


    public HttpRequest(List<String> lineList) {
        this.lineList = lineList;

        this.parseRequest();
    }

    private void parseRequest() {
        if (this.lineList.size() > 0) {
            this.rawRequestLine = this.lineList.get(0);
            String[] requestLineSplit = this.rawRequestLine.split(" ");

            this.method = requestLineSplit[0];
            this.requestTarget = requestLineSplit[1];
            this.httpVersion = requestLineSplit[2];

            this.parseHeaders();
        }
    }

    private void parseHeaders() {
        for (int i = 1; i < this.lineList.size(); i++) {
            if (this.lineList.get(i).isEmpty()) {
                break;
            }

            StringBuilder headerBuilder = new StringBuilder();
            String header;
            String value;
            int limitIndex = -1;

            String line = this.lineList.get(i);
            char[] charArray = line.toCharArray();

            for (int j = 0; j < charArray.length; j++) {
                if (charArray[j] == ':') {
                    limitIndex = j;
                    break;
                }
                headerBuilder.append(charArray[j]);
            }

            header = headerBuilder.toString();
            value = line.substring(limitIndex + 2, line.length());

            this.headers.put(header, value);
        }
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
