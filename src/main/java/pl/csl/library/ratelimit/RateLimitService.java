package pl.csl.library.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final RateLimitProperties props;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitService(RateLimitProperties props) {
        this.props = props;
    }

    public Bucket resolveBucketForAuthenticatedUser(Long userId) {
        String key = "u:" + userId;
        return buckets.computeIfAbsent(key, k -> newBucket(props.authPerMinute()));
    }

    public Bucket resolveBucketForAnonymousIp(String ip) {
        String key = "ip:" + ip;
        return buckets.computeIfAbsent(key, k -> newBucket(props.anonPerMinute()));
    }

    private Bucket newBucket(int tokensPerMinute) {
        Bandwidth limit = Bandwidth.classic(
                tokensPerMinute,
                Refill.intervally(tokensPerMinute, Duration.ofMinutes(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }
}