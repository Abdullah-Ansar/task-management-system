
package com.example.taskmanager.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class LoginRateLimiterTest {

    private LoginRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new LoginRateLimiter();
    }

    @Test
    void shouldAllowFiveRequestsFromSameIp() {

        String ip = "127.0.0.1";

        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.isAllowed(ip));
        }
    }

    @Test
    void shouldBlockSixthRequestFromSameIp() {

        String ip = "127.0.0.1";

        for (int i = 0; i < 5; i++) {
            rateLimiter.isAllowed(ip);
        }

        assertFalse(rateLimiter.isAllowed(ip));
    }

    @Test
    void shouldMaintainSeparateLimitsForDifferentIps() {

        String ip1 = "127.0.0.1";
        String ip2 = "192.168.1.10";

        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.isAllowed(ip1));
        }

        assertFalse(rateLimiter.isAllowed(ip1));

        // Different IP should have its own bucket.
        assertTrue(rateLimiter.isAllowed(ip2));
    }

    @Test
    void shouldAllowOnlyFiveConcurrentRequestsFromSameIp()
            throws InterruptedException, ExecutionException {

        String ip = "127.0.0.1";

        int totalRequests = 50;
        int threadCount = 20;

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        CountDownLatch startSignal = new CountDownLatch(1);

        try {
            List<Future<Boolean>> results = new ArrayList<>();

            for (int i = 0; i < totalRequests; i++) {

                Future<Boolean> result = executor.submit(() -> {
                    startSignal.await();
                    return rateLimiter.isAllowed(ip);
                });

                results.add(result);
            }

            // Release all requests at approximately the same time.
            startSignal.countDown();

            int allowedRequests = 0;

            for (Future<Boolean> result : results) {
                if (result.get()) {
                    allowedRequests++;
                }
            }

            assertEquals(5, allowedRequests);

        } finally {
            executor.shutdownNow();
        }
    }

}
