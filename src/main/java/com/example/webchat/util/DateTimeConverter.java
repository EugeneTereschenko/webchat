package com.example.webchat.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;



@Slf4j
public class DateTimeConverter {
    public static LocalDateTime toLocalDateTime(Date date) {
        log.debug("Converting Date to LocalDateTime: " + date);
        if (date == null) {
            // Handle null case by returning a default value or throwing an exception
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
