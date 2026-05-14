package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends Entity{
    private ChannelType type;
    private String name;
    private String description;
    private UUID authorId;
    public Channel(ChannelType type, String name, String description, UUID authorId) {
        super();
        this.type = type;
        this.name = name;
        this.description = description;
        this.authorId = authorId;
    }
    public void update(ChannelType newType, String newName, String newDescription) {
        this.name = newName;
        this.description = newDescription;
        this.type = newType;
        makeUpdate();
    }
    public ChannelType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Object getAuthorId() { return authorId;}
    @Override
    public String toString() {
        return "Channel{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }



}
