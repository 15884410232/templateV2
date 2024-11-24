package com.dtsw.collection.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * @author Wangwang Tang
 * @since 2024-11-06
 */
public class HttpUtils {

    private static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(60)).followRedirects(HttpClient.Redirect.NEVER).build();

    public static InputStream get(String uri) throws IOException {
        return get(URI.create(uri), HttpResponse.BodyHandlers.ofInputStream(), 0, Duration.ZERO);
    }

    public static InputStream get(String uri, int retry) throws IOException {
        return get(URI.create(uri), HttpResponse.BodyHandlers.ofInputStream(), retry, Duration.ZERO);
    }

    public static InputStream get(String uri, int retry, Duration retryInterval) throws IOException {
        return get(URI.create(uri), HttpResponse.BodyHandlers.ofInputStream(), retry, retryInterval);
    }

    public static InputStream get(URI uri) throws IOException {
        return get(uri, HttpResponse.BodyHandlers.ofInputStream(), 0, Duration.ZERO);
    }

    public static InputStream get(URI uri, int retry) throws IOException {
        return get(uri, HttpResponse.BodyHandlers.ofInputStream(), retry, Duration.ZERO);
    }

    public static InputStream get(URI uri, int retry, Duration retryInterval) throws IOException {
        return get(uri, HttpResponse.BodyHandlers.ofInputStream(), retry, retryInterval);
    }

    public static <T> T get(String uri, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException {
        return get(URI.create(uri), responseBodyHandler, 0, Duration.ZERO);
    }

    public static <T> T get(String uri, HttpResponse.BodyHandler<T> responseBodyHandler, int retry) throws IOException {
        return get(URI.create(uri), responseBodyHandler, retry, Duration.ZERO);
    }

    public static <T> T get(String uri, HttpResponse.BodyHandler<T> responseBodyHandler, int retry, Duration retryInterval) throws IOException {
        return get(URI.create(uri), responseBodyHandler, retry, retryInterval);
    }


    public static <T> T get(URI uri, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException {
        return get(uri, responseBodyHandler, 0, Duration.ZERO);
    }

    public static <T> T get(URI uri, HttpResponse.BodyHandler<T> responseBodyHandler, int retry) throws IOException {
        return get(uri, responseBodyHandler, retry, Duration.ZERO);
    }

    public static <T> T get(URI uri, HttpResponse.BodyHandler<T> responseBodyHandler, int retry, Duration retryInterval) throws IOException {
        int times = 0;
        do {
            times++;
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
                try {
                    HttpResponse<T> response = client.send(request, responseBodyHandler);
                    if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                        return response.body();
                    } else {
                        throw new IOException("Unexpected response: " + response);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException(e);
                }
            } catch (IOException e) {
                if (times > retry) {
                    throw e;
                }
                try {
                    // noinspection BusyWait
                    Thread.sleep(retryInterval.toMillis());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IOException(ex);
                }
            }
        } while (true);
    }

}
