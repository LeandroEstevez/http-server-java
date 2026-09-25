import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class HttpResponse {
    private Map<String, String> headers = new HashMap<>();
    private final String HttpVersion = "HTTP/1.1";
    private String responseCode;
    private String reasonPhrase;
    private String originalBody;
    private byte[] compressedBody;
    private Set<String> supportedEncoding = Set.of("gzip");
    private String responseHeaders;

    public void buildHeaders() {
        String responseLine = String.join(" ", this.HttpVersion, this.responseCode, this.reasonPhrase) + HttpRequest.SEPARATOR;

        if (this.originalBody != null) {
            if (this.headers.get("Content-Encoding") != null && this.headers.get("Content-Encoding").equals("gzip")) {
                try {
                    this.compressedBody = this.gzip(this.originalBody);

                    this.headers.put("Content-Length", String.valueOf(this.compressedBody.length));
                } catch (IOException e) {
                    System.out.println("Error when compressing: " + e.getMessage());
                    this.headers.put("Content-Length", String.valueOf(this.originalBody.length()));
                }
            }
        }

        List<String> headersList = new ArrayList<>();
        String bodyDelimiter = HttpRequest.SEPARATOR + HttpRequest.SEPARATOR;

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String header = key + ": " + value;
            headersList.add(header);
        }

        if (!headersList.isEmpty()) {
            String headerLines = String.join(HttpRequest.SEPARATOR, headersList);
            this.responseHeaders = responseLine + headerLines + bodyDelimiter;
        } else {
            this.responseHeaders = responseLine + bodyDelimiter;
        }
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

    public byte[] gzip(String text) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

        try (GZIPOutputStream gzipOutput = new GZIPOutputStream(byteOutput)) {
            gzipOutput.write(text.getBytes(StandardCharsets.UTF_8));
        }

        return byteOutput.toByteArray();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getOriginalBody() {
        return originalBody;
    }

    public void setOriginalBody(String body) {
        this.originalBody = body;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String getResponseHeaders() {
        return responseHeaders;
    }

    public boolean isBodyCompressed() {
        return this.compressedBody != null;
    }

    public byte[] getCompressedBody() {
        return compressedBody;
    }
}
