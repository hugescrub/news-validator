package net.hugescrub.newsservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RequestConsumer {
    //private final String BASE_URL = "http://111.1.1.1:port";
    private final String BASE_URL = "http://localhost:8080";

    public void passData(final Long articleId, final String uriPath) {
        if (articleId > 0 && uriPath != null) {
            WebClient client = WebClient.create(BASE_URL);
            client.post()
                    .uri(uriPath)
                    .bodyValue(articleId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(System.out::println);
        }
    }
}
