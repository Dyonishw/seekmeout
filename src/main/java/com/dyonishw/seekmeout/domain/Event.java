package com.dyonishw.seekmeout.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "event")
public class Event extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "casual", nullable = false)
    private Boolean casual;

    @NotNull
    @Column(name = "hour", nullable = false)
    private LocalDate hour;

    @Column(name = "casual_description")
    private String casualDescription;

    @ManyToOne
    @JsonIgnoreProperties("events")
    private Activity activityEvent;

    @ManyToOne
    @JsonIgnoreProperties("events")
    private Place placeEvent;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "event_event_user",
               joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "event_user_id", referencedColumnName = "id"))
    private Set<User> eventUsers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCasualDescription() {
        return casualDescription;
    }

    public Event casualDescription(String casualDescription) {
        this.casualDescription = casualDescription;
        return this;
    }

    public void setCasualDescription(String casualDescription) {
        this.casualDescription = casualDescription;
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

    public Set<User> getEventUsers() {
        return eventUsers;
    }

    public Event eventUsers(Set<User> users) {
        this.eventUsers = users;
        return this;
    }

    public Event addEventUser(User user) {
        this.eventUsers.add(user);
        return this;
    }

    public Event removeEventUser(User user) {
        this.eventUsers.remove(user);
        return this;
    }

    public void setEventUsers(Set<User> users) {
        this.eventUsers = users;
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
            ", casual='" + isCasual() + "'" +
            ", hour='" + getHour() + "'" +
            ", casualDescription='" + getCasualDescription() + "'" +
            "}";
    }
}
