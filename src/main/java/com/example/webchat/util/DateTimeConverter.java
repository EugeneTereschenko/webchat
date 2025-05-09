package com.example.webchat.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeConverter {
    public static LocalDateTime toLocalDateTime(Date date) {
        System.out.println("Converting Date to LocalDateTime: " + date);
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
