package com.example.ws.microservices.firstmicroservices.oldstructure.repository.etc;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.TrainingDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingDocumentsRepository extends JpaRepository<TrainingDocument, Long> {
}
