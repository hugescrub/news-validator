package net.hugescrub.newsservice.service;

import lombok.extern.slf4j.Slf4j;
import net.hugescrub.newsservice.model.Article;
import net.hugescrub.newsservice.repository.ArticleRepository;
import net.hugescrub.newsservice.repository.SourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final SourceRepository sourceRepository;
    private final RequestConsumer requestConsumer;

    @Autowired
    public ArticleService(ArticleRepository articleRepository,
                          SourceRepository sourceRepository,
                          RequestConsumer requestConsumer) {
        this.articleRepository = articleRepository;
        this.sourceRepository = sourceRepository;
        this.requestConsumer = requestConsumer;
    }

    // e.g. if article is fake return true, if not return false
    @Transactional
    public boolean validateArticle(Long articleId, HttpServletRequest request) {
        Map<String, String> requestCookies = new HashMap<>();
        Arrays.stream(request.getCookies())
                .forEach(cookie -> requestCookies.put(cookie.getName(), cookie.getValue()));

        String authCookie = requestCookies.get(RequestConsumer.AUTH_COOKIE);
        String sessionCookie = requestCookies.get("JSESSIONID");

        Article article = Objects.requireNonNull(
                requestConsumer.retrieveData(articleId, "/portal/news/{id}", authCookie, sessionCookie)
        );

        boolean isFake = article.getBody() != null
                && Pattern.compile(".*gazeta\\.ru.*kommersant\\.ru.*lenta\\.ru.*interfax\\.ru")
                .matcher(article.getBody()).find();

        requestConsumer.changeFake(articleId, isFake, "/portal/news/{articleId}", authCookie, sessionCookie);
        if (!articleRepository.existsByTitle(article.getTitle())) {
            article.setFake(isFake);
            articleRepository.save(article);
        }
        return isFake;
    }
}
