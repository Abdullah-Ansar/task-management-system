
package com.example.taskmanager.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class LoginRateLimiter {

    private final Cache<String, Bucket> buckets;

    public LoginRateLimiter() {

        this.buckets = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(Duration.ofMinutes(10))
                .build();
    }

    private Bucket createBucket() {

        Bandwidth limit = Bandwidth.classic(
                5,
                Refill.intervally(5, Duration.ofMinutes(1))
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public boolean isAllowed(String ipAddress) {

        Bucket bucket = buckets.get(
                ipAddress,
                key -> createBucket()
        );

        return bucket.tryConsume(1);
    }
}
