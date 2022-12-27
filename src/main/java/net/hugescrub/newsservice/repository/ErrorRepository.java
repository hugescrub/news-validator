package net.hugescrub.newsservice.repository;

import net.hugescrub.newsservice.model.ModuleError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorRepository extends JpaRepository<ModuleError, Long> {
}
