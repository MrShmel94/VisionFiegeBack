package com.example.ws.microservices.firstmicroservices.entity.etc;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "documents", schema = "etc")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 128)
    @NotNull
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_document")
    private TypeDocument typeDocument;

    @Column(name = "url", length = Integer.MAX_VALUE)
    private String url;

    @NotNull
    @Column(name = "version", nullable = false)
    private Integer version;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "date_last_upd", nullable = false)
    private LocalDate dateLastUpd;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "date_start", nullable = false)
    private LocalDate dateStart;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "date_finish", nullable = false)
    private LocalDate dateFinish;

    @Size(max = 512)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 512)
    private String userId;

}