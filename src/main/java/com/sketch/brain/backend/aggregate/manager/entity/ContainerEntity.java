package com.sketch.brain.backend.aggregate.manager.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "container")
public class ContainerEntity {
    /**
     * Container 관련 정보를 저장할 Entity.
     * RDBMS에 저장.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "BINARY(12)")
    private byte[] experiment_id;
    private String user_id;
    private String data_name;
    private String model_name;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private LocalDateTime created_at;

    @NotNull
    private String X_TOKEN;
    @NotNull
    private String TOKEN;
}
