package com.example.ws.microservices.firstmicroservices.service.etc;

import com.example.ws.microservices.firstmicroservices.entity.etc.DocumentsDepartment;

import java.util.List;
import java.util.Map;

public interface DocumentsDepartmentService {

    Map<Long, List<String>> getMapDocumentsDepartment();
    void saveAll(List<DocumentsDepartment> documentsDepartments);
    void deleteByDocumentIdAndDepartmentIds(Long documentId, List<Long> idsToRemove);
}
