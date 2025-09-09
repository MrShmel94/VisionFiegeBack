package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.ActivityName;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd.ActivityNameRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd.ActivityNameService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ActivityNameServiceImpl implements ActivityNameService {

    private ActivityNameRepository activityNameRepository;

    public List<ActivityName> getAllActivityNames() {
        return activityNameRepository.findAllWithSpiClustersAndPerformances();
    }

    @Override
    public ActivityName saveActivityName(ActivityName activityName) {
        return activityNameRepository.save(activityName);
    }

    @Override
    public void saveAll(List<ActivityName> activityNames) {
        activityNameRepository.saveAll(activityNames);
    }
}
