package com.dyonishw.seekmeout.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the Place entity.
 */
public class PlaceDTO implements Serializable {

    private Long id;

    @NotNull
    private String address;

    private String possibleActivities;

    @NotNull
    private String phoneNumber;

    @NotNull
    @Size(min = 15, max = 255)
    private String description;

    @NotNull
    private String openHours;

    @NotNull
    @Size(min = 3)
    private String name;

    @NotNull
    @Min(value = 1)
    private Integer pricePerHour;

    private String contactForm;

    @Lob
    private byte[] pictures;

    private String picturesContentType;
    private String facilities;


    private Set<ActivityDTO> activityPlaces = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPossibleActivities() {
        return possibleActivities;
    }

    public void setPossibleActivities(String possibleActivities) {
        this.possibleActivities = possibleActivities;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Integer pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getContactForm() {
        return contactForm;
    }

    public void setContactForm(String contactForm) {
        this.contactForm = contactForm;
    }

    public byte[] getPictures() {
        return pictures;
    }

    public void setPictures(byte[] pictures) {
        this.pictures = pictures;
    }

    public String getPicturesContentType() {
        return picturesContentType;
    }

    public void setPicturesContentType(String picturesContentType) {
        this.picturesContentType = picturesContentType;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public Set<ActivityDTO> getActivityPlaces() {
        return activityPlaces;
    }

    public void setActivityPlaces(Set<ActivityDTO> activities) {
        this.activityPlaces = activities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlaceDTO placeDTO = (PlaceDTO) o;
        if (placeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), placeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PlaceDTO{" +
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
            ", facilities='" + getFacilities() + "'" +
            "}";
    }
}
