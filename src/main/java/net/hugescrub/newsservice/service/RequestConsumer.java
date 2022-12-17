package net.hugescrub.newsservice.service;

import lombok.extern.slf4j.Slf4j;
import net.hugescrub.newsservice.model.Article;
import net.hugescrub.newsservice.payload.PatchArticleRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RequestConsumer {

    public static final String AUTH_COOKIE = "CkieUsrSessionID";
    private final String PORTAL_BASE_URL = "http://localhost:8080";
    private final String CLASSIFICATION_BASE_URL = "http://localhost:8082";
    private final String MOCK_CL_URL = "http://localhost:8081";

    public void passData(final Long articleId, final String uriPath) {
        if (articleId > 0 && uriPath != null) {
            WebClient client = WebClient.create(MOCK_CL_URL); // TODO change to actual url
            client.post()
                    .uri(uriPath)
                    .bodyValue(articleId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(System.out::println);
        }
    }

    public Mono<Article> retrieveData(final Long id, final String uriPath, final String authCookie) {
        if (id > 0 && uriPath != null) {
            WebClient client = WebClient.create(PORTAL_BASE_URL);

            return client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(uriPath)
                            .build(id))
                    .cookie(AUTH_COOKIE, authCookie)
                    .retrieve()
                    .bodyToMono(Article.class);
        } else
            throw new IllegalArgumentException("Article id < 1 or wrong uri provided.");
    }

    public void changeFake(final Long articleId, final Boolean isFake, final String uriPath, final String authCookie) {
        if (articleId > 0 && uriPath != null) {
            // declare empty request body
            PatchArticleRequest request = new PatchArticleRequest();
            // assign needed field to patch request body field
            request.setIsFake(isFake);

            WebClient client = WebClient.create(PORTAL_BASE_URL);
            client.patch()
                    .uri(uriBuilder -> uriBuilder
                            .path("/portal/news/{articleId}")
                            .build(articleId))
                    .body(BodyInserters.fromValue(request))
                    .cookie(AUTH_COOKIE, authCookie)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(System.out::println);
        } else
            throw new IllegalArgumentException("Article id < 1 or wrong uri provided.");
    }
}
