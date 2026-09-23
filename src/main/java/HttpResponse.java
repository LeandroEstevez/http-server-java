import java.util.*;

public class HttpResponse {
    private Map<String, String> headers = new HashMap<>();
    private final String HttpVersion = "HTTP/1.1";
    private String responseCode;
    private String reasonPhrase;
    private String body;
    private Set<String> supportedEncoding = Set.of("gzip");

    public String buildResponse() {
        String responseLine = String.join(" ", this.HttpVersion, this.responseCode, this.reasonPhrase) + HttpRequest.SEPARATOR;

        List<String> headersList = new ArrayList<>();
        String bodyDelimiter = HttpRequest.SEPARATOR + HttpRequest.SEPARATOR;

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String header = key + ": " + value;
            headersList.add(header);
        }

        String response;
        if (!headersList.isEmpty()) {
            String headerLines = String.join(HttpRequest.SEPARATOR, headersList);
            response = responseLine + headerLines + bodyDelimiter;
        } else {
            response = responseLine + bodyDelimiter;
        }

        if (this.body != null) {
            response += this.body;
        }

        return response;
    }

    public void setEncodingHeader(String acceptEncodingHeader) {
        if (acceptEncodingHeader == null) {
            return;
        }
        String[] acceptEncodingArr = acceptEncodingHeader.split(", ");

        for (String encoding: acceptEncodingArr) {
            if (supportedEncoding.contains(encoding)) {
                this.headers.put("Content-Encoding", encoding);
                break;
            }
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }
}
