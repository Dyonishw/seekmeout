package com.dyonishw.seekmeout.web.rest;

import com.dyonishw.seekmeout.SeekMeOutApp;

import com.dyonishw.seekmeout.domain.Event;
import com.dyonishw.seekmeout.domain.Activity;
import com.dyonishw.seekmeout.domain.Place;
import com.dyonishw.seekmeout.domain.User;
import com.dyonishw.seekmeout.repository.EventRepository;
import com.dyonishw.seekmeout.repository.search.EventSearchRepository;
import com.dyonishw.seekmeout.service.EventService;
import com.dyonishw.seekmeout.service.dto.EventDTO;
import com.dyonishw.seekmeout.service.mapper.EventMapper;
import com.dyonishw.seekmeout.web.rest.errors.ExceptionTranslator;
import com.dyonishw.seekmeout.service.dto.EventCriteria;
import com.dyonishw.seekmeout.service.EventQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static com.dyonishw.seekmeout.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EventResource REST controller.
 *
 * @see EventResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeekMeOutApp.class)
public class EventResourceIntTest {

    private static final Boolean DEFAULT_CASUAL = false;
    private static final Boolean UPDATED_CASUAL = true;

    private static final LocalDate DEFAULT_HOUR = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_HOUR = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_CASUAL_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_CASUAL_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private EventRepository eventRepository;

    @Mock
    private EventRepository eventRepositoryMock;

    @Autowired
    private EventMapper eventMapper;

    @Mock
    private EventService eventServiceMock;

    @Autowired
    private EventService eventService;

    /**
     * This repository is mocked in the com.dyonishw.seekmeout.repository.search test package.
     *
     * @see com.dyonishw.seekmeout.repository.search.EventSearchRepositoryMockConfiguration
     */
    @Autowired
    private EventSearchRepository mockEventSearchRepository;

    @Autowired
    private EventQueryService eventQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restEventMockMvc;

    private Event event;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EventResource eventResource = new EventResource(eventService, eventQueryService);
        this.restEventMockMvc = MockMvcBuilders.standaloneSetup(eventResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createEntity(EntityManager em) {
        Event event = new Event()
            .casual(DEFAULT_CASUAL)
            .hour(DEFAULT_HOUR)
            .casualDescription(DEFAULT_CASUAL_DESCRIPTION);
        return event;
    }

    @Before
    public void initTest() {
        event = createEntity(em);
    }

    @Test
    @Transactional
    public void createEvent() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isCreated());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.isCasual()).isEqualTo(DEFAULT_CASUAL);
        assertThat(testEvent.getHour()).isEqualTo(DEFAULT_HOUR);
        assertThat(testEvent.getCasualDescription()).isEqualTo(DEFAULT_CASUAL_DESCRIPTION);

        // Validate the Event in Elasticsearch
        verify(mockEventSearchRepository, times(1)).save(testEvent);
    }

    @Test
    @Transactional
    public void createEventWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        // Create the Event with an existing ID
        event.setId(1L);
        EventDTO eventDTO = eventMapper.toDto(event);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate);

        // Validate the Event in Elasticsearch
        verify(mockEventSearchRepository, times(0)).save(event);
    }

    @Test
    @Transactional
    public void checkCasualIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        // set the field null
        event.setCasual(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHourIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        // set the field null
        event.setHour(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEvents() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList
        restEventMockMvc.perform(get("/api/events?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].casual").value(hasItem(DEFAULT_CASUAL.booleanValue())))
            .andExpect(jsonPath("$.[*].hour").value(hasItem(DEFAULT_HOUR.toString())))
            .andExpect(jsonPath("$.[*].casualDescription").value(hasItem(DEFAULT_CASUAL_DESCRIPTION.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllEventsWithEagerRelationshipsIsEnabled() throws Exception {
        EventResource eventResource = new EventResource(eventServiceMock, eventQueryService);
        when(eventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restEventMockMvc = MockMvcBuilders.standaloneSetup(eventResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restEventMockMvc.perform(get("/api/events?eagerload=true"))
        .andExpect(status().isOk());

        verify(eventServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        EventResource eventResource = new EventResource(eventServiceMock, eventQueryService);
            when(eventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restEventMockMvc = MockMvcBuilders.standaloneSetup(eventResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restEventMockMvc.perform(get("/api/events?eagerload=true"))
        .andExpect(status().isOk());

            verify(eventServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.casual").value(DEFAULT_CASUAL.booleanValue()))
            .andExpect(jsonPath("$.hour").value(DEFAULT_HOUR.toString()))
            .andExpect(jsonPath("$.casualDescription").value(DEFAULT_CASUAL_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getAllEventsByCasualIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where casual equals to DEFAULT_CASUAL
        defaultEventShouldBeFound("casual.equals=" + DEFAULT_CASUAL);

        // Get all the eventList where casual equals to UPDATED_CASUAL
        defaultEventShouldNotBeFound("casual.equals=" + UPDATED_CASUAL);
    }

    @Test
    @Transactional
    public void getAllEventsByCasualIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where casual in DEFAULT_CASUAL or UPDATED_CASUAL
        defaultEventShouldBeFound("casual.in=" + DEFAULT_CASUAL + "," + UPDATED_CASUAL);

        // Get all the eventList where casual equals to UPDATED_CASUAL
        defaultEventShouldNotBeFound("casual.in=" + UPDATED_CASUAL);
    }

    @Test
    @Transactional
    public void getAllEventsByCasualIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where casual is not null
        defaultEventShouldBeFound("casual.specified=true");

        // Get all the eventList where casual is null
        defaultEventShouldNotBeFound("casual.specified=false");
    }

    @Test
    @Transactional
    public void getAllEventsByHourIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where hour equals to DEFAULT_HOUR
        defaultEventShouldBeFound("hour.equals=" + DEFAULT_HOUR);

        // Get all the eventList where hour equals to UPDATED_HOUR
        defaultEventShouldNotBeFound("hour.equals=" + UPDATED_HOUR);
    }

    @Test
    @Transactional
    public void getAllEventsByHourIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where hour in DEFAULT_HOUR or UPDATED_HOUR
        defaultEventShouldBeFound("hour.in=" + DEFAULT_HOUR + "," + UPDATED_HOUR);

        // Get all the eventList where hour equals to UPDATED_HOUR
        defaultEventShouldNotBeFound("hour.in=" + UPDATED_HOUR);
    }

    @Test
    @Transactional
    public void getAllEventsByHourIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where hour is not null
        defaultEventShouldBeFound("hour.specified=true");

        // Get all the eventList where hour is null
        defaultEventShouldNotBeFound("hour.specified=false");
    }

    @Test
    @Transactional
    public void getAllEventsByHourIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where hour greater than or equals to DEFAULT_HOUR
        defaultEventShouldBeFound("hour.greaterOrEqualThan=" + DEFAULT_HOUR);

        // Get all the eventList where hour greater than or equals to UPDATED_HOUR
        defaultEventShouldNotBeFound("hour.greaterOrEqualThan=" + UPDATED_HOUR);
    }

    @Test
    @Transactional
    public void getAllEventsByHourIsLessThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where hour less than or equals to DEFAULT_HOUR
        defaultEventShouldNotBeFound("hour.lessThan=" + DEFAULT_HOUR);

        // Get all the eventList where hour less than or equals to UPDATED_HOUR
        defaultEventShouldBeFound("hour.lessThan=" + UPDATED_HOUR);
    }


    @Test
    @Transactional
    public void getAllEventsByCasualDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where casualDescription equals to DEFAULT_CASUAL_DESCRIPTION
        defaultEventShouldBeFound("casualDescription.equals=" + DEFAULT_CASUAL_DESCRIPTION);

        // Get all the eventList where casualDescription equals to UPDATED_CASUAL_DESCRIPTION
        defaultEventShouldNotBeFound("casualDescription.equals=" + UPDATED_CASUAL_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEventsByCasualDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where casualDescription in DEFAULT_CASUAL_DESCRIPTION or UPDATED_CASUAL_DESCRIPTION
        defaultEventShouldBeFound("casualDescription.in=" + DEFAULT_CASUAL_DESCRIPTION + "," + UPDATED_CASUAL_DESCRIPTION);

        // Get all the eventList where casualDescription equals to UPDATED_CASUAL_DESCRIPTION
        defaultEventShouldNotBeFound("casualDescription.in=" + UPDATED_CASUAL_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEventsByCasualDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where casualDescription is not null
        defaultEventShouldBeFound("casualDescription.specified=true");

        // Get all the eventList where casualDescription is null
        defaultEventShouldNotBeFound("casualDescription.specified=false");
    }

    @Test
    @Transactional
    public void getAllEventsByActivityEventIsEqualToSomething() throws Exception {
        // Initialize the database
        Activity activityEvent = ActivityResourceIntTest.createEntity(em);
        em.persist(activityEvent);
        em.flush();
        event.setActivityEvent(activityEvent);
        eventRepository.saveAndFlush(event);
        Long activityEventId = activityEvent.getId();

        // Get all the eventList where activityEvent equals to activityEventId
        defaultEventShouldBeFound("activityEventId.equals=" + activityEventId);

        // Get all the eventList where activityEvent equals to activityEventId + 1
        defaultEventShouldNotBeFound("activityEventId.equals=" + (activityEventId + 1));
    }


    @Test
    @Transactional
    public void getAllEventsByPlaceEventIsEqualToSomething() throws Exception {
        // Initialize the database
        Place placeEvent = PlaceResourceIntTest.createEntity(em);
        em.persist(placeEvent);
        em.flush();
        event.setPlaceEvent(placeEvent);
        eventRepository.saveAndFlush(event);
        Long placeEventId = placeEvent.getId();

        // Get all the eventList where placeEvent equals to placeEventId
        defaultEventShouldBeFound("placeEventId.equals=" + placeEventId);

        // Get all the eventList where placeEvent equals to placeEventId + 1
        defaultEventShouldNotBeFound("placeEventId.equals=" + (placeEventId + 1));
    }


    @Test
    @Transactional
    public void getAllEventsByEventUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User eventUser = UserResourceIntTest.createEntity(em);
        em.persist(eventUser);
        em.flush();
        event.addEventUser(eventUser);
        eventRepository.saveAndFlush(event);
        Long eventUserId = eventUser.getId();

        // Get all the eventList where eventUser equals to eventUserId
        defaultEventShouldBeFound("eventUserId.equals=" + eventUserId);

        // Get all the eventList where eventUser equals to eventUserId + 1
        defaultEventShouldNotBeFound("eventUserId.equals=" + (eventUserId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultEventShouldBeFound(String filter) throws Exception {
        restEventMockMvc.perform(get("/api/events?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].casual").value(hasItem(DEFAULT_CASUAL.booleanValue())))
            .andExpect(jsonPath("$.[*].hour").value(hasItem(DEFAULT_HOUR.toString())))
            .andExpect(jsonPath("$.[*].casualDescription").value(hasItem(DEFAULT_CASUAL_DESCRIPTION)));

        // Check, that the count call also returns 1
        restEventMockMvc.perform(get("/api/events/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultEventShouldNotBeFound(String filter) throws Exception {
        restEventMockMvc.perform(get("/api/events?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventMockMvc.perform(get("/api/events/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event
        Event updatedEvent = eventRepository.findById(event.getId()).get();
        // Disconnect from session so that the updates on updatedEvent are not directly saved in db
        em.detach(updatedEvent);
        updatedEvent
            .casual(UPDATED_CASUAL)
            .hour(UPDATED_HOUR)
            .casualDescription(UPDATED_CASUAL_DESCRIPTION);
        EventDTO eventDTO = eventMapper.toDto(updatedEvent);

        restEventMockMvc.perform(put("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.isCasual()).isEqualTo(UPDATED_CASUAL);
        assertThat(testEvent.getHour()).isEqualTo(UPDATED_HOUR);
        assertThat(testEvent.getCasualDescription()).isEqualTo(UPDATED_CASUAL_DESCRIPTION);

        // Validate the Event in Elasticsearch
        verify(mockEventSearchRepository, times(1)).save(testEvent);
    }

    @Test
    @Transactional
    public void updateNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc.perform(put("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Event in Elasticsearch
        verify(mockEventSearchRepository, times(0)).save(event);
    }

    @Test
    @Transactional
    public void deleteEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeDelete = eventRepository.findAll().size();

        // Delete the event
        restEventMockMvc.perform(delete("/api/events/{id}", event.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Event in Elasticsearch
        verify(mockEventSearchRepository, times(1)).deleteById(event.getId());
    }

    @Test
    @Transactional
    public void searchEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);
        when(mockEventSearchRepository.search(queryStringQuery("id:" + event.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(event), PageRequest.of(0, 1), 1));
        // Search the event
        restEventMockMvc.perform(get("/api/_search/events?query=id:" + event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].casual").value(hasItem(DEFAULT_CASUAL.booleanValue())))
            .andExpect(jsonPath("$.[*].hour").value(hasItem(DEFAULT_HOUR.toString())))
            .andExpect(jsonPath("$.[*].casualDescription").value(hasItem(DEFAULT_CASUAL_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);
        event2.setId(2L);
        assertThat(event1).isNotEqualTo(event2);
        event1.setId(null);
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventDTO.class);
        EventDTO eventDTO1 = new EventDTO();
        eventDTO1.setId(1L);
        EventDTO eventDTO2 = new EventDTO();
        assertThat(eventDTO1).isNotEqualTo(eventDTO2);
        eventDTO2.setId(eventDTO1.getId());
        assertThat(eventDTO1).isEqualTo(eventDTO2);
        eventDTO2.setId(2L);
        assertThat(eventDTO1).isNotEqualTo(eventDTO2);
        eventDTO1.setId(null);
        assertThat(eventDTO1).isNotEqualTo(eventDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(eventMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(eventMapper.fromId(null)).isNull();
    }
}
