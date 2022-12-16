package net.hugescrub.newsservice.service;

import net.hugescrub.newsservice.model.Article;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RequestConsumer {

    private final String PORTAL_BASE_URL = "http://localhost:8080";
    private final String CLASSIFICATION_BASE_URL = "http://localhost:8082";

    public void passData(final Long articleId, final String uriPath) {
        if (articleId > 0 && uriPath != null) {
            WebClient client = WebClient.create(CLASSIFICATION_BASE_URL);
            client.post()
                    .uri(uriPath)
                    .bodyValue(articleId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(System.out::println);
        }
    }

    public Mono<Article> retrieveData(final Long id, final String uriPath) {
        if (id > 0 && uriPath != null) {
            WebClient client = WebClient.create(PORTAL_BASE_URL);
            return client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(uriPath)
                            .build(id))
                    .retrieve()
                    .bodyToMono(Article.class);
        } else
            throw new IllegalArgumentException("Article id < 1 or wrong uri provided.");
    }

    public void changeFake(final Long articleId, final String uriPath, final boolean isFake) {
        if (articleId > 0 && uriPath != null) {
            WebClient client = WebClient.create(PORTAL_BASE_URL);
            client.patch()
                    .uri(uriBuilder -> uriBuilder
                            .path(uriPath)
                            .queryParam("isFake", isFake)
                            .build(articleId))
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(System.out::println);
        } else
            throw new IllegalArgumentException("Article id < 1 or wrong uri provided.");
    }
}
