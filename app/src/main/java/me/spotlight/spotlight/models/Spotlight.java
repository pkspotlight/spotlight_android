package me.spotlight.spotlight.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Anatol on 7/14/2016.
 */
public class Spotlight {

    String objectId;
    String title;
    String teamsAvatar;
    String cover;
    List<String> coverUrls = new ArrayList<>();
    Date createdAt;
    Date updatedAt;
    User spotlightParticipant;
    String creatorName;
    User creator;
    List<User> moderators;
    Team team;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getCoverUrls() {
        return coverUrls;
    }

    public void setCoverUrl(List<String> coverUrls) {
        this.coverUrls = coverUrls;
    }

    public String getTeamsAvatar() {
        return teamsAvatar;
    }

    public void setTeamsAvatar(String teamsAvatar) {
        this.teamsAvatar = teamsAvatar;
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

    public User getSpotlightParticipant() {
        return spotlightParticipant;
    }

    public void setSpotlightParticipant(User spotlightParticipant) {
        this.spotlightParticipant = spotlightParticipant;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<User> getModerators() {
        return moderators;
    }

    public void setModerators(List<User> moderators) {
        this.moderators = moderators;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
