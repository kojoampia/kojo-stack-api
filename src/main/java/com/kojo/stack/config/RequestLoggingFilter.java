package com.kojo.stack.config;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * RequestLoggingFilter - Logs all incoming HTTP requests with distributed tracing
 * Creates a span for each request and records timing information
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final Tracer tracer;
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String traceId = UUID.randomUUID().toString();
        String method = request.getMethod();
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = queryString != null ? path + "?" + queryString : path;

        // Create a span for this request
        Span span = tracer.spanBuilder(method + " " + path)
                .setAttribute("http.method", method)
                .setAttribute("http.url", fullPath)
                .setAttribute("trace.id", traceId)
                .setAttribute("http.client_ip", request.getRemoteAddr())
                .startSpan();

        long startTime = System.currentTimeMillis();

        try (var scope = span.makeCurrent()) {
            // Log incoming request
            log.info(">>> Incoming Request: [{}] {} {} | Trace-ID: {} | Query: {}",
                    method, path, response.getStatus(), traceId, queryString);

            // Add trace ID to response header
            response.addHeader(TRACE_ID_HEADER, traceId);

            // Continue filter chain
            filterChain.doFilter(request, response);

            // Log response with timing
            long duration = System.currentTimeMillis() - startTime;
            log.info("<<< Response: [{}] {} | Status: {} | Duration: {}ms | Trace-ID: {}",
                    method, path, response.getStatus(), duration, traceId);

            // Add attributes to span
            span.setAttribute("http.status_code", response.getStatus());
            span.setAttribute("http.duration_ms", duration);

        } finally {
            span.end();
        }
    }
}
