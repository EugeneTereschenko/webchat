package com.example.webchat.service;

import com.example.webchat.model.Activity;
import com.example.webchat.model.User;
import com.example.webchat.repository.ActivityRepository;
import com.example.webchat.service.impl.ActivityService;
import com.example.webchat.util.TimeAgoFormatter;
import com.example.webchat.util.DateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    public void saveActivity(Activity activity) {
        activityRepository.save(activity);
        log.info("Activity saved: " + activity);
    }

    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
        log.info("Activity deleted with id: " + id);
    }

    public void updateActivity(Activity activity) {
        activityRepository.save(activity);
        log.info("Activity updated: " + activity);
    }

    public List<Activity> getActivitiesByUserId(Long userId) {
        return activityRepository.findByUserId(userId);
    }

    public void addActivity(String activityName, Long userId, Date activityDate) {
        Activity activity = new Activity.Builder()
                .userId(userId)
                .activity(activityName)
                .activityDate(activityDate)
                .build();


        activityRepository.save(activity);
        log.info("Activity added: " + activity);
    }

    public HashMap<String, String> getActivitiesByUserId(Long userId, int limit) {
        HashMap<String, String> response = new HashMap<>();
        List<Activity> activities = activityRepository.findByUserId(userId);
        activities
                .stream()
                .limit(limit)
                .forEach(activity -> response.put(
                        activity.getActivity(),
                        TimeAgoFormatter.timeAgo(DateTimeConverter.toLocalDateTime(activity.getTimestamp()))
                ));

        return response;
    }
}
