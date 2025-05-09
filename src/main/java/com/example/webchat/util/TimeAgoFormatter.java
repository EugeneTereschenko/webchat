package com.example.webchat.util;
import java.time.Duration;
import java.time.LocalDateTime;

public class TimeAgoFormatter {
    public static String timeAgo(LocalDateTime pastTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(pastTime, now);
        long days = duration.toDays();
        if (days > 1) {
            return days + " days ago";
        } else if (days == 1) {
            return "1 day ago";
        } else {
            long hours = duration.toHours();
            if (hours > 1) {
                return hours + " hours ago";
            } else if (hours == 1) {
                return "1 hour ago";
            } else {
                long minutes = duration.toMinutes();
                if (minutes > 1) {
                    return minutes + " minutes ago";
                } else if (minutes == 1) {
                    return "1 minute ago";
                } else {
                    return "Just now";
                }
            }
        }
    }
}
