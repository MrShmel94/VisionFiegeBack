package com.example.ws.microservices.firstmicroservices.request;

public record SiteRequestModel (
        String nameSite, String nameDepartment, String nameShift,
        String nameTeam, String namePosition, String nameAgency, String nameCountry
){
}
