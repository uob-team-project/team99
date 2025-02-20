package bham.team.repository;

import bham.team.domain.Screenshot;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Screenshot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScreenshotRepository extends JpaRepository<Screenshot, Long> {}
