package com.qihoo.finance.chronus.protocol.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {
    private HttpHeaders header;

    public HeaderRequestInterceptor(HttpHeaders header) {
        this.header = header;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        if (header != null && !header.isEmpty()) {
            httpRequest.getHeaders().addAll(header);
        }
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
