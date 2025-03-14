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

//    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(60)).followRedirects(HttpClient.Redirect.NEVER).build();

    private static final HttpClient clientProxy1 = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10)).followRedirects(HttpClient.Redirect.ALWAYS).build();
    private static final HttpClient clientProxy2 = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10)).followRedirects(HttpClient.Redirect.ALWAYS).build();
    private static final HttpClient clientProxy3 = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10)).followRedirects(HttpClient.Redirect.ALWAYS).build();
    private static final HttpClient clientProxy4 = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10)).followRedirects(HttpClient.Redirect.ALWAYS).build();
    private static final HttpClient clientProxy5 = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10)).followRedirects(HttpClient.Redirect.ALWAYS).build();
    private static final HttpClient clientProxy6 = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10)).followRedirects(HttpClient.Redirect.ALWAYS).build();


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
                    int random = (int) (Math.random() * 6);
                    HttpClient clientProxy = switch (random) {
                        case 0 -> clientProxy1;
                        case 1 -> clientProxy2;
                        case 2 -> clientProxy3;
                        case 3 -> clientProxy4;
                        case 4 -> clientProxy5;
                        case 5 -> clientProxy6;
                        default -> throw new IllegalStateException("Unexpected value: " + random);
                    };
                    HttpResponse<T> response = clientProxy.send(request, responseBodyHandler);
                    if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                        return response.body();
                    } else {
                        if (response.statusCode() == 429) {
                            //触发限流，让线程睡眠再重试
                            //获取[5-10)之间的随机数  秒
                            int second = (int) (Math.random() * (6));
                            Thread.sleep(1000 * second);
                            throw new IOException();
                        }
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
            } catch (Exception exception) {
                exception.printStackTrace();
                throw exception;
            }
        } while (true);
    }

}
