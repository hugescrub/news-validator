package net.hugescrub.newsservice.service;

import lombok.extern.slf4j.Slf4j;
import net.hugescrub.newsservice.model.Article;
import net.hugescrub.newsservice.payload.PatchArticleRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class RequestConsumer {

    public static final String AUTH_COOKIE = "CkieUsrSessionID";
    private final String PORTAL_BASE_URL = "http://localhost:8080";
    private final String CLASSIFICATION_BASE_URL = "http://localhost:8081";

    public void passData(final Long uuid, final String uriPath) {
        if (uuid > 0 && uriPath != null) {
            WebClient client = WebClient.create(CLASSIFICATION_BASE_URL); // TODO change to actual url
            client.post()
                    .uri(uriPath + "?uuid=" + uuid)
                    .bodyValue(uuid)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(System.out::println);
        }
    }

    public Article retrieveData(final Long id, final String uriPath, final String authCookie,
                                final String sessionCookie) {
        if (id > 0 && uriPath != null) {
            WebClient client = WebClient.create(PORTAL_BASE_URL);

            return client.post()
                    .uri("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(
                            "username", "serviceValUsr") // TODO set values with env variables
                            .with("password", "serviceValPasswd"))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(voidResponseEntity -> client.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(uriPath)
                                    .build(id))
                            .cookies(cookies ->
                                    cookies.setAll(Map.of(AUTH_COOKIE, authCookie,
                                                         "JSESSIONID", sessionCookie)
                                    ))
                            .retrieve()
                            .bodyToMono(Article.class))
                    .block();
        } else {
            throw new IllegalArgumentException("Article id < 1 or wrong uri provided.");
        }
    }

    public void changeFake(final Long articleId, final Boolean isFake, final String uriPath,
                           final String authCookie, final String sessionCookie) {
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
                    .cookies(cookies ->
                            cookies.setAll(Map.of(AUTH_COOKIE, authCookie,
                                                 "JSESSIONID", sessionCookie)
                            ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(System.out::println);
        } else
            throw new IllegalArgumentException("Article id < 1 or wrong uri provided.");
    }
}
