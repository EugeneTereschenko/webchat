package com.example.webchat.service.impl;

import com.example.webchat.model.Activity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface ActivityService {
    public void addActivity(String activityName, Long userId, Date activityDate);
    public HashMap<String, String> getActivitiesByUserId(Long userId, int limit);
}
