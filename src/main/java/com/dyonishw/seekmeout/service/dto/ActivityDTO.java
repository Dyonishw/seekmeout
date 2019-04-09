package com.dyonishw.seekmeout.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Activity entity.
 */
public class ActivityDTO implements Serializable {

    private Long id;

    @NotNull
    private String type;

    @NotNull
    @Min(value = 2)
    private Integer numberOfPlayers;

    private String officialDuration;

    private String officialRules;

    @Size(min = 15)
    private String shortDescription;

    private String recommendedGear;

    private String longDescription;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Integer numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getOfficialDuration() {
        return officialDuration;
    }

    public void setOfficialDuration(String officialDuration) {
        this.officialDuration = officialDuration;
    }

    public String getOfficialRules() {
        return officialRules;
    }

    public void setOfficialRules(String officialRules) {
        this.officialRules = officialRules;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getRecommendedGear() {
        return recommendedGear;
    }

    public void setRecommendedGear(String recommendedGear) {
        this.recommendedGear = recommendedGear;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActivityDTO activityDTO = (ActivityDTO) o;
        if (activityDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), activityDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ActivityDTO{" +
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
