package net.hugescrub.newsservice.repository;

import net.hugescrub.newsservice.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByCreatedBefore(LocalDateTime date);
    List<Article> findAllByBodyContains(String bodyPart);
    List<Article> findAllByIsFake(Boolean flag);
    Article findByTitle(String title);
    Boolean existsByTitle(String title);
}
