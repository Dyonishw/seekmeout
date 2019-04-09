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
 * A Place.
 */
@Entity
@Table(name = "place")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "place")
public class Place implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "possible_activities")
    private String possibleActivities;

    @NotNull
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @NotNull
    @Size(min = 15, max = 255)
    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @NotNull
    @Column(name = "open_hours", nullable = false)
    private String openHours;

    @NotNull
    @Size(min = 3)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Min(value = 1)
    @Column(name = "price_per_hour", nullable = false)
    private Integer pricePerHour;

    @Column(name = "contact_form")
    private String contactForm;

    @Lob
    @Column(name = "pictures")
    private byte[] pictures;

    @Column(name = "pictures_content_type")
    private String picturesContentType;

    @Column(name = "facilities")
    private String facilities;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "place_activity_place",
               joinColumns = @JoinColumn(name = "place_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "activity_place_id", referencedColumnName = "id"))
    private Set<Activity> activityPlaces = new HashSet<>();

    @OneToMany(mappedBy = "placeEvent")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Event> placeEvents = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public Place address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPossibleActivities() {
        return possibleActivities;
    }

    public Place possibleActivities(String possibleActivities) {
        this.possibleActivities = possibleActivities;
        return this;
    }

    public void setPossibleActivities(String possibleActivities) {
        this.possibleActivities = possibleActivities;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Place phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public Place description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpenHours() {
        return openHours;
    }

    public Place openHours(String openHours) {
        this.openHours = openHours;
        return this;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getName() {
        return name;
    }

    public Place name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPricePerHour() {
        return pricePerHour;
    }

    public Place pricePerHour(Integer pricePerHour) {
        this.pricePerHour = pricePerHour;
        return this;
    }

    public void setPricePerHour(Integer pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getContactForm() {
        return contactForm;
    }

    public Place contactForm(String contactForm) {
        this.contactForm = contactForm;
        return this;
    }

    public void setContactForm(String contactForm) {
        this.contactForm = contactForm;
    }

    public byte[] getPictures() {
        return pictures;
    }

    public Place pictures(byte[] pictures) {
        this.pictures = pictures;
        return this;
    }

    public void setPictures(byte[] pictures) {
        this.pictures = pictures;
    }

    public String getPicturesContentType() {
        return picturesContentType;
    }

    public Place picturesContentType(String picturesContentType) {
        this.picturesContentType = picturesContentType;
        return this;
    }

    public void setPicturesContentType(String picturesContentType) {
        this.picturesContentType = picturesContentType;
    }

    public String getFacilities() {
        return facilities;
    }

    public Place facilities(String facilities) {
        this.facilities = facilities;
        return this;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public Set<Activity> getActivityPlaces() {
        return activityPlaces;
    }

    public Place activityPlaces(Set<Activity> activities) {
        this.activityPlaces = activities;
        return this;
    }

    public Place addActivityPlace(Activity activity) {
        this.activityPlaces.add(activity);
        activity.getActivityPlaces().add(this);
        return this;
    }

    public Place removeActivityPlace(Activity activity) {
        this.activityPlaces.remove(activity);
        activity.getActivityPlaces().remove(this);
        return this;
    }

    public void setActivityPlaces(Set<Activity> activities) {
        this.activityPlaces = activities;
    }

    public Set<Event> getPlaceEvents() {
        return placeEvents;
    }

    public Place placeEvents(Set<Event> events) {
        this.placeEvents = events;
        return this;
    }

    public Place addPlaceEvent(Event event) {
        this.placeEvents.add(event);
        event.setPlaceEvent(this);
        return this;
    }

    public Place removePlaceEvent(Event event) {
        this.placeEvents.remove(event);
        event.setPlaceEvent(null);
        return this;
    }

    public void setPlaceEvents(Set<Event> events) {
        this.placeEvents = events;
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
        Place place = (Place) o;
        if (place.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), place.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Place{" +
            "id=" + getId() +
            ", address='" + getAddress() + "'" +
            ", possibleActivities='" + getPossibleActivities() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", description='" + getDescription() + "'" +
            ", openHours='" + getOpenHours() + "'" +
            ", name='" + getName() + "'" +
            ", pricePerHour=" + getPricePerHour() +
            ", contactForm='" + getContactForm() + "'" +
            ", pictures='" + getPictures() + "'" +
            ", picturesContentType='" + getPicturesContentType() + "'" +
            ", facilities='" + getFacilities() + "'" +
            "}";
    }
}
