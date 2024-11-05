package com.example.ws.microservices.firstmicroservices.repository.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.SpiCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpiClusterRepository extends JpaRepository<SpiCluster, Long> {

    @Query("SELECT s FROM SpiCluster s")
    List<SpiCluster> findAllWithPerformances();
}
