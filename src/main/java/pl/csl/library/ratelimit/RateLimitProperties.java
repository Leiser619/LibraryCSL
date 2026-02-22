package pl.csl.library.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ratelimit")
public record RateLimitProperties(
        int authPerMinute,
        int anonPerMinute
) {}