package com.example.demo.config;

import com.example.demo.service.AdminService;
import com.example.demo.service.ManagerService;
import com.example.demo.service.StaffOperatorService;
import com.example.demo.service.TechnicianService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private AdminService adminService;
    @Autowired private TechnicianService technicianService;
    @Autowired private StaffOperatorService staffOperatorService;
    @Autowired private ManagerService managerService;

    // ── Routes that should bypass JWT filtering entirely ──────────────────────
    private static final List<String> PUBLIC_PATHS = List.of(
            "/",
            "/index",
            "/index.html",
            "/login",
            "/login.html",
            "/manager-login",
            "/manager-login.html",
            "/tech-login",
            "/tech-login.html",
            "/staff-login",
            "/staff-login.html",
            "/register",
            "/manager-register",
            "/tech-register",
            "/staff-register",
            "/api/admin/login",
            "/api/admin/register",
            "/api/technician/login",
            "/api/technician/register",
            "/api/staff/login",
            "/api/staff/register",
            "/api/manager/login",
            "/api/manager/register",
            "/password-reset",
            "/password-reset.html",
            "/api/password-reset/send-code",
            "/api/password-reset",
            "/style.css"
    );

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/auth/",
            "/api/password-reset/",
            "/css/",
            "/js/",
            "/images/",
            "/static/",
            "/webjars/"
    );

    /**
     * Skip JWT processing entirely for public pages and static assets.
     * This prevents filter exceptions from blocking the home page.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        // Exact match
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }

        // Prefix match (static assets, api/auth, etc.)
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                String rawSubject = jwtUtil.getUsernameFromToken(token);

                UserDetails userDetails;

                if (rawSubject.startsWith("TECH:")) {
                    userDetails = technicianService.loadUserByUsername(rawSubject.substring(5));
                } else if (rawSubject.startsWith("STAFF:")) {
                    userDetails = staffOperatorService.loadUserByUsername(rawSubject.substring(6));
                } else if (rawSubject.startsWith("MANAGER:")) {
                    userDetails = managerService.loadUserByUsername(rawSubject.substring(8));
                } else {
                    // Plain username → Admin
                    userDetails = adminService.loadUserByUsername(rawSubject);
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                rawSubject, null, userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}