package com.aurum.casesintegrator.domain;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceCreatedResponse {

    private Long id;
    private String uri;
    private HttpStatus status;
    private LocalDateTime createdAt;

}
