package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public enum ChannelType  implements Serializable {
    PUBLIC("공개",true ),
    PRIVATE("비공개", false );

    //필드 선언
    private final String description; //패널 타입 설명
    private final boolean isPublic; //공개 여부

    //생성자에게 필드에 저장

    ChannelType(String description, boolean isPublic) {
        this.description = description;
        this.isPublic = isPublic;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPublic() {
        return isPublic;
    }
}
