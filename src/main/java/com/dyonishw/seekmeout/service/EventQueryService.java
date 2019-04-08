package com.dyonishw.seekmeout.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.dyonishw.seekmeout.domain.Event;
import com.dyonishw.seekmeout.domain.*; // for static metamodels
import com.dyonishw.seekmeout.repository.EventRepository;
import com.dyonishw.seekmeout.repository.search.EventSearchRepository;
import com.dyonishw.seekmeout.service.dto.EventCriteria;
import com.dyonishw.seekmeout.service.dto.EventDTO;
import com.dyonishw.seekmeout.service.mapper.EventMapper;

/**
 * Service for executing complex queries for Event entities in the database.
 * The main input is a {@link EventCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EventDTO} or a {@link Page} of {@link EventDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventQueryService extends QueryService<Event> {

    private final Logger log = LoggerFactory.getLogger(EventQueryService.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final EventSearchRepository eventSearchRepository;

    public EventQueryService(EventRepository eventRepository, EventMapper eventMapper, EventSearchRepository eventSearchRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventSearchRepository = eventSearchRepository;
    }

    /**
     * Return a {@link List} of {@link EventDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EventDTO> findByCriteria(EventCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Event> specification = createSpecification(criteria);
        return eventMapper.toDto(eventRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EventDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EventDTO> findByCriteria(EventCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Event> specification = createSpecification(criteria);
        return eventRepository.findAll(specification, page)
            .map(eventMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EventCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Event> specification = createSpecification(criteria);
        return eventRepository.count(specification);
    }

    /**
     * Function to convert EventCriteria to a {@link Specification}
     */
    private Specification<Event> createSpecification(EventCriteria criteria) {
        Specification<Event> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Event_.id));
            }
            if (criteria.getActivityType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getActivityType(), Event_.activityType));
            }
            if (criteria.getTakingPlaceAt() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTakingPlaceAt(), Event_.takingPlaceAt));
            }
            if (criteria.getPeopleAttending() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPeopleAttending(), Event_.peopleAttending));
            }
            if (criteria.getCasual() != null) {
                specification = specification.and(buildSpecification(criteria.getCasual(), Event_.casual));
            }
            if (criteria.getHour() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHour(), Event_.hour));
            }
            if (criteria.getActivityEventId() != null) {
                specification = specification.and(buildSpecification(criteria.getActivityEventId(),
                    root -> root.join(Event_.activityEvent, JoinType.LEFT).get(Activity_.id)));
            }
            if (criteria.getPlaceEventId() != null) {
                specification = specification.and(buildSpecification(criteria.getPlaceEventId(),
                    root -> root.join(Event_.placeEvent, JoinType.LEFT).get(Place_.id)));
            }
        }
        return specification;
    }
}
