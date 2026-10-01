package org.example.models;

import java.time.LocalDateTime;

public class MediaItem {
    private Long id;
    private String title;
    private String creator;
    private String mediaType;
    private String description;
    private boolean isPublic;
    private String owner;
    private LocalDateTime createdDate;

    public MediaItem() {
    }

    public MediaItem(Long id,
                     String title,
                     String creator,
                     String mediaType,
                     String description,
                     boolean isPublic,
                     String owner,
                     LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.mediaType = mediaType;
        this.description = description;
        this.isPublic = isPublic;
        this.owner = owner;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
