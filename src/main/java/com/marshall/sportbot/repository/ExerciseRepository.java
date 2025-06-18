package com.marshall.sportbot.repository;

import com.marshall.sportbot.entity.ExerciseEntity;
import com.marshall.sportbot.enums.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseEntity, Long> {
    @Query("SELECT e FROM ExerciseEntity e WHERE e.userId = :userId AND e.exerciseType = :exerciseType AND e.dateTime > :from")
    List<ExerciseEntity> findAllFromTime(@Param("userId") Long userId,
                                         @Param("exerciseType") ExerciseType exerciseType,
                                         @Param("from") ZonedDateTime from);

    @Query("""
                SELECT e.userId, SUM(e.count)
                FROM ExerciseEntity e
                WHERE e.exerciseType = :type
                  AND e.dateTime >= :from
                GROUP BY e.userId
            """)
    List<Object[]> getUserPushUpSumsFromTime(
            @Param("type") ExerciseType type,
            @Param("from") ZonedDateTime from
    );

}
