package org.example.models;

public class AddItemToCollectionRequest {
    private Long itemId;

    public AddItemToCollectionRequest() {
    }

    public AddItemToCollectionRequest(Long itemId) {
        this.itemId = itemId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
