package com.example.ws.microservices.firstmicroservices.oldstructure.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record SiteRequestModel (
        @NotNull String nameSite,
        List<String> nameDepartment,
        List<String> nameShift,
        List<String> nameTeam,
        List<String> namePosition,
        List<String> nameAgency,
        List<String> nameCountry
) {

    /**
     * Canonical constructor that ensures the mandatory field {@code nameSite} is not null and
     * that all list fields are never null. If any list is null, it is replaced by an empty list.
     *
     * @param nameSite the name of the site (must not be null)
     * @param nameDepartment list of departments; if null, it defaults to an empty list
     * @param nameShift list of shifts; if null, it defaults to an empty list
     * @param nameTeam list of teams; if null, it defaults to an empty list
     * @param namePosition list of positions; if null, it defaults to an empty list
     * @param nameAgency list of agencies; if null, it defaults to an empty list
     * @param nameCountry list of countries; if null, it defaults to an empty list
     */
    @JsonCreator
    public SiteRequestModel(
            @JsonProperty("nameSite") String nameSite,
            @JsonProperty("nameDepartment") List<String> nameDepartment,
            @JsonProperty("nameShift") List<String> nameShift,
            @JsonProperty("nameTeam") List<String> nameTeam,
            @JsonProperty("namePosition") List<String> namePosition,
            @JsonProperty("nameAgency") List<String> nameAgency,
            @JsonProperty("nameCountry") List<String> nameCountry) {
        this.nameSite = Objects.requireNonNull(nameSite, "nameSite must not be null");
        this.nameDepartment = (nameDepartment == null) ? Collections.emptyList() : List.copyOf(nameDepartment);
        this.nameShift = (nameShift == null) ? Collections.emptyList() : List.copyOf(nameShift);
        this.nameTeam = (nameTeam == null) ? Collections.emptyList() : List.copyOf(nameTeam);
        this.namePosition = (namePosition == null) ? Collections.emptyList() : List.copyOf(namePosition);
        this.nameAgency = (nameAgency == null) ? Collections.emptyList() : List.copyOf(nameAgency);
        this.nameCountry = (nameCountry == null) ? Collections.emptyList() : List.copyOf(nameCountry);
    }
}
