package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.FinalCluster;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd.FinalClusterRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd.FinalClusterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FinalClusterServiceImpl implements FinalClusterService {

    private final FinalClusterRepository finalClusterRepository;

    @Override
    public List<FinalCluster> getAllFinalClusters() {
        return finalClusterRepository.findAllWithPerformances();
    }

    @Override
    public FinalCluster saveFinalCluster(FinalCluster finalCluster) {
        return finalClusterRepository.save(finalCluster);
    }

    @Override
    public void saveAll(List<FinalCluster> finalClusters) {
        finalClusterRepository.saveAll(finalClusters);
    }
}
