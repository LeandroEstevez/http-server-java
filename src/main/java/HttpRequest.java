import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public static final String SEPARATOR = "\r\n";

    private final String[] lineArr;
    private String method;
    private String requestTarget;
    private String httpVersion;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private String[] acceptedCompression = {"gzip"};

    public HttpRequest(String[] lineArr) {
        this.lineArr = lineArr;

        this.parseRequest();
    }

    private void parseRequest() {
        this.parseRequestLine();
        this.parseHeaders();
    }

    private void parseRequestLine() {
        String[] requestLineSplit = this.lineArr[0].split(" ");

        this.method = requestLineSplit[0];
        this.requestTarget = requestLineSplit[1];
        this.httpVersion = requestLineSplit[2];
    }

    private void parseHeaders() {
        for (int i = 1; i < this.lineArr.length; i++) {
            if (this.lineArr[i].isEmpty()) {
                break;
            }

            StringBuilder headerBuilder = new StringBuilder();
            String header;
            String value;
            int limitIndex = -1;

            String line = this.lineArr[i];
            System.out.println("Line from line array: " + line);
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

        System.out.println("Request headers: " + this.headers);
    }

    public String getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
