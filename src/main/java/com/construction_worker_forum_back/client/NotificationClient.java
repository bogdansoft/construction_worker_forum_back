package com.construction_worker_forum_back.client;

import com.construction_worker_forum_back.model.Notification;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Service
public class NotificationClient {

    private final WebClient webClient;

    SslContext context = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();

    HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(context));

    public NotificationClient(@Value("${notification.service.url}") String url) throws SSLException {
        webClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public Mono<Notification> sendNotification(Notification notification) {
        return webClient.post()
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(Notification.class);
    }

}
