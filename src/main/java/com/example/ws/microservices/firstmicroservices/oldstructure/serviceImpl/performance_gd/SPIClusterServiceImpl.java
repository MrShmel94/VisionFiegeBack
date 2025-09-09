package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.SpiCluster;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd.SpiClusterRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd.SpiClusterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SPIClusterServiceImpl implements SpiClusterService {

    private final SpiClusterRepository spiClusterRepository;

    @Override
    public List<SpiCluster> getAllSpiClusters() {
        return spiClusterRepository.findAllWithPerformances();
    }

    @Override
    public SpiCluster saveSpiCluster(SpiCluster spiCluster) {
        return spiClusterRepository.save(spiCluster);
    }

    @Override
    public void saveAll(List<SpiCluster> spiClusters) {
        spiClusterRepository.saveAll(spiClusters);
    }

}
