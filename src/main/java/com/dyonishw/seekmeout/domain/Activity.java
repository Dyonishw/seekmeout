package com.dyonishw.seekmeout.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "jhi_type", nullable = false)
    private String type;

    @NotNull
    @Min(value = 2)
    @Column(name = "number_of_players", nullable = false)
    private Integer numberOfPlayers;

    @Column(name = "official_duration")
    private String officialDuration;

    @Column(name = "official_rules")
    private String officialRules;

    @Size(min = 15)
    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "recommended_gear")
    private String recommendedGear;

    @Column(name = "long_description")
    private String longDescription;

    @ManyToMany(mappedBy = "activityPlaces")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Place> activityPlaces = new HashSet<>();

    @OneToMany(mappedBy = "activityEvent")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Event> activityEvents = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public Activity type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Activity numberOfPlayers(Integer numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        return this;
    }

    public void setNumberOfPlayers(Integer numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getOfficialDuration() {
        return officialDuration;
    }

    public Activity officialDuration(String officialDuration) {
        this.officialDuration = officialDuration;
        return this;
    }

    public void setOfficialDuration(String officialDuration) {
        this.officialDuration = officialDuration;
    }

    public String getOfficialRules() {
        return officialRules;
    }

    public Activity officialRules(String officialRules) {
        this.officialRules = officialRules;
        return this;
    }

    public void setOfficialRules(String officialRules) {
        this.officialRules = officialRules;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public Activity shortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getRecommendedGear() {
        return recommendedGear;
    }

    public Activity recommendedGear(String recommendedGear) {
        this.recommendedGear = recommendedGear;
        return this;
    }

    public void setRecommendedGear(String recommendedGear) {
        this.recommendedGear = recommendedGear;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Activity longDescription(String longDescription) {
        this.longDescription = longDescription;
        return this;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public Set<Place> getActivityPlaces() {
        return activityPlaces;
    }

    public Activity activityPlaces(Set<Place> places) {
        this.activityPlaces = places;
        return this;
    }

    public Activity addActivityPlace(Place place) {
        this.activityPlaces.add(place);
        place.getActivityPlaces().add(this);
        return this;
    }

    public Activity removeActivityPlace(Place place) {
        this.activityPlaces.remove(place);
        place.getActivityPlaces().remove(this);
        return this;
    }

    public void setActivityPlaces(Set<Place> places) {
        this.activityPlaces = places;
    }

    public Set<Event> getActivityEvents() {
        return activityEvents;
    }

    public Activity activityEvents(Set<Event> events) {
        this.activityEvents = events;
        return this;
    }

    public Activity addActivityEvent(Event event) {
        this.activityEvents.add(event);
        event.setActivityEvent(this);
        return this;
    }

    public Activity removeActivityEvent(Event event) {
        this.activityEvents.remove(event);
        event.setActivityEvent(null);
        return this;
    }

    public void setActivityEvents(Set<Event> events) {
        this.activityEvents = events;
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
        Activity activity = (Activity) o;
        if (activity.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), activity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", numberOfPlayers=" + getNumberOfPlayers() +
            ", officialDuration='" + getOfficialDuration() + "'" +
            ", officialRules='" + getOfficialRules() + "'" +
            ", shortDescription='" + getShortDescription() + "'" +
            ", recommendedGear='" + getRecommendedGear() + "'" +
            ", longDescription='" + getLongDescription() + "'" +
            "}";
    }
}
