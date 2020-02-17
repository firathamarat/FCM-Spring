package com.ef.firebasenotification.service;

import com.ef.firebasenotification.domain.DeviceToken;
import com.ef.firebasenotification.domain.NotificationData;
import com.ef.firebasenotification.domain.NotificationType;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired DeviceTokenService deviceTokenService;


    public Map<String, String> dataHashMapper(NotificationData notification, NotificationType notificationType) {

        // Hash Mapping
        Map<String, String> map = new HashMap<>();
        map.put("click_action", notification.getData().getClickAction());
        map.put("sound", notification.getData().getSound());
        // map.put("type", notificationType.getValue());
        map.put("image", notification.getData().getImageURL());
        map.put("chat_room_id", notification.getData().getChatRoomId().toString());

        if (notificationType.getValue().equals("TOPIC")) {
            map.put("type", "topic");
        } else if (notificationType.getValue().equals("SPECIFIC")) {
            map.put("type", "specific");
        } else if (notificationType.getValue().equals("GROUP")) {
            map.put("type", "group");
        } else if (notificationType.getValue().equals("ALL")) {
            map.put("type", "all");
        } else {
            map.put("type", "unknown");
        }

        return map;
    }

    // TOPIC Notification
    public ResponseEntity<?> sendToTopic(NotificationData notification, String topic) {

        // Message Builder
        Message message = Message.builder().putAllData(dataHashMapper(notification, NotificationType.TOPIC))

                // Notification Title and Body
                .setNotification(new Notification(notification.getTitle(), notification.getBody()))

                // Android Notification Configuration
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(Duration.ofMinutes(3600).toMillis()) // Time to live - 4 weeks default and
                        .setPriority(AndroidConfig.Priority.NORMAL)
                        .setNotification(AndroidNotification.builder()
                                .setIcon("stock")
                                .setColor("#168070")
                                .build())
                        .build())

                // IOS Notification Configuration
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setBadge(1)
                                .build())
                        .build())

                .setTopic(topic)
                .build(); // Message.builder

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            return new ResponseEntity<>("[TOPIC] Message sent successfully: \n" + response, HttpStatus.OK);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("[TOPIC] Notification sending failed. Try again.", HttpStatus.EXPECTATION_FAILED);
        }
    }

    // Subscribe Topic
    public void subscribeToTopic(String topic) throws Exception {
        List<String> tokens = new ArrayList<>();
        // Token Request
        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopicAsync(tokens, topic).get();
        System.out.println(response.getSuccessCount() + " tokens were subscribed successfully");
    }

    // unSubscribe Topic
    public void unsubscribeFromTopic(String topic) throws Exception {
        List<String> tokens = new ArrayList<>();
        // Token Request
        TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(tokens, topic).get();
        System.out.println(response.getSuccessCount() + " tokens were unsubscribed successfully");
    }

    // SPECIFIC Notification
    public ResponseEntity<?> sendToSpecificDevice(NotificationData notification, Long userId) {

        // User's Token Information
        DeviceToken deviceToken = deviceTokenService.findTokenByUserId(userId);

        // Message Builder
        Message message = Message.builder().putAllData(dataHashMapper(notification, NotificationType.SPECIFIC))

                // Notification Title and Body
                .setNotification(new Notification(notification.getTitle(), notification.getBody()))

                // Android Notification Configuration
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(Duration.ofMinutes(3600).toMillis()) // Time to live - 4 weeks default and
                        .setPriority(AndroidConfig.Priority.NORMAL)
                        .setNotification(AndroidNotification.builder()
                                .setIcon("stock")
                                .setColor("#168070")
                                .build())
                        .build())

                // IOS Notification Configuration
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setBadge(1)
                                .build())
                        .build())

                .setToken(deviceToken.getDeviceToken())
                .build(); // Message.builder

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            return new ResponseEntity<>("(SPECIFIC) Message sent successfully: \n" + response, HttpStatus.OK);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("(SPECIFIC) Notification sending failed. Try again.", HttpStatus.EXPECTATION_FAILED);
        }
    }


    /*
    // Group Notification
    public ResponseEntity<?> sendToGroupDevice(NotificationData notification, Long chatRoomId) {


        ArrayList<Long> chatUsers = new ArrayList<>();
        ChatRoom chatRoom = chatRoomService.getChatRoomById(chatRoomId);

        chatRoom.getUsers().forEach(chatUserId -> chatUsers.add(chatUserId.getId()));
        socketService.getOnlineUsers().stream().filter(onlineUser -> onlineUser.getPlatform().equals("mobile")).forEach(onlineUser ->
                chatUsers.remove(onlineUser.getUserid())
        );

        // All Device Token
        ArrayList<DeviceToken> deviceTokens = (ArrayList<DeviceToken>) deviceTokenService.getAllByUserIds(chatUsers);
        ArrayList<String> tokens = new ArrayList<>();
        deviceTokens.forEach(deviceToken -> tokens.add(deviceToken.getDeviceToken()));


        // Device Tokens Check
        if (deviceTokens.size() > 0) {

            // MulticastMessage Builder
            MulticastMessage message = MulticastMessage.builder().putAllData(dataHashMapper(chatNotification, ChatNotificationType.GROUP)).addAllTokens(tokens)

                    // Notification Title and Body
                    .setNotification(new NotificationData(chatNotification.getTitle(), chatNotification.getBody()))

                    // Android Notification Configuration
                    .setAndroidConfig(AndroidConfig.builder()
                            .setTtl(Duration.ofMinutes(3600).toMillis()) // Time to live - 4 weeks default and
                            .setPriority(AndroidConfig.Priority.NORMAL)
                            .setNotification(AndroidNotification.builder()
                                    .setIcon("stock")
                                    .setColor("#168070")
                                    .build())
                            .build())

                    // IOS Notification Configuration
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setBadge(1)
                                    .build())
                            .build())

                    .build(); // MulticastMessage.builder

            try {
                BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
                return new ResponseEntity<>("(GROUP) Messages sent successfully: \n" + response, HttpStatus.OK);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                return new ResponseEntity<>("(GROUP) Notification sending failed. Try again.", HttpStatus.EXPECTATION_FAILED);
            }

        }

        return null;

    }
    */


    // All Devices Notification
    public ResponseEntity<?> sendAllDevices(NotificationData notification) {

        // All Device Token
        ArrayList<DeviceToken> deviceTokens = (ArrayList<DeviceToken>) deviceTokenService.getAll();
        List<String> tokens = new ArrayList<>();

        tokens = deviceTokens.stream().map(DeviceToken::getDeviceToken).filter(Objects::nonNull).collect(Collectors.toList());
        if (tokens.size() == 0) {
            tokens.add("empty-token");
        }

        // Device Tokens Check
        if (deviceTokens.size() > 0) {

            // MulticastMessage Builder
            MulticastMessage message = MulticastMessage.builder().putAllData(dataHashMapper(notification, NotificationType.ALL)).addAllTokens(tokens)

                    // Notification Title and Body
                    .setNotification(new Notification(notification.getTitle(), notification.getBody()))

                    // Android Notification Configuration
                    .setAndroidConfig(AndroidConfig.builder()
                            .setTtl(Duration.ofMinutes(3600).toMillis()) // Time to live - 4 weeks default and
                            .setPriority(AndroidConfig.Priority.NORMAL)
                            .setNotification(AndroidNotification.builder()
                                    .setIcon("stock")
                                    .setColor("#168070")
                                    .build())
                            .build())

                    // IOS Notification Configuration
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setBadge(1)
                                    .build())
                            .build())

                    .build(); // MulticastMessage.builder

            try {
                BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
                return new ResponseEntity<>("Messages sent successfully: \n" + response, HttpStatus.OK);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Notification sending failed. Try again.", HttpStatus.EXPECTATION_FAILED);
            }

        }

        return null;

    }


    /*
    // Generic Notification [Chat Notification Data, Notification Type, (specific: userId )]
    public ResponseEntity<?> notification(NotificationData notification, NotificationType notificationType) {

        // Notification HashMap
        Map<String, String> map = new HashMap<>();
        map.put("click_action", notification.getData().getClickAction());
        map.put("sound", notification.getData().getSound());
        map.put("image", notification.getData().getImageURL());
        map.put("chat_room_id", notification.getData().getChatRoomId().toString());

        try {
            // Notification Type ALL and GROUP Check
            if (notificationType.getValue().equals("ALL") || notificationType.getValue().equals("GROUP")) {
                map.put("type", "multicast");
                // Generic MessageBuilder Function
                MulticastMessage message = (MulticastMessage) MessageBuilder(map, notification, notificationType, null);
                BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
                return new ResponseEntity<>("[Multicast]: Messages sent successfully: \n" + response, HttpStatus.OK);
            } else {
                map.put("type", "specific");
                // Generic MessageBuilder Function
                Message message = (Message) MessageBuilder(map, chatNotification, chatNotificationType, null);
                String response = FirebaseMessaging.getInstance().send(message);
                return new ResponseEntity<>("[Message]: Message sent successfully: \n" + response, HttpStatus.OK);
            }

        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Message sending failed. Try again.", HttpStatus.EXPECTATION_FAILED);
        }

    }
    */

    /*
    // Generic MessageBuilder [notification.map, Chat Notification Data, Notification Type]
    public Object MessageBuilder(Map<String, String> map, NotificationData notification, NotificationType notificationType, @Nullable String topic) {

        // Notification Type ALL and GROUP Check
        if (notificationType.getValue().equals("ALL") || notificationType.getValue().equals("GROUP")) {

            // MulticastMessage Builder
            MulticastMessage message = MulticastMessage.builder().putAllData(dataHashMapper(notification, notificationType)).addAllTokens(userTokens(notificationType, notification))

                    // Notification Title and Body
                    .setNotification(new Notification(notification.getTitle(), notification.getBody()))
                    // Android Notification Configuration
                    .setAndroidConfig(AndroidConfig.builder()
                            .setTtl(Duration.ofMinutes(3600).toMillis()) // Time to live - 4 weeks default and
                            .setPriority(AndroidConfig.Priority.NORMAL)
                            .setNotification(AndroidNotification.builder()
                                    .setIcon("stock")
                                    .setColor("#168070")
                                    .build())
                            .build())

                    // IOS Notification Configuration
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setBadge(1)
                                    .build())
                            .build())

                    .build(); // MulticastMessage.builder
            return message;


        } else if (notificationType.getValue().equals("TOPIC") || notificationType.getValue().equals("SPECIFIC")) {
            {

                if (notificationType.getValue().equals("TOPIC")) {
                    // Message Builder
                    Message message = Message.builder().putAllData(map)

                            // Notification Title and Body
                            .setNotification(new Notification(notification.getTitle(), notification.getBody()))

                            // Android Notification Configuration
                            .setAndroidConfig(AndroidConfig.builder()
                                    .setTtl(Duration.ofMinutes(3600).toMillis()) // Time to live - 4 weeks default and
                                    .setPriority(AndroidConfig.Priority.NORMAL)
                                    .setNotification(AndroidNotification.builder()
                                            .setIcon("stock")
                                            .setColor("#168070")
                                            .build())
                                    .build())

                            // IOS Notification Configuration
                            .setApnsConfig(ApnsConfig.builder()
                                    .setAps(Aps.builder()
                                            .setBadge(1)
                                            .build())
                                    .build())

                            .setTopic(topic)
                            .build(); // Message.builder
                }

                if (notificationType.getValue().equals("SPECIFIC")) {

                    // User's Token Information
                    DeviceToken deviceToken = deviceTokenService.findTokenByUserId(notification.getData().getChatRoomId());

                    // Message Builder
                    Message message = Message.builder().putAllData(map)

                            // Notification Title and Body
                            .setNotification(new Notification(notification.getTitle(), notification.getBody()))

                            // Android Notification Configuration
                            .setAndroidConfig(AndroidConfig.builder()
                                    .setTtl(Duration.ofMinutes(3600).toMillis()) // Time to live - 4 weeks default and
                                    .setPriority(AndroidConfig.Priority.NORMAL)
                                    .setNotification(AndroidNotification.builder()
                                            .setIcon("stock")
                                            .setColor("#168070")
                                            .build())
                                    .build())

                            // IOS Notification Configuration
                            .setApnsConfig(ApnsConfig.builder()
                                    .setAps(Aps.builder()
                                            .setBadge(1)
                                            .build())
                                    .build())

                            .setToken(deviceToken.getDeviceToken())
                            .build(); // Message.builder

                    return message;
                }


            }

        }

        return new Object();
    }
    */

    /*
    public List<String> userTokens(NotificationType notificationType, NotificationData notification){

        // Generic Token Values
        List<String> tokens = new ArrayList<>();

        // Notification Type [ALL]
        if (notificationType.getValue().equals("ALL")) {
            ArrayList<DeviceToken> deviceTokens = (ArrayList<DeviceToken>) deviceTokenService.getAll();
            tokens = deviceTokens.stream().map(DeviceToken::getDeviceToken).filter(Objects::nonNull).collect(Collectors.toList());
        }

        // Notification Type [GROUP]
        if(notificationType.getValue().equals("GROUP")){
            List<Long> chatUsers;
            ChatRoom chatRoom = chatRoomService.getChatRoomById(notification.getData().getChatRoomId()); // Request
            chatUsers = chatRoom.getUsers().stream().map(AbstractEntity::getId).filter(Objects::nonNull).collect(Collectors.toList());
            socketService.getOnlineUsers().stream().filter(onlineUser -> onlineUser.getPlatform().equals("mobile")).forEach(onlineUser ->
                    chatUsers.remove(Long.parseLong(onlineUser.getUserid()))
            );

            // all tokens in the chat user list
            ArrayList<DeviceToken> deviceTokens = (ArrayList<DeviceToken>) deviceTokenService.getAllByUserIds(chatUsers);
            tokens = deviceTokens.stream().map(DeviceToken::getDeviceToken).filter(Objects::nonNull).collect(Collectors.toList());
        }

        if(tokens.size() == 0){ tokens.add("empty-token"); } // If there is no token to avoid error
        return tokens;

    }
    */

}


