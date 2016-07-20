package me.spotlight.spotlight.models;

import com.parse.ParseFile;

import java.util.Date;

/**
 * Created by Anatol on 7/14/2016.
 */
public class TeamLogoMedia {

    String objectId;
    boolean isVideo;
    ParseFile mediaFile;
    Team parent;
    ParseFile thumbnailImageFile;
    Date createdAt;
    Date updatedAt;


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public ParseFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(ParseFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    public Team getParent() {
        return parent;
    }

    public void setParent(Team parent) {
        this.parent = parent;
    }

    public ParseFile getThumbnailImageFile() {
        return thumbnailImageFile;
    }

    public void setThumbnailImageFile(ParseFile thumbnailImageFile) {
        this.thumbnailImageFile = thumbnailImageFile;
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
}
