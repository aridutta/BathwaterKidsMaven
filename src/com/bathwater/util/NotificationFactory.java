/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bathwater.dynamodb.tables.Notification;

/**
 *
 * @author rajeshk
 */
public class NotificationFactory {

    private static final String WELCOME = "1";

    private static final String CREDITS_AWARDED = "2";

    private static final String PICK_UP_SCHEDULED = "3";

    private static final String PICK_UP_CANCELLED = "4";

    private static final String PICK_UP = "5";

    private static final String DELIVERY = "6";

    private static final String DELIVERY_SCHEDULED = "7";

    private static final String DELIVERY_CANCELLED = "8";

    private static final String GEAR_ARRIVED = "9";

    private static final String PICKUP_AND_DELIVERY = "11";

    public static Notification addWelcomeNotification(List<Notification> notifications) {
        Notification notification = initialize(notifications);
        List<Map<String, String>> params = notification.getParams();
        Map<String, String> map = new HashMap<>();
        map.put("id", WELCOME);
        params.add(map);
        return notification;
    }

    public static Notification addCreditsAwardedNotification(List<Notification> notifications, String creditsAwarded) {
        Notification notification = initialize(notifications);
        List<Map<String, String>> params = notification.getParams();
        Map<String, String> map = new HashMap<>();
        map.put("id", CREDITS_AWARDED);
        map.put("credits", creditsAwarded);
        params.add(map);
        return notification;
    }

    public static Notification addPickupScheduledNotification(List<Notification> notifications, String day, String time, String userRequestID) {
        Notification notification = initialize(notifications);
        List<Map<String, String>> params = notification.getParams();
        Map<String, String> map = new HashMap<>();
        map.put("id", PICK_UP_SCHEDULED);
        map.put("day", day);
        map.put("time", time);
        map.put("userRequestID", userRequestID);
        params.add(map);

        return notification;
    }

    public static Notification addPickupCanceledNotification(List<Notification> notifications, String day, String time) {
        Notification notification = initialize(notifications);
        List<Map<String, String>> params = notification.getParams();
        Map<String, String> map = new HashMap<>();
        map.put("id", PICK_UP_CANCELLED);
        map.put("day", day);
        map.put("time", time);
        params.add(map);

        return notification;
    }

    public static Notification addPickupNotification(List<Notification> notifications) {
        Notification notification = initialize(notifications);
        List<Map<String, String>> params = notification.getParams();
        Map<String, String> map = new HashMap<>();
        map.put("id", PICK_UP);
        params.add(map);

        return notification;
    }

    public static Notification addDeliveryNotification(List<Notification> notifications) {
        Notification notification = initialize(notifications);
        List<Map<String, String>> params = notification.getParams();
        Map<String, String> map = new HashMap<>();
        map.put("id", DELIVERY);
        params.add(map);

        return notification;
    }

    public static Notification addDeliveryScheduledNotification(List<Notification> notifications, String day, String time, String userRequestID) {
        Notification notification = initialize(notifications);
        List<Map<String, String>> params = notification.getParams();
        Map<String, String> map = new HashMap<>();
        map.put("id", DELIVERY_SCHEDULED);
        map.put("day", day);
        map.put("time", time);
        map.put("userRequestID", userRequestID);
        params.add(map);

        return notification;
    }

    public static Notification addDeliveryCancelledNotification(List<Notification> notifications, String day, String time) {
        Notification notification = initialize(notifications);
        List<Map<String, String>> params = notification.getParams();
        Map<String, String> map = new HashMap<>();
        map.put("id", DELIVERY_CANCELLED);
        map.put("day", day);
        map.put("time", time);
        params.add(map);

        return notification;
    }

    public static Notification addGearArrivedNotification(List<Notification> notifications) {
        Notification notification = initialize(notifications);
        List<Map<String, String>> params = notification.getParams();
        Map<String, String> map = new HashMap<>();
        map.put("id", GEAR_ARRIVED);
        params.add(map);

        return notification;
    }

    public static Notification addPickupAndDeliveryNotification(List<Notification> notifications, String day, String time, String pickupRequestID, String dropOffRequestID) {
        Notification notification = initialize(notifications);
        List<Map<String, String>> params = notification.getParams();
        Map<String, String> map = new HashMap<>();
        map.put("id", PICKUP_AND_DELIVERY);
        map.put("day", day);
        map.put("time", time);
        if (!StringUtil.isBlank(pickupRequestID)) {
            map.put("pickupRequestID", pickupRequestID);
        }
        if (!StringUtil.isBlank(dropOffRequestID)) {
            map.put("dropOffRequestID", dropOffRequestID);
        }
        params.add(map);

        return notification;
    }

    private static Notification initialize(List<Notification> notifications) {
        Notification notification = getTodaysNotification(notifications);
        if (notification == null) {
            notification = new Notification();
            String today = new SimpleDateFormat("MM.dd.yyyy").format(new Date());
            notification.setDate(today);
            notifications.add(notification);
        }

        if (notification.getParams() == null) {
            List<Map<String, String>> params = new ArrayList<>();
            notification.setParams(params);
        }

        return notification;
    }

    private static Notification getTodaysNotification(List<Notification> notifications) {
        String today = new SimpleDateFormat("MM.dd.yyyy").format(new Date());

        if (notifications != null) {
            for (Notification notification : notifications) {
                if (today.equals(notification.getDate())) {
                    return notification;
                }
            }
        }

        return null;
    }

}
