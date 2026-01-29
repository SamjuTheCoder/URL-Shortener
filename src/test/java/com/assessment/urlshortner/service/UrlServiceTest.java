package com.assessment.urlshortner.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assessment.urlshortner.dto.UrlMappingRequest;
import com.assessment.urlshortner.dto.UrlMappingResponse;
import com.assessment.urlshortner.exception.UrlNotFoundException;
import com.assessment.urlshortner.model.UrlMapping;
import com.assessment.urlshortner.repository.UrlMappingRepository;
import com.assessment.urlshortner.utils.UrlCodeGenerator;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    
    @Mock
    private UrlMappingRepository repository;
    
    @Mock
    private UrlCodeGenerator codeGenerator;
    
    private UrlMappingService urlService;
    private MeterRegistry meterRegistry;
    
    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        urlService = new UrlMappingService(repository, codeGenerator, meterRegistry);
        
        // Use reflection to set private fields
        try {
            var field = UrlMappingService.class.getDeclaredField("baseUrl");
            field.setAccessible(true);
            field.set(urlService, "http://localhost:8080");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    void testCreateShortUrl_Success() {
        // Arrange
        UrlMappingRequest request = new UrlMappingRequest();
        request.setLongUrl("https://www.geeksforgeeks.org/advance-java/rate-limiting-a-spring-api-using-bucket4j");
        
        when(repository.findByLongUrl(any())).thenReturn(Optional.empty());
        when(codeGenerator.generateCode(anyInt())).thenReturn("samju1234");
        when(repository.save(any(UrlMapping.class))).thenAnswer(invocation -> {
            UrlMapping mapping = invocation.getArgument(0);
            mapping.setId(1L);
            mapping.setCreatedAt(Instant.now());
            return mapping;
        });
        
        // Act
        UrlMappingResponse response = urlService.createShortUrl(request);
        
        // Assert
        assertNotNull(response);
        assertEquals("samju1234", response.getCode());
        assertEquals("http://localhost:8080/r/samju1234", response.getShortUrl());
        
        verify(repository, times(1)).save(any(UrlMapping.class));
    }
    
    @Test
    void testCreateShortUrl_Idempotent() {
        // Arrange
        UrlMappingRequest request = new UrlMappingRequest();
        request.setLongUrl("https://www.geeksforgeeks.org/advance-java/rate-limiting-a-spring-api-using-bucket4j");
        
        UrlMapping existing = new UrlMapping("samju1234", "https://www.geeksforgeeks.org/advance-java/rate-limiting-a-spring-api-using-bucket4j", null);
        existing.setId(1L);
        
        when(repository.findByLongUrl(any())).thenReturn(Optional.of(existing));
        
        // Act
        UrlMappingResponse response = urlService.createShortUrl(request);
        
        // Assert
        assertNotNull(response);
        assertEquals("samju1234", response.getCode());
        verify(repository, times(0)).save(any(UrlMapping.class));
    }
    
    @Test
    void testGetLongUrl_Success() {
        // Arrange
        UrlMapping mapping = new UrlMapping("samju1234", "https://www.geeksforgeeks.org/advance-java/rate-limiting-a-spring-api-using-bucket4j", null);
        mapping.setId(1L);
        
        when(repository.findByCode("samju1234")).thenReturn(Optional.of(mapping));
        when(repository.save(any(UrlMapping.class))).thenReturn(mapping);
        
        // Act
        String longUrl = urlService.getLongUrl("samju1234");
        
        // Assert
        assertEquals("https://www.geeksforgeeks.org/advance-java/rate-limiting-a-spring-api-using-bucket4j", longUrl);
        verify(repository, times(1)).save(mapping);
    }
    
    @Test
    void testGetLongUrl_NotFound() {
        // Arrange
        when(repository.findByCode("invalid")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(UrlNotFoundException.class, () -> {
            urlService.getLongUrl("invalid");
        });
    }
}