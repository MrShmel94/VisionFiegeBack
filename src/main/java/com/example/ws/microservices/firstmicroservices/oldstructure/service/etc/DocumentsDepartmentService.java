package com.example.ws.microservices.firstmicroservices.oldstructure.service.etc;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.DocumentsDepartment;

import java.util.List;
import java.util.Map;

public interface DocumentsDepartmentService {

    Map<Long, List<String>> getMapDocumentsDepartment();
    void saveAll(List<DocumentsDepartment> documentsDepartments);
    void deleteByDocumentIdAndDepartmentIds(Long documentId, List<Long> idsToRemove);
}
