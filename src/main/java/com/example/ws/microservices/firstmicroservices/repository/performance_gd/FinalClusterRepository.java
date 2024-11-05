package com.example.ws.microservices.firstmicroservices.repository.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.FinalCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FinalClusterRepository extends JpaRepository<FinalCluster, Long> {

    @Query("SELECT f FROM FinalCluster f")
    List<FinalCluster> findAllWithPerformances();
}
