package com.mts.aadati.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import ua_parser.Client;
import ua_parser.Parser;

import java.io.IOException;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private static final String UNKNOWN = "Unknown";


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        String uri = request.getRequestURI();
        String clientIP = request.getHeader("X-Forwarded-For");
        if (clientIP == null || clientIP.isBlank()) {
            clientIP = request.getRemoteAddr();
        }
        String servletPath = request.getServletPath() ;
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String query = request.getQueryString() == null ? "" : request.getQueryString();
        String protocol = request.getProtocol() ;
        String correlationIdHeader = request.getHeader("X-Correlation-Id");
        String correlationId = (correlationIdHeader == null || correlationIdHeader.isBlank())
                ? UUID.randomUUID().toString()
                : correlationIdHeader;


        String os = UNKNOWN;
        String browser = UNKNOWN;
        String device = UNKNOWN;

        try{
        Parser parser = new Parser();
        Client client = parser.parse(userAgent);
        os = client.os.family == null ? UNKNOWN : client.os.family ;
        browser  = client.userAgent.family == null ? UNKNOWN : client.userAgent.family;
        device = client.device.family == null ? UNKNOWN : client.device.family;
        }catch (Exception e) {
            logger.error("Failed to parse User-Agent: {}", userAgent, e);
        }
        String timeRequest = Instant.now().toString();

        String json = String.format("""
            {
              "status": 401,
              "error": "unauthorized",
              "servlet-path": "%s",
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
                servletPath,authException.getMessage(), uri, query, method, clientIP, userAgent,
                os, browser, device, correlationId, protocol, timeRequest);

        logger.warn("Unauthorized access detected: {}", json);
        logger.debug("commence  {}",json);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
