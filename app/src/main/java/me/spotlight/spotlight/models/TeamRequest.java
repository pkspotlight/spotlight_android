package me.spotlight.spotlight.models;

/**
 * Created by Anatol on 8/5/2016.
 */
public class TeamRequest {

    String avatarUrl;
    String objectId;
    int state;
    String requesterName;
    String teamName;
    String requesterObjId;
    String teamObjectId;

    public String getTeamObjectId() {
        return teamObjectId;
    }

    public void setTeamObjectId(String teamObjectId) {
        this.teamObjectId = teamObjectId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getRequesterObjId() {
        return requesterObjId;
    }

    public void setRequesterObjId(String requesterObjId) {
        this.requesterObjId = requesterObjId;
    }
}
