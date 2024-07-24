package com.thentrees.shopapp.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.thentrees.shopapp.components.JwtTokenUtils;
import com.thentrees.shopapp.models.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter { // filter theo tung request

    @Value("${api.prefix}")
    private String apiPrefix;

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException, NullPointerException {
        // lọc request
        // lấy token từ header
        // kiểm tra token
        // nếu token hợp lệ thì cho phép request đi tiếp<==> trả về lỗi
        try {
            if (isByPassTokens(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            final String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && !authorizationHeader.startsWith("Bearer ")) {
                logger.error("Authorization header is not started with Bearer");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "authHeader null or not started with Bearer");
            }
            // kiểm tra token
            // nếu token hợp lệ thì cho phép request đi tiếp<==> trả về lỗi
            // nếu token không hợp lệ thì trả về lỗi
            final String token = authorizationHeader.substring(7);
            final String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }

    private boolean isByPassTokens(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> byPassTokens = Arrays.asList(
                // Healthcheck request, no JWT token required
                Pair.of(String.format("%s/healthcheck/health", apiPrefix), "GET"),
                Pair.of(String.format("%s/send-sms/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/payments/vn-pay-callback**", apiPrefix), "GET"),
                Pair.of(String.format("%s/actuator/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/roles**", apiPrefix), "GET"),
                Pair.of(String.format("%s/comments**", apiPrefix), "GET"),
                Pair.of(String.format("%s/coupons**", apiPrefix), "GET"),
                Pair.of(String.format("%s/products**", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories**", apiPrefix), "GET"),
                Pair.of(String.format("%s/orders**", apiPrefix), "GET"),
                Pair.of(String.format("%s/order-details**", apiPrefix), "GET"),
                Pair.of(String.format("%s/authentications/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/authentications/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/authentications/login-google", apiPrefix), "GET"),
                Pair.of(String.format("%s/authentications/refresh-token", apiPrefix), "POST"),
                Pair.of("/api-docs", "GET"),
                Pair.of("/api-docs/**", "GET"),
                Pair.of("/swagger-resources", "GET"),
                Pair.of("/swagger-resources/**", "GET"),
                Pair.of("/configuration/ui", "GET"),
                Pair.of("/configuration/security", "GET"),
                Pair.of("/swagger-ui/**", "GET"),
                Pair.of("/swagger-ui.html", "GET"),
                Pair.of("/swagger-ui/index.html", "GET"));

        // Swagger

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        for (Pair<String, String> token : byPassTokens) {
            String path = token.getFirst();
            String method = token.getSecond();
            // Check if the request path and method match any pair in the bypassTokens list
            if (requestPath.matches(path.replace("**", ".*")) && requestMethod.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
}
