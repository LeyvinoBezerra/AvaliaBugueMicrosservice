package br.edu.ufersa.pw.todo.buggytrip.infrastructure.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class HttpRequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final Pattern VALID_CORRELATION_ID = Pattern.compile("[A-Za-z0-9._-]{1,64}");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || !VALID_CORRELATION_ID.matcher(correlationId).matches()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        long startedAt = System.nanoTime();
        boolean completed = false;

        try {
            chain.doFilter(request, response);
            completed = true;
        } catch (ServletException | IOException | RuntimeException exception) {
            logger.error("HTTP {} {} failed", request.getMethod(), request.getRequestURI(), exception);
            throw exception;
        } finally {
            long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            if (completed) {
                int status = response.getStatus();
                if (status >= 500) {
                    logger.error("HTTP {} {} completed with status={} durationMs={}",
                            request.getMethod(), request.getRequestURI(), status, durationMs);
                } else if (status >= 400) {
                    logger.warn("HTTP {} {} completed with status={} durationMs={}",
                            request.getMethod(), request.getRequestURI(), status, durationMs);
                } else {
                    logger.info("HTTP {} {} completed with status={} durationMs={}",
                            request.getMethod(), request.getRequestURI(), status, durationMs);
                }
            }
            MDC.remove("correlationId");
        }
    }
}
