package pl.csl.library.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.csl.library.security.UserPrincipal;

import java.io.IOException;
import java.time.Duration;

public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Limitujemy tylko wyszukiwarkę
        String path = request.getRequestURI();
        return !path.startsWith("/api/books/search");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Bucket bucket = resolveBucket(request);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        // Dodatkowe nagłówki "ładne" do debugu/monitoringu
        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));

        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
            return;
        }

        long waitNanos = probe.getNanosToWaitForRefill();
        long retryAfterSeconds = Math.max(1, Duration.ofNanos(waitNanos).toSeconds());

        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"Rate limit exceeded. Try again later.\"}");
    }

    private Bucket resolveBucket(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserPrincipal principal) {
            return rateLimitService.resolveBucketForAuthenticatedUser(principal.getId());
        }

        String ip = clientIp(request);
        return rateLimitService.resolveBucketForAnonymousIp(ip);
    }

    private String clientIp(HttpServletRequest request) {
        // Jeśli kiedyś postawisz reverse proxy (nginx), to będzie potrzebne
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}