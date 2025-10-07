package com.mts.aadati.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private static final String UNKNOWN = "Unknown" ;
    private static final Logger logger = LoggerFactory.getLogger(JwtAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        String uri = request.getRequestURI();
        String clientIP = request.getHeader("X-Forwarded-For");
        if (clientIP == null || clientIP.isBlank()) {
            clientIP = request.getRemoteAddr();
        }
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String protocol = request.getProtocol();
        String query = request.getQueryString() == null ? "" : request.getQueryString() ;
        String correlationIdHeader = request.getHeader("X-Correlation-Id");
        String correlationId = (correlationIdHeader == null || correlationIdHeader.isBlank())
                ? UUID.randomUUID().toString()
                : correlationIdHeader;


        String device = UNKNOWN;
        String os = UNKNOWN;
        String browser = UNKNOWN;

        try {
            Parser parser = new Parser();
            Client client = parser.parse(userAgent);
            device = client.device.family == null ? UNKNOWN : client.device.family;
            os = client.os.family == null ? UNKNOWN : client.os.family;
            browser = client.userAgent.family == null ?  UNKNOWN: client.userAgent.family;
        } catch (Exception e) {
            logger.error("Failed to parse User-Agent:{}",userAgent,e);
        }

        String timeRequest = Instant.now().toString();



        String json = String.format("""
            {
              "status": 403,
              "error": "forbidden",
              "message": "%s",
              "path": "%s",
              "query": "%s",
              "method": "%s",
              "clientIp": "%s",
              "userAgent": "%s",
              "os": "%s",
              "browser": "%s",
              "device": "%s",
              "correlationId": "%s",
              "protocol": "%s",
              "time": "%s"
            }
            """,
                accessDeniedException.getMessage(), uri, query, method, clientIP, userAgent,
                os, browser, device, correlationId, protocol, timeRequest);


        logger.warn(" Forbidden access detected: {}", json);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);

    }
}
