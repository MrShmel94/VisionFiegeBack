package com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.SpiCluster;

import java.util.List;

public interface SpiClusterService {

    List<SpiCluster> getAllSpiClusters();
    SpiCluster saveSpiCluster(SpiCluster spiCluster);
    void saveAll(List<SpiCluster> spiClusters);
}
