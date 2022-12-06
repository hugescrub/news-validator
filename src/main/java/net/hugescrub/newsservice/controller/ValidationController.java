package net.hugescrub.newsservice.controller;

import lombok.extern.slf4j.Slf4j;
import net.hugescrub.newsservice.dto.ArticleDto;
import net.hugescrub.newsservice.model.Article;
import net.hugescrub.newsservice.payload.MessageResponse;
import net.hugescrub.newsservice.repository.ArticleRepository;
import net.hugescrub.newsservice.service.ArticleService;
import net.hugescrub.newsservice.service.RequestConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @GetMapping("/get/all")
    public List<Article> getAllArticles(){
        return articleRepository.findAll();
    }

    @GetMapping(value = "/get", params = "fake")
    public List<Article> getAllByFakeFlag(@RequestParam Boolean fake) {
        return articleRepository.findAllByIsFake(fake);
    }

    @GetMapping(value = "/get", params = "date")
    public List<Article> getAllByDate(@RequestParam String date) {
        return articleRepository.findAllByCreatedBefore(LocalDateTime.parse(date));
    }

    @GetMapping(value = "/get", params = "bodyPart")
    public List<Article> getAllByBodyPart(@RequestParam String bodyPart) {
        return articleRepository.findAllByBodyContains(bodyPart);
    }


    @PostMapping("/validate")
    public ResponseEntity<? extends MessageResponse> validateArticle(@RequestBody ArticleDto articleDto) {
        if(articleService.validateArticle(articleDto)) {
            return ResponseEntity.ok()
                    .body(new MessageResponse("The article is fake."));
        } else {
            Long articleId = articleRepository.findByTitle(articleDto.getTitle()).getId();
            //consumer.passData(articleId, "/api/classification/classify");
            consumer.passData(articleId, "/api/validator/classificationMock");
            return ResponseEntity.ok()
                    .body(new MessageResponse("The article is not fake."));
        }
    }

    @PostMapping("/classificationMock")
    public ResponseEntity<? extends MessageResponse> respondWithTopic() {
        log.warn("Classification endpoint was called at: " + LocalTime.now());
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
