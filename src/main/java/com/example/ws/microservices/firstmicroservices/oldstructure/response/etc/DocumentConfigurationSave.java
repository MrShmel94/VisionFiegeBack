package com.example.ws.microservices.firstmicroservices.oldstructure.response.etc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DocumentConfigurationSave {

    List<String> typeDocuments;
    List<String> departments;
    List<String> positions;
}
