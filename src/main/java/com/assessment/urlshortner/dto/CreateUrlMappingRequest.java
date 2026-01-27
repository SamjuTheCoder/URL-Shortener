package com.assessment.urlshortner.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUrlMappingRequest (
        @NotBlank
        @URL
        @Size(max = 2048)
        String longUrl
) {}
