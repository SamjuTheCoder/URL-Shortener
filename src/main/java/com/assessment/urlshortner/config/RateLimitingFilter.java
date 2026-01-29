package com.assessment.urlshortner.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // One bucket per IP (in-memory)
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Value("${rate.limit.tokens}")
    private int maxTokens;

    @Value("${rate.limit.refill-minutes}")
    private int refillMinutes;

    @Value("${rate.limit.http-method}")
    private String httpMethod;

    @Value("${rate.limit.path}")
    private String path;

    private Bucket newBucket() {
        Refill refill = Refill.greedy(maxTokens, Duration.ofMinutes(refillMinutes));
        Bandwidth limit = Bandwidth.classic(maxTokens, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Apply only to configured HTTP method and path
        if (!httpMethod.equalsIgnoreCase(request.getMethod())
                || !request.getRequestURI().startsWith(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, k -> newBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("Too many requests. Please try again later.");
        }
    }
}
