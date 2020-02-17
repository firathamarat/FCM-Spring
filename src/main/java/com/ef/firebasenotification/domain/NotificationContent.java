package com.ef.firebasenotification.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotificationContent {

    private String clickAction;

    private String sound;

    private String type;

    private String imageURL;

    private Long chatRoomId;

}
