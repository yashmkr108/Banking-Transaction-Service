package com.yash.banking_transaction_service.test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentTransferExecution {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {

        int requestCount = 2;

        String reference = "TRF-20260924-9924C4B3F46E49A29A6E1AE7BE19FE77";

        ExecutorService executor =
                Executors.newFixedThreadPool(requestCount);

        CountDownLatch startSignal = new CountDownLatch(1);

        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < requestCount; i++) {

            int requestNumber = i;

            futures.add(executor.submit(() -> {

                // Wait until both threads are ready
                startSignal.await();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(
                                "http://localhost:8080/api/transfer/"
                                        + reference
                                        + "/execute"
                        ))
                        .POST(HttpRequest.BodyPublishers.noBody())
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

        // Release both requests together
        startSignal.countDown();

        for (Future<String> future : futures) {
            System.out.println(future.get());
        }

        executor.shutdown();
    }
}