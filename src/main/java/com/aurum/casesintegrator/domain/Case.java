package com.aurum.casesintegrator.domain;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.cloud.gcp.data.firestore.Document;

import com.google.cloud.firestore.annotation.DocumentId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collectionName = "cases")
public class Case {

    @DocumentId
    private String id;

    @Size(max = 40, message = "Property \"folder\" must not exceed max length 40.")
    private String folder;

    @NotBlank(message = "Property \"customer\" must not be blank.")
    private String customer;

    @NotBlank(message = "Property \"title\" must not be blank.")
    private String title;

    private List<String> labels;

    private String description;

    private String notes;

    @NotBlank(message = "Property \"inChargeOf\" must not be blank.")
    private String inChargeOf;
    private AccessType accessType;

    private Long createdAtInstant;

}
