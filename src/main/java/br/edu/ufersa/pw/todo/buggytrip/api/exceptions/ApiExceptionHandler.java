package br.edu.ufersa.pw.todo.buggytrip.api.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ProblemDetail> nf(NotFoundException e, HttpServletRequest r) {
        return p(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), r);
    }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ProblemDetail> cf(ConflictException e, HttpServletRequest r) {
        return p(HttpStatus.CONFLICT, "CONFLICT", e.getMessage(), r);
    }

    @ExceptionHandler(BusinessRuleException.class)
    ResponseEntity<ProblemDetail> br(BusinessRuleException e, HttpServletRequest r) {
        return p(HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_RULE", e.getMessage(), r);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ProblemDetail> ad(AccessDeniedException e, HttpServletRequest r) {
        return p(HttpStatus.FORBIDDEN, "FORBIDDEN", "Acesso negado", r);
    }

    @ExceptionHandler(DomainException.class)
    ResponseEntity<ProblemDetail> de(DomainException e, HttpServletRequest r) {
        return p(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage(), r);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> va(MethodArgumentNotValidException e, HttpServletRequest r) {
        var d = p(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Um ou mais campos são inválidos", r).getBody();
        d.setProperty("errors", e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(x -> x.getField(), x -> x.getDefaultMessage(), (a, b) -> a)));
        return ResponseEntity.badRequest().body(d);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ProblemDetail> cv(ConstraintViolationException e, HttpServletRequest r) {
        return p(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", e.getMessage(), r);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> all(Exception e, HttpServletRequest r) {
        logger.error("Unhandled API exception method={} path={}", r.getMethod(), r.getRequestURI(), e);
        return p(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Erro interno inesperado", r);
    }

    private ResponseEntity<ProblemDetail> p(HttpStatus s, String c, String d, HttpServletRequest r) {
        if (s.is4xxClientError()) {
            logger.warn("API error status={} code={} method={} path={}",
                    s.value(), c, r.getMethod(), r.getRequestURI());
        }
        var x = ProblemDetail.forStatusAndDetail(s, d);
        x.setTitle(s.getReasonPhrase());
        x.setProperty("code", c);
        x.setProperty("instance", r.getRequestURI());
        String correlationId = MDC.get("correlationId");
        if (correlationId != null) {
            x.setProperty("correlationId", correlationId);
        }
        return ResponseEntity.status(s).body(x);
    }
}
