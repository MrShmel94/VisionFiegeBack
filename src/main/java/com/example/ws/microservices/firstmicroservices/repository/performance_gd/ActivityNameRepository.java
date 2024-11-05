package com.example.ws.microservices.firstmicroservices.repository.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.ActivityName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityNameRepository extends JpaRepository<ActivityName, Long> {

    @Query("SELECT a FROM ActivityName a LEFT JOIN FETCH a.spiCluster")
    List<ActivityName> findAllWithSpiClustersAndPerformances();
}
