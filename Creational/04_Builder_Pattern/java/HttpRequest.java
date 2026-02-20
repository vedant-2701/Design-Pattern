/**
 * A microservices API Gateway needs to construct outgoing HTTP 
 * requests to downstream services — with headers, query params, 
 * auth tokens, timeouts, retry configs, and body payloads all configurable. 
 * Each microservice call has different combinations of these options.
 */

// Scenario: HTTP Request Builder — API Gateway Service
// Constructs outgoing HTTP requests step by step.
// Each downstream service call configures only what it needs.

import java.time.Duration;
import java.util.*;

// ── Step 1: Product ───────────────────────────────────────────────────────────

public final class HttpRequest {
    public enum Method { GET, POST, PUT, PATCH, DELETE }

    private final String              url;
    private final Method              method;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String              body;
    private final Duration            timeout;
    private final int                 maxRetries;
    private final boolean             followRedirects;
    private final String              bearerToken;

    // Private — only HttpRequestBuilder can instantiate
    private HttpRequest(HttpRequestBuilder builder) {
        if (builder.url == null || builder.url.isBlank())
            throw new IllegalStateException("URL is required");
        if (builder.method == null)
            throw new IllegalStateException("HTTP method is required");

        this.url             = builder.url;
        this.method          = builder.method;
        this.headers         = Collections.unmodifiableMap(new LinkedHashMap<>(builder.headers));
        this.queryParams     = Collections.unmodifiableMap(new LinkedHashMap<>(builder.queryParams));
        this.body            = builder.body;
        this.timeout         = builder.timeout;
        this.maxRetries      = builder.maxRetries;
        this.followRedirects = builder.followRedirects;
        this.bearerToken     = builder.bearerToken;
    }

    // Getters
    public String              getUrl()             { return url; }
    public Method              getMethod()          { return method; }
    public Map<String, String> getHeaders()         { return headers; }
    public Map<String, String> getQueryParams()     { return queryParams; }
    public Optional<String>    getBody()            { return Optional.ofNullable(body); }
    public Duration            getTimeout()         { return timeout; }
    public int                 getMaxRetries()      { return maxRetries; }
    public boolean             isFollowRedirects()  { return followRedirects; }
    public Optional<String>    getBearerToken()     { return Optional.ofNullable(bearerToken); }

    public String buildFullUrl() {
        if (queryParams.isEmpty()) return url;
        StringBuilder sb = new StringBuilder(url).append("?");
        queryParams.forEach((k, v) -> sb.append(k).append("=").append(v).append("&"));
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    public void summarize() {
        System.out.println("── HTTP Request ──────────────────────────────────────");
        System.out.println("Method:     " + method);
        System.out.println("URL:        " + buildFullUrl());
        System.out.println("Headers:    " + headers);
        System.out.println("Auth:       " + (bearerToken != null ? "Bearer ***" : "None"));
        System.out.println("Body:       " + (body != null ? body.substring(0, Math.min(50, body.length())) + "..." : "None"));
        System.out.println("Timeout:    " + timeout.toSeconds() + "s");
        System.out.println("Retries:    " + maxRetries);
        System.out.println("Redirects:  " + followRedirects);
        System.out.println("─────────────────────────────────────────────────────");
    }

    // Entry point to the builder
    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    // ── Step 2: Builder (static inner class — idiomatic Java) ─────────────────

    public static final class HttpRequestBuilder {

        private String                    url;
        private Method                    method;
        private final Map<String, String> headers         = new LinkedHashMap<>();
        private final Map<String, String> queryParams     = new LinkedHashMap<>();
        private String                    body;
        private Duration                  timeout         = Duration.ofSeconds(30);
        private int                       maxRetries      = 3;
        private boolean                   followRedirects = true;
        private String                    bearerToken;

        private HttpRequestBuilder() {}

        public HttpRequestBuilder url(String url) {
            this.url = url;
            return this;
        }

        public HttpRequestBuilder method(Method method) {
            this.method = method;
            return this;
        }

        public HttpRequestBuilder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public HttpRequestBuilder contentType(String contentType) {
            return header("Content-Type", contentType);
        }

        public HttpRequestBuilder accept(String mediaType) {
            return header("Accept", mediaType);
        }

        public HttpRequestBuilder queryParam(String key, String value) {
            this.queryParams.put(key, value);
            return this;
        }

        public HttpRequestBuilder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequestBuilder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public HttpRequestBuilder maxRetries(int maxRetries) {
            if (maxRetries < 0) throw new IllegalArgumentException("maxRetries must be >= 0");
            this.maxRetries = maxRetries;
            return this;
        }

        public HttpRequestBuilder followRedirects(boolean follow) {
            this.followRedirects = follow;
            return this;
        }

        public HttpRequestBuilder bearerToken(String token) {
            this.bearerToken = token;
            return this;
        }

        // Terminal method
        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}

// ── Step 3: Director ──────────────────────────────────────────────────────────

class GatewayRequestDirector {

    // Recipe: standard authenticated JSON POST
    public static HttpRequest buildAuthenticatedPost(
            String url, String jsonBody, String token) {
        return HttpRequest.builder()
            .url(url)
            .method(HttpRequest.Method.POST)
            .contentType("application/json")
            .accept("application/json")
            .body(jsonBody)
            .bearerToken(token)
            .timeout(Duration.ofSeconds(10))
            .maxRetries(3)
            .build();
    }

    // Recipe: internal health-check GET (no auth, no retry, fast timeout)
    public static HttpRequest buildHealthCheck(String serviceUrl) {
        return HttpRequest.builder()
            .url(serviceUrl + "/health")
            .method(HttpRequest.Method.GET)
            .timeout(Duration.ofSeconds(3))
            .maxRetries(0)
            .followRedirects(false)
            .build();
    }
}
