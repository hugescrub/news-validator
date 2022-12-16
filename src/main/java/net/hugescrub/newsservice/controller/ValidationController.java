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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<? extends MessageResponse> validateArticle(@RequestBody Long articleId) {
        if(articleService.validateArticle(articleId)) {
            return ResponseEntity.ok()
                    .body(new MessageResponse("The article is fake."));
        } else {
            consumer.passData(articleId, "/api/validator/classificationMock");
            return ResponseEntity.ok()
                    .body(new MessageResponse("The article is not fake."));
        }
    }

    @PostMapping("/classificationMock")
    public ResponseEntity<? extends MessageResponse> respondWithTopic(@RequestBody Long articleId) {
        Map<Integer, String> topics = Map.of(
                1, "Medicine",
                2, "Politics",
                3, "Sports",
                4, "Science"
        );
        return ResponseEntity.ok()
                .body(new MessageResponse(topics.get((int) (Math.random() * 4))));
    }
}
