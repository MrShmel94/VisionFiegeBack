package com.example.ws.microservices.firstmicroservices.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.SpiCluster;
import com.example.ws.microservices.firstmicroservices.repository.performance_gd.SpiClusterRepository;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.SpiClusterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SPIClusterServiceImpl implements SpiClusterService {

    private final SpiClusterRepository spiClusterRepository;

    @Override
    public List<SpiCluster> getAllSpiClusters() {
        return spiClusterRepository.findAll();
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
