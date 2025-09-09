package com.example.ws.microservices.firstmicroservices.oldstructure.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    private List<T> data;
    private List<String> missingItems;
    private long totalItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
