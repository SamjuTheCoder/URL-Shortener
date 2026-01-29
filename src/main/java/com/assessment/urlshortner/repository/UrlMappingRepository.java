package com.assessment.urlshortner.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.assessment.urlshortner.model.UrlMapping;

/**
 * Repository interface for managing UrlMapping entities.
 *
 * Uses Spring Data JPA to provide CRUD operations and
 * custom query methods derived from method names.
 */
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    /**
     * Finds a URL mapping by its short code.
     *
     * Used during URL redirect.
     *
     * @param code the generated short code
     * @return Optional containing UrlMapping if found, otherwise empty
     */
    Optional<UrlMapping> findByCode(String code);

    /**
     * Finds a URL mapping by its original long URL.
     *
     * Supports idempotent behavior: if the same long URL
     * is submitted multiple times, the existing mapping
     * can be returned instead of creating a new one.
     *
     * @param longUrl the original URL
     * @return Optional containing UrlMapping if found, otherwise empty
     */
    Optional<UrlMapping> findByLongUrl(String longUrl);

    /**
     * Checks whether a short code already exists.
     *
     * Used during code generation to avoid collisions
     * before persisting a new UrlMapping.
     *
     * @param code the generated short code
     * @return true if the code already exists, false otherwise
     */
    boolean existsByCode(String code);

    boolean existsByLongUrl(String longUrl);
    
    @Query("SELECT COUNT(u) FROM UrlMapping u WHERE u.expiresAt < :now")
    long countExpiredUrls(@Param("now") Instant now);
    
    @Modifying
    @Query("DELETE FROM UrlMapping u WHERE u.expiresAt < :now")
    void deleteExpiredUrls(@Param("now") Instant now);
}

