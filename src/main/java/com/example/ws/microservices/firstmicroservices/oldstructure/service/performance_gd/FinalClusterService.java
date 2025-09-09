package com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.FinalCluster;

import java.util.List;

public interface FinalClusterService {

    List<FinalCluster> getAllFinalClusters();
    FinalCluster saveFinalCluster(FinalCluster finalCluster);
    void saveAll(List<FinalCluster> finalClusters);
}
