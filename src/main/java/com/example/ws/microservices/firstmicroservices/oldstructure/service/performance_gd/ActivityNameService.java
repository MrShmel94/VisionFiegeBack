package com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.ActivityName;

import java.util.List;

public interface ActivityNameService {
    List<ActivityName> getAllActivityNames();
    ActivityName saveActivityName(ActivityName activityName);
    void saveAll(List<ActivityName> activityNames);
}
