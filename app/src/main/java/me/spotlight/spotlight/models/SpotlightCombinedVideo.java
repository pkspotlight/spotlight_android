package me.spotlight.spotlight.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Anatol on 7/14/2016.
 */
public class SpotlightCombinedVideo {

    String objectId;
    boolean combined;
    // uploader ?
    String userId;
    Date createdAt;
    Date updatedAt;
    List<SpotlightUserUploadedVideo> videos;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isCombined() {
        return combined;
    }

    public void setCombined(boolean combined) {
        this.combined = combined;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public List<SpotlightUserUploadedVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<SpotlightUserUploadedVideo> videos) {
        this.videos = videos;
    }
}
