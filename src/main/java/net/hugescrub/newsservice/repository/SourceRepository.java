package net.hugescrub.newsservice.repository;

import net.hugescrub.newsservice.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {
    Source findBySourceName(String sourceName);
}
