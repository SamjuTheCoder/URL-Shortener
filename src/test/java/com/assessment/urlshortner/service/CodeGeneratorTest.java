package com.assessment.urlshortner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assessment.urlshortner.utils.UrlCodeGenerator;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CodeGeneratorTest {
    
    private UrlCodeGenerator codeGenerator;
    
    @Mock
    private Set<String> existingCodes;
    
    @BeforeEach
    void setUp() {
        codeGenerator = new UrlCodeGenerator();
    }
    
    @Test
    void testGenerateCode_Length() {
        String code = codeGenerator.generateCode();
        assertEquals(6, code.length());
    }
    
    @Test
    void testGenerateCode_Base62Characters() {
        String code = codeGenerator.generateCode();
        assertTrue(code.matches("^[A-Za-z0-9]+$"));
    }
    
    @Test
    void testGenerateCode_CustomLength() {
        String code = codeGenerator.generateCode(8);
        assertEquals(8, code.length());
    }
    
    @Test
    void testIsValidCode_Valid() {
        assertTrue(codeGenerator.isValidCode("samju1234"));
        assertTrue(codeGenerator.isValidCode("samju1234"));
        assertTrue(codeGenerator.isValidCode("123456"));
    }
    
    @Test
    void testIsValidCode_Invalid() {
        assertFalse(codeGenerator.isValidCode("ab")); // Too short
        assertFalse(codeGenerator.isValidCode("abc-123")); // Invalid character
        assertFalse(codeGenerator.isValidCode(null));
    }
    
    @Test
    void testGenerateUniqueCode() {
        Set<String> existing = new HashSet<>();
        existing.add("samju1234");
        
        String code = codeGenerator.generateUniqueCode(6, existing);
        assertNotNull(code);
        assertEquals(6, code.length());
        assertNotEquals("samju1234", code);
    }
}