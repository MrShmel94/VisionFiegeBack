package com.example.ws.microservices.firstmicroservices.entity.etc;

import com.example.ws.microservices.firstmicroservices.entity.vision.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "training_documents", schema = "etc")
public class TrainingDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "date_training", nullable = false)
    private LocalDate dateTraining;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Employee mentor;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "version_training", nullable = false)
    private Integer versionTraining;

    @NotNull
    @Column(name = "user_training_id", nullable = false)
    private String userId;

}