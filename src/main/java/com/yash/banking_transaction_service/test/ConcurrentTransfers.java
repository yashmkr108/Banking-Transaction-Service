package com.yash.banking_transaction_service.test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentTransfers {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {

        int requestCount = 200;

        ExecutorService executor =
                Executors.newFixedThreadPool(requestCount);

        CountDownLatch startSignal = new CountDownLatch(1);

        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < requestCount; i++) {

            int requestNumber = i;

            futures.add(executor.submit(() -> {

                // Wait until all threads are ready
                startSignal.await();

                String idempotencyKey =
                        UUID.randomUUID().toString();

                String json = """
                        {
                            "sourceAccountNumber": "100000000001",
                            "destinationAccountNumber": "100000000002",
                            "amount": 200.00
                        }
                        """.formatted(requestNumber);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(
                                "http://localhost:8080/api/transfer"
                        ))
                        .header("Content-Type", "application/json")
                        .header("Idempotency-Key", idempotencyKey)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response =
                        CLIENT.send(
                                request,
                                HttpResponse.BodyHandlers.ofString()
                        );

                return "Request " + requestNumber
                        + " → "
                        + response.statusCode()
                        + " → "
                        + response.body();
            }));
        }

        // Release all requests at approximately the same time
        startSignal.countDown();

        for (Future<String> future : futures) {
            System.out.println(future.get());
        }

        executor.shutdown();
    }
}
