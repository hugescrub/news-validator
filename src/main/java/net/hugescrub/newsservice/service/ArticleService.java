package net.hugescrub.newsservice.service;

import net.hugescrub.newsservice.dto.ArticleDto;
import net.hugescrub.newsservice.model.Article;
import net.hugescrub.newsservice.model.Source;
import net.hugescrub.newsservice.repository.ArticleRepository;
import net.hugescrub.newsservice.repository.SourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final SourceRepository sourceRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, SourceRepository sourceRepository) {
        this.articleRepository = articleRepository;
        this.sourceRepository = sourceRepository;
    }

    // e.g. if article is fake return true, if not return false
    @Transactional
    public boolean validateArticle(ArticleDto articleDto) {
        if (articleRepository.existsByTitle(articleDto.getTitle())) {
            Article article = articleRepository.findByTitle(articleDto.getTitle());
            return article.getIsFake();
        } else if (articleDto.getSource().getSourceName().matches("gazeta\\.ru|kommersant\\.ru|lenta\\.ru")) {
            // get source from database associated with source passed in request body
            Source source = sourceRepository.findBySourceName(articleDto.getSource().getSourceName());
            // write the article to database
            Article article = new Article(
                    articleDto.getTitle(),
                    articleDto.getBody(),
                    LocalDateTime.now(),
                    false
            );
            article.getSources().add(source);
            articleRepository.save(article);
            return false;
        } else {
            Article article = new Article(
                    articleDto.getTitle(),
                    articleDto.getBody(),
                    LocalDateTime.now(),
                    true
            );
            articleRepository.save(article);
            return true;
        }
    }
}
