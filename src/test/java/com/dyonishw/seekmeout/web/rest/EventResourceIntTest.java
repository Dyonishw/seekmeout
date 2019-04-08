package com.dyonishw.seekmeout.web.rest;

import com.dyonishw.seekmeout.SeekMeOutApp;

import com.dyonishw.seekmeout.domain.Event;
import com.dyonishw.seekmeout.domain.Activity;
import com.dyonishw.seekmeout.domain.Place;
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

    private static final String DEFAULT_ACTIVITY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVITY_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_TAKING_PLACE_AT = "AAAAAAAAAA";
    private static final String UPDATED_TAKING_PLACE_AT = "BBBBBBBBBB";

    private static final String DEFAULT_PEOPLE_ATTENDING = "AAAAAAAAAA";
    private static final String UPDATED_PEOPLE_ATTENDING = "BBBBBBBBBB";

    private static final Boolean DEFAULT_CASUAL = false;
    private static final Boolean UPDATED_CASUAL = true;

    private static final LocalDate DEFAULT_HOUR = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_HOUR = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

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
            .activityType(DEFAULT_ACTIVITY_TYPE)
            .takingPlaceAt(DEFAULT_TAKING_PLACE_AT)
            .peopleAttending(DEFAULT_PEOPLE_ATTENDING)
            .casual(DEFAULT_CASUAL)
            .hour(DEFAULT_HOUR);
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
        assertThat(testEvent.getActivityType()).isEqualTo(DEFAULT_ACTIVITY_TYPE);
        assertThat(testEvent.getTakingPlaceAt()).isEqualTo(DEFAULT_TAKING_PLACE_AT);
        assertThat(testEvent.getPeopleAttending()).isEqualTo(DEFAULT_PEOPLE_ATTENDING);
        assertThat(testEvent.isCasual()).isEqualTo(DEFAULT_CASUAL);
        assertThat(testEvent.getHour()).isEqualTo(DEFAULT_HOUR);

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
            .andExpect(jsonPath("$.[*].activityType").value(hasItem(DEFAULT_ACTIVITY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].takingPlaceAt").value(hasItem(DEFAULT_TAKING_PLACE_AT.toString())))
            .andExpect(jsonPath("$.[*].peopleAttending").value(hasItem(DEFAULT_PEOPLE_ATTENDING.toString())))
            .andExpect(jsonPath("$.[*].casual").value(hasItem(DEFAULT_CASUAL.booleanValue())))
            .andExpect(jsonPath("$.[*].hour").value(hasItem(DEFAULT_HOUR.toString())));
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
            .andExpect(jsonPath("$.activityType").value(DEFAULT_ACTIVITY_TYPE.toString()))
            .andExpect(jsonPath("$.takingPlaceAt").value(DEFAULT_TAKING_PLACE_AT.toString()))
            .andExpect(jsonPath("$.peopleAttending").value(DEFAULT_PEOPLE_ATTENDING.toString()))
            .andExpect(jsonPath("$.casual").value(DEFAULT_CASUAL.booleanValue()))
            .andExpect(jsonPath("$.hour").value(DEFAULT_HOUR.toString()));
    }

    @Test
    @Transactional
    public void getAllEventsByActivityTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where activityType equals to DEFAULT_ACTIVITY_TYPE
        defaultEventShouldBeFound("activityType.equals=" + DEFAULT_ACTIVITY_TYPE);

        // Get all the eventList where activityType equals to UPDATED_ACTIVITY_TYPE
        defaultEventShouldNotBeFound("activityType.equals=" + UPDATED_ACTIVITY_TYPE);
    }

    @Test
    @Transactional
    public void getAllEventsByActivityTypeIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where activityType in DEFAULT_ACTIVITY_TYPE or UPDATED_ACTIVITY_TYPE
        defaultEventShouldBeFound("activityType.in=" + DEFAULT_ACTIVITY_TYPE + "," + UPDATED_ACTIVITY_TYPE);

        // Get all the eventList where activityType equals to UPDATED_ACTIVITY_TYPE
        defaultEventShouldNotBeFound("activityType.in=" + UPDATED_ACTIVITY_TYPE);
    }

    @Test
    @Transactional
    public void getAllEventsByActivityTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where activityType is not null
        defaultEventShouldBeFound("activityType.specified=true");

        // Get all the eventList where activityType is null
        defaultEventShouldNotBeFound("activityType.specified=false");
    }

    @Test
    @Transactional
    public void getAllEventsByTakingPlaceAtIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where takingPlaceAt equals to DEFAULT_TAKING_PLACE_AT
        defaultEventShouldBeFound("takingPlaceAt.equals=" + DEFAULT_TAKING_PLACE_AT);

        // Get all the eventList where takingPlaceAt equals to UPDATED_TAKING_PLACE_AT
        defaultEventShouldNotBeFound("takingPlaceAt.equals=" + UPDATED_TAKING_PLACE_AT);
    }

    @Test
    @Transactional
    public void getAllEventsByTakingPlaceAtIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where takingPlaceAt in DEFAULT_TAKING_PLACE_AT or UPDATED_TAKING_PLACE_AT
        defaultEventShouldBeFound("takingPlaceAt.in=" + DEFAULT_TAKING_PLACE_AT + "," + UPDATED_TAKING_PLACE_AT);

        // Get all the eventList where takingPlaceAt equals to UPDATED_TAKING_PLACE_AT
        defaultEventShouldNotBeFound("takingPlaceAt.in=" + UPDATED_TAKING_PLACE_AT);
    }

    @Test
    @Transactional
    public void getAllEventsByTakingPlaceAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where takingPlaceAt is not null
        defaultEventShouldBeFound("takingPlaceAt.specified=true");

        // Get all the eventList where takingPlaceAt is null
        defaultEventShouldNotBeFound("takingPlaceAt.specified=false");
    }

    @Test
    @Transactional
    public void getAllEventsByPeopleAttendingIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where peopleAttending equals to DEFAULT_PEOPLE_ATTENDING
        defaultEventShouldBeFound("peopleAttending.equals=" + DEFAULT_PEOPLE_ATTENDING);

        // Get all the eventList where peopleAttending equals to UPDATED_PEOPLE_ATTENDING
        defaultEventShouldNotBeFound("peopleAttending.equals=" + UPDATED_PEOPLE_ATTENDING);
    }

    @Test
    @Transactional
    public void getAllEventsByPeopleAttendingIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where peopleAttending in DEFAULT_PEOPLE_ATTENDING or UPDATED_PEOPLE_ATTENDING
        defaultEventShouldBeFound("peopleAttending.in=" + DEFAULT_PEOPLE_ATTENDING + "," + UPDATED_PEOPLE_ATTENDING);

        // Get all the eventList where peopleAttending equals to UPDATED_PEOPLE_ATTENDING
        defaultEventShouldNotBeFound("peopleAttending.in=" + UPDATED_PEOPLE_ATTENDING);
    }

    @Test
    @Transactional
    public void getAllEventsByPeopleAttendingIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where peopleAttending is not null
        defaultEventShouldBeFound("peopleAttending.specified=true");

        // Get all the eventList where peopleAttending is null
        defaultEventShouldNotBeFound("peopleAttending.specified=false");
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

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultEventShouldBeFound(String filter) throws Exception {
        restEventMockMvc.perform(get("/api/events?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].activityType").value(hasItem(DEFAULT_ACTIVITY_TYPE)))
            .andExpect(jsonPath("$.[*].takingPlaceAt").value(hasItem(DEFAULT_TAKING_PLACE_AT)))
            .andExpect(jsonPath("$.[*].peopleAttending").value(hasItem(DEFAULT_PEOPLE_ATTENDING)))
            .andExpect(jsonPath("$.[*].casual").value(hasItem(DEFAULT_CASUAL.booleanValue())))
            .andExpect(jsonPath("$.[*].hour").value(hasItem(DEFAULT_HOUR.toString())));

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
            .activityType(UPDATED_ACTIVITY_TYPE)
            .takingPlaceAt(UPDATED_TAKING_PLACE_AT)
            .peopleAttending(UPDATED_PEOPLE_ATTENDING)
            .casual(UPDATED_CASUAL)
            .hour(UPDATED_HOUR);
        EventDTO eventDTO = eventMapper.toDto(updatedEvent);

        restEventMockMvc.perform(put("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getActivityType()).isEqualTo(UPDATED_ACTIVITY_TYPE);
        assertThat(testEvent.getTakingPlaceAt()).isEqualTo(UPDATED_TAKING_PLACE_AT);
        assertThat(testEvent.getPeopleAttending()).isEqualTo(UPDATED_PEOPLE_ATTENDING);
        assertThat(testEvent.isCasual()).isEqualTo(UPDATED_CASUAL);
        assertThat(testEvent.getHour()).isEqualTo(UPDATED_HOUR);

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
            .andExpect(jsonPath("$.[*].activityType").value(hasItem(DEFAULT_ACTIVITY_TYPE)))
            .andExpect(jsonPath("$.[*].takingPlaceAt").value(hasItem(DEFAULT_TAKING_PLACE_AT)))
            .andExpect(jsonPath("$.[*].peopleAttending").value(hasItem(DEFAULT_PEOPLE_ATTENDING)))
            .andExpect(jsonPath("$.[*].casual").value(hasItem(DEFAULT_CASUAL.booleanValue())))
            .andExpect(jsonPath("$.[*].hour").value(hasItem(DEFAULT_HOUR.toString())));
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
