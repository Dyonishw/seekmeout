package com.dyonishw.seekmeout.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "event")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "activity_type")
    private String activityType;

    @Column(name = "taking_place_at")
    private String takingPlaceAt;

    @Column(name = "people_attending")
    private String peopleAttending;

    @NotNull
    @Column(name = "casual", nullable = false)
    private Boolean casual;

    @NotNull
    @Column(name = "hour", nullable = false)
    private LocalDate hour;

    @ManyToOne
    @JsonIgnoreProperties("events")
    private Activity activityEvent;

    @ManyToOne
    @JsonIgnoreProperties("events")
    private Place placeEvent;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityType() {
        return activityType;
    }

    public Event activityType(String activityType) {
        this.activityType = activityType;
        return this;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getTakingPlaceAt() {
        return takingPlaceAt;
    }

    public Event takingPlaceAt(String takingPlaceAt) {
        this.takingPlaceAt = takingPlaceAt;
        return this;
    }

    public void setTakingPlaceAt(String takingPlaceAt) {
        this.takingPlaceAt = takingPlaceAt;
    }

    public String getPeopleAttending() {
        return peopleAttending;
    }

    public Event peopleAttending(String peopleAttending) {
        this.peopleAttending = peopleAttending;
        return this;
    }

    public void setPeopleAttending(String peopleAttending) {
        this.peopleAttending = peopleAttending;
    }

    public Boolean isCasual() {
        return casual;
    }

    public Event casual(Boolean casual) {
        this.casual = casual;
        return this;
    }

    public void setCasual(Boolean casual) {
        this.casual = casual;
    }

    public LocalDate getHour() {
        return hour;
    }

    public Event hour(LocalDate hour) {
        this.hour = hour;
        return this;
    }

    public void setHour(LocalDate hour) {
        this.hour = hour;
    }

    public Activity getActivityEvent() {
        return activityEvent;
    }

    public Event activityEvent(Activity activity) {
        this.activityEvent = activity;
        return this;
    }

    public void setActivityEvent(Activity activity) {
        this.activityEvent = activity;
    }

    public Place getPlaceEvent() {
        return placeEvent;
    }

    public Event placeEvent(Place place) {
        this.placeEvent = place;
        return this;
    }

    public void setPlaceEvent(Place place) {
        this.placeEvent = place;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        if (event.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", activityType='" + getActivityType() + "'" +
            ", takingPlaceAt='" + getTakingPlaceAt() + "'" +
            ", peopleAttending='" + getPeopleAttending() + "'" +
            ", casual='" + isCasual() + "'" +
            ", hour='" + getHour() + "'" +
            "}";
    }
}
