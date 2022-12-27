package net.hugescrub.newsservice.service;

import lombok.extern.slf4j.Slf4j;
import net.hugescrub.newsservice.model.Article;
import net.hugescrub.newsservice.model.ModuleError;
import net.hugescrub.newsservice.payload.PatchArticleRequest;
import net.hugescrub.newsservice.repository.ErrorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class RequestConsumer {

    public static final String AUTH_COOKIE = "CkieUsrSessionID";
    private final String PORTAL_BASE_URL = "http://localhost:8080";
    private final String CLASSIFICATION_BASE_URL = "http://localhost:8081";

    @Autowired
    private ErrorRepository errorRepository;

    public void passData(final Long uuid, final String uriPath) throws WebClientRequestException {
        if (uuid > 0 && uriPath != null) {
            WebClient client = WebClient.create(CLASSIFICATION_BASE_URL); // TODO change to actual url
            client.post()
                    .uri(uriPath + "?uuid=" + uuid)
                    .bodyValue(uuid)
                    .retrieve()
                    .bodyToMono(ClientResponse.class)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnError(errorSpec -> {
                        final String message = errorSpec.getMessage();
                        log.warn("An error occurred: " + message.substring(message.lastIndexOf("Connection")));
                        errorRepository.save(new ModuleError("Tried to fetch remote but it was down", 503, LocalDateTime.now()));
                    })
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
                    .publishOn(Schedulers.boundedElastic())
                    .doOnError(errorSpec -> {
                        final String message = errorSpec.getMessage();
                        log.warn("An error occurred: " + message);
                        errorRepository.save(new ModuleError("Remote server error", 500, LocalDateTime.now()));
                    })
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
