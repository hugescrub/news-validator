package net.hugescrub.newsservice.controller;

import lombok.extern.slf4j.Slf4j;
import net.hugescrub.newsservice.model.Article;
import net.hugescrub.newsservice.payload.MessageResponse;
import net.hugescrub.newsservice.repository.ArticleRepository;
import net.hugescrub.newsservice.service.ArticleService;
import net.hugescrub.newsservice.service.RequestConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/validator")
public class ValidationController {

    private final ArticleRepository articleRepository;
    private final ArticleService articleService;
    private final RequestConsumer consumer;

    @Autowired
    public ValidationController(ArticleRepository articleRepository,
                                ArticleService articleService,
                                RequestConsumer consumer) {
        this.articleRepository = articleRepository;
        this.articleService = articleService;
        this.consumer = consumer;
    }

    @GetMapping("/articles")
    public List<Article> getAllArticles(){
        return articleRepository.findAll();
    }

    @GetMapping(value = "/articles", params = "fake")
    public List<Article> getAllByFakeFlag(@RequestParam Boolean fake) {
        return articleRepository.findAllByIsFake(fake);
    }

    @GetMapping(value = "/articles", params = "date")
    public List<Article> getAllByDate(@RequestParam String date) {
        return articleRepository.findAllByCreatedBefore(LocalDateTime.parse(date));
    }

    @GetMapping(value = "/articles", params = "bodyPart")
    public List<Article> getAllByBodyPart(@RequestParam String bodyPart) {
        return articleRepository.findAllByBodyContains(bodyPart);
    }


    @PostMapping("/validate")
    public ResponseEntity<? extends MessageResponse> validateArticle(@RequestBody Long articleId, HttpServletRequest request) {
        if(articleService.validateArticle(articleId, request)) {
            return ResponseEntity.ok()
                    .body(new MessageResponse("The article is fake."));
        } else {
            consumer.passData(articleId, "/classify");
            return ResponseEntity.ok()
                    .body(new MessageResponse("The article is not fake."));
        }
    }
}
