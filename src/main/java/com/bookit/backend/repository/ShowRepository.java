package com.bookit.backend.repository;

import com.bookit.backend.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShowRepository extends JpaRepository<Show, UUID> {
    @Query("""
    SELECT COUNT(s) > 0
    FROM Show s
    WHERE s.screen.screenId = :screenId
      AND s.startTime < :endTime
      AND s.endTime > :startTime
""")
    boolean existsOverlappingShow(
            @Param("screenId") UUID screenId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    @Query("SELECT s FROM Show s WHERE s.movie.movieId = ?1")
    List<Show> findShowsByMovieId(UUID movieId);

    @Query("SELECT s FROM Show s JOIN s.screen sc WHERE sc.theatre.theatreId = ?1")
    List<Show> findShowsByTheatreId(UUID theatreId);
}
