package org.example.models;

import java.time.LocalDateTime;

public class MediaCollection {
    private Long id;
    private String name;
    private String description;
    private boolean isPublic;
    private String owner;
    private LocalDateTime createdDate;

    public MediaCollection() {
    }

    public MediaCollection(Long id,
                           String name,
                           String description,
                           boolean isPublic,
                           String owner,
                           LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
