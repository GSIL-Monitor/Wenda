package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel{
    private EventType type;
    private int actorId;
    private int entityId;
    private int entityType;
    private int entityOwnerId;
    private Map<String, String> exts = new HashMap<>();

    public EventModel() {

    }

    /**
     * @return the type
     */
    public EventType getType() {
        return type;
    }

    /**
     * @return the exts
     */
    public String getExts(String key) {
        return exts.get(key);
    }

    /**
     * @param exts the exts to set
     */
    public EventModel setExts(String key,String value) {
        exts.put(key, value);
        return this;
    }

    /**
     * @return the entityOwnerId
     */
    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    /**
     * @param entityOwnerId the entityOwnerId to set
     */
    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    /**
     * @return the entityType
     */
    public int getEntityType() {
        return entityType;
    }

    /**
     * @param entityType the entityType to set
     */
    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    /**
     * @return the entityId
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @param entityId the entityId to set
     */
    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    /**
     * @return the actorId
     */
    public int getActorId() {
        return actorId;
    }

    /**
     * @param actorId the actorId to set
     */
    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    /**
     * @param type the type to set
     */
    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }
}