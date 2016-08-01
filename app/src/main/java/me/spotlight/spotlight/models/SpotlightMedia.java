package me.spotlight.spotlight.models;

import com.parse.ParseFile;

import java.util.Date;
import java.util.List;

/**
 * Created by Anatol on 7/14/2016.
 */
public class SpotlightMedia {

    String objectId;
    boolean isVideo;
    String thumbnailUrl;
    String fileUrl;
    ParseFile mediaFile;
    Spotlight parent;
    String parentId;
    ParseFile thumbnailImageFile;
    Date createdAt;
    Date updatedAt;
    List<User> likes;
    String title;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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

    public Spotlight getParent() {
        return parent;
    }

    public void setParent(Spotlight parent) {
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

    public List<User> getLikes() {
        return likes;
    }

    public void setLikes(List<User> likes) {
        this.likes = likes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
