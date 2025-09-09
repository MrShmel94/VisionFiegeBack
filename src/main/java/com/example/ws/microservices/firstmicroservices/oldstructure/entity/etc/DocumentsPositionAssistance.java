package com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Position;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "documents_position_assistance", schema = "etc")
public class DocumentsPositionAssistance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_document")
    private Document idDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_position")
    private Position idPosition;

    @Size(max = 512)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 512)
    private String userId;

}