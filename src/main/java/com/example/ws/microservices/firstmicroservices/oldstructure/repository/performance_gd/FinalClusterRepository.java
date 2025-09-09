package com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.FinalCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FinalClusterRepository extends JpaRepository<FinalCluster, Long> {

    @Query("SELECT f FROM FinalCluster f")
    List<FinalCluster> findAllWithPerformances();
}
