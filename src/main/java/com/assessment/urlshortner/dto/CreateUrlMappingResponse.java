package com.assessment.urlshortner.dto;

public record CreateUrlMappingResponse (
        String code,
        String shortUrl
) {}
