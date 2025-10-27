package com.example.cleanus_mockapi.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class ApiKeyFilter implements Filter {

    @Value("${mockapi.api-key}")
    private String apiKey;

    private static final Set<String> PROTECTED_PATHS = Set.of("/accounts", "/transactions");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse w = (HttpServletResponse) res;

        if (PROTECTED_PATHS.contains(r.getServletPath())) {
            String key = r.getHeader("X-Api-Key");
            if (key == null || !key.equals(apiKey)) {
                w.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                w.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }

}
