package com.ef.firebasenotification.web;

import com.ef.firebasenotification.domain.NotificationContent;
import com.ef.firebasenotification.domain.DeviceToken;
import com.ef.firebasenotification.domain.NotificationData;
import com.ef.firebasenotification.repository.DeviceTokenDAO;
import com.ef.firebasenotification.service.DeviceTokenService;
import com.ef.firebasenotification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/notification")
public class NotificationREST {

    @Autowired
    DeviceTokenService deviceTokenService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    DeviceTokenDAO deviceTokenDAO;

    @PostMapping("/all-device")
    public ResponseEntity<CustomResponse> save(@RequestParam("title") String title, @RequestParam("body") String body) throws Exception {
        sendPushNotification(title, body);
        return new ResponseEntity<>(new CustomResponse(HttpStatus.OK.toString()), HttpStatus.OK);
    }

    private void sendPushNotification(String title, String body) {

        // New Object
        NotificationContent data = new NotificationContent();
        NotificationData notification = new NotificationData();

        // Notification Data
        data.setClickAction("FLUTTER_NOTIFICATION_CLICK");
        data.setImageURL("https://ibin.co/2t1lLdpfS06F.png");
        data.setSound("default");
        data.setType("global");
        data.setChatRoomId(1L);

        // Notification Title And Body Message
        notification.setTitle(title);
        notification.setBody(body);

        notification.setData(data);

        // Send chatNotificationService
        notificationService.sendAllDevices(notification);

    }


}
