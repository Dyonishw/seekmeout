package com.dyonishw.seekmeout.repository;

import com.dyonishw.seekmeout.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Event entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query(value = "select distinct event from Event event left join fetch event.eventUsers",
        countQuery = "select count(distinct event) from Event event")
    Page<Event> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct event from Event event left join fetch event.eventUsers")
    List<Event> findAllWithEagerRelationships();

    @Query("select event from Event event left join fetch event.eventUsers where event.id =:id")
    Optional<Event> findOneWithEagerRelationships(@Param("id") Long id);

}
