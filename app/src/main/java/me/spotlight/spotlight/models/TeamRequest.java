package me.spotlight.spotlight.models;

/**
 * Created by Anatol on 8/5/2016.
 */
public class TeamRequest {

    String avatarUrl;
    int state;
    String requesterName;


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
}
