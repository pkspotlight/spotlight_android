package me.spotlight.spotlight.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Anatol on 7/14/2016.
 */
public class Team {

    String objectId;
    String avatarUrl;
    String name;
    String sport;
    String town;
    String teamName;
    Date createdAt;
    Date updatedAt;
    List<User> teamParticipants;
    TeamLogoMedia teamLogoMedia;
    String coach;
    String grade;
    String season;
    String year;
    List<User> moderators;


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<User> getTeamParticipants() {
        return teamParticipants;
    }

    public void setTeamParticipants(List<User> teamParticipants) {
        this.teamParticipants = teamParticipants;
    }

    public TeamLogoMedia getTeamLogoMedia() {
        return teamLogoMedia;
    }

    public void setTeamLogoMedia(TeamLogoMedia teamLogoMedia) {
        this.teamLogoMedia = teamLogoMedia;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<User> getModerators() {
        return moderators;
    }

    public void setModerators(List<User> moderators) {
        this.moderators = moderators;
    }
}
