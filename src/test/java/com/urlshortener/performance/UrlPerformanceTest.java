package com.urlshortener.performance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UrlPerformanceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testShortenUrlPerformance() throws InterruptedException, ExecutionException {
        int totalRequests = 5;
        int concurrentThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentThreads);

        List<Callable<Long>> tasks = new ArrayList<>();
        for (int i = 0; i < totalRequests; i++) {
            tasks.add(() -> {
                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("longUrl", "https://performance-test.com");

                long start = System.currentTimeMillis();
                restTemplate.postForEntity("/api/url/shorten", map, String.class);
                return System.currentTimeMillis() - start;
            });
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Future<Long>> results = executor.invokeAll(tasks);
        stopWatch.stop();
        executor.shutdown();

        long totalMillis = 0;
        for (Future<Long> res : results) {
            totalMillis += res.get();
        }

        double average = (double) totalMillis / totalRequests;
        System.out.println("Promedio de respuesta: " + average + "ms");

        assertThat(average).isLessThan(500.0);
    }
}