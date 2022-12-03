package net.hugescrub.newsservice.controller;

import net.hugescrub.newsservice.dto.ArticleDto;
import net.hugescrub.newsservice.model.Article;
import net.hugescrub.newsservice.payload.MessageResponse;
import net.hugescrub.newsservice.repository.ArticleRepository;
import net.hugescrub.newsservice.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/validator")
public class ValidationController {

    private final ArticleRepository articleRepository;
    private final ArticleService articleService;

    @Autowired
    public ValidationController(ArticleRepository articleRepository, ArticleService articleService) {
        this.articleRepository = articleRepository;
        this.articleService = articleService;
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
            return ResponseEntity.ok()
                    .body(new MessageResponse("The article is not fake."));
        }
    }
}
