package com.sprint.mission.discodeit.entity;

public class Channel extends Entity{
    ChannelType type;
    String name;
    String description;

    public Channel(ChannelType type, String name, String description) {
        super();
        this.type = type;
        this.name = name;
        this.description = description;
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


    @Override
    public String toString() {
        return "Channel{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


}
