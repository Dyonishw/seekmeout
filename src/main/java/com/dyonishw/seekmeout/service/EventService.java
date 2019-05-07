package com.dyonishw.seekmeout.service;

import com.dyonishw.seekmeout.service.dto.EventDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Event.
 */
public interface EventService {

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save
     * @return the persisted entity
     */
    EventDTO save(EventDTO eventDTO);

    /**
     * Get all the events.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<EventDTO> findAll(Pageable pageable);

    /**
     * Get all the Event with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    Page<EventDTO> findAllWithEagerRelationships(Pageable pageable);
    
    /**
     * Get the "id" event.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<EventDTO> findOne(Long id);

    /**
     * Delete the "id" event.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the event corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<EventDTO> search(String query, Pageable pageable);
}
