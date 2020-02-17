package com.ef.firebasenotification.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotificationData {

    private Long id;

    private String title;

    private String body;

    private String topic;

    private NotificationContent data;

}
