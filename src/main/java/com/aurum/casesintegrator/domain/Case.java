package com.aurum.casesintegrator.domain;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "cases")
public class Case {

    @Id
    private Long id;

    @Size(max = 40, message = "Property \"folder\" must not exceed max length 40.")
    private String folder;

    @NotBlank(message = "Property \"customer\" must not be blank.")
    private String customer;

    @NotBlank(message = "Property \"title\" must not be blank.")
    private String title;

    private Set<String> labels;
    private String description;
    private String notes;

    @NotBlank(message = "Property \"inChargeOf\" must not be blank.")
    private String inChargeOf;
    private AccessType accessType;

    @NotNull(message = "Property \"createdAt\" must not be null.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

}
