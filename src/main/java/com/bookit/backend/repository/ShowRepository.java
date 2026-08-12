package com.bookit.backend.repository;

import com.bookit.backend.model.Show;
import com.bookit.backend.model.ShowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ShowRepository extends JpaRepository<Show, UUID> {

    @Query("""
        SELECT COUNT(s) > 0
        FROM Show s
        WHERE s.screen.screenId = :screenId
          AND s.status <> com.bookit.backend.model.ShowStatus.CANCELLED
          AND s.startTime < :endTime
          AND s.endTime > :startTime
    """)
    boolean existsOverlappingShow(
            @Param("screenId") UUID screenId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
        SELECT COUNT(s) > 0
        FROM Show s
        WHERE s.screen.screenId = :screenId
          AND s.showId <> :showId
          AND s.status <> com.bookit.backend.model.ShowStatus.CANCELLED
          AND s.startTime < :endTime
          AND s.endTime > :startTime
    """)
    boolean existsOverlappingShowForUpdate(
            @Param("screenId") UUID screenId,
            @Param("showId") UUID showId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
        SELECT s
        FROM Show s
        WHERE s.movie.movieId = :movieId
          AND s.movie.active = true
          AND s.status <> :status
    """)
    List<Show> findShowsByMovieIdActiveTrueAndShowStatusNot(
            @Param("movieId") UUID movieId,
            @Param("status") ShowStatus status
    );

    @Query("""
        SELECT s
        FROM Show s
        JOIN s.screen sc
        WHERE sc.theatre.theatreId = :theatreId
          AND sc.active = true
          AND sc.theatre.active = true
          AND s.status <> :status
    """)
    List<Show> findShowsByTheatreIdActiveTrueAndShowStatusNot(
            @Param("theatreId") UUID theatreId,
            @Param("status") ShowStatus status
    );

    Optional<Show> findByShowIdAndStatusNot(
            UUID showId,
            ShowStatus status
    );

    Page<Show> findAllByStatusNot(
            Pageable pageable,
            ShowStatus status
    );
}