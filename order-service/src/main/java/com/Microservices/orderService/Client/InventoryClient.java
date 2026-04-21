package com.Microservices.orderService.Client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.Microservices.orderService.dto.InventoryRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service//Spring does:Creates this class object Stores it in container Makes it available for injection
public class InventoryClient {

    private static final Logger log =
            LoggerFactory.getLogger(InventoryClient.class);

    //private final RestTemplate restTemplate;

    private final WebClient inventoryWebClient;

//    public InventoryClient(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }

/*
* Spring looks for a bean named "inventoryWebClient"
Finds the one you created earlier
Injects it here

✔ @Qualifier ensures correct bean if multiple exist*/
    public InventoryClient(@Qualifier("inventoryWebClient") WebClient inventoryWebClient) {
        this.inventoryWebClient = inventoryWebClient;
    }

//    public void reserveInventory() {
//        log.info("Calling Inventory Service");
//
//        restTemplate.postForObject(
//                "http://localhost:8083/inventory/reserve",
//                null,
//                String.class
//        );
//
//        log.info("Inventory Service responded");
//    }


    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(
            name = "inventory",
            fallbackMethod = "inventoryFallback"
    )
    public void reserveInventory(InventoryRequest inventoryRequest) {
        log.info("Calling Inventory Service");

        inventoryWebClient
                .post()
                .uri("/api/v1/inventory/reserve")
                .bodyValue(inventoryRequest)
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("Inventory Service responded");
    }


    public  void inventoryFallback(InventoryRequest request, Throwable ex){
        throw new RuntimeException("Inventory failed, stopping order"); //degradded mode
    }



    public void releaseInventory(InventoryRequest request) {
        inventoryWebClient.post()
                .uri("/api/v1/inventory/release")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

        /*IMPORTATNT



-> SPRING WEB FRAMEWORK FOR BLOCKED COMMUNICATION
-> SPRING WEBFLUX FOR ASYNC COMMUNCICATION


      ->  WebClient  with block() flow
        the reactive pipeline executes only when block() called
        You send request using WebClient
    Netty registers this request in event loop
    Thread does NOT wait ❗
    That same thread can handle other requests
    When response arrives → event loop gets notified
    It picks the task and processes response
*
*
*
* COMPANIES USE(SPRING MVC(BLOCKING) + WEBCLIENT(REACTIVE))
*
* */


        /**
         **WebClient without block()
         *
         * OrderService thread
         *    ↓
         * calls WebClient
         *    ↓
         * pipeline created
         *    ↓
         * .block() called
         *    ↓
         * 👉 subscription triggered
         *    ↓
         * Netty sends request (event loop)
         *    ↓
         * ❗ YOUR thread WAITS here
         *    ↓
         * Response comes via event loop
         *    ↓
         * Result returned
         *    ↓
         * Thread continues
         *
         *
         *
         * APP BLOCKING
         * Still async internally
         */


        /*
        * 🔹 6. Key Concepts you MUST remember in webclient
✅ 1. Lazy execution
Nothing runs until subscribe()
This is core Reactor concept
✅ 2. Event loop
Few threads handle many requests
Threads are reused
✅ 3. Non-blocking
No waiting
Threads are always free
✅ 4. Callback style (but cleaner)

Instead of:
wait → get result

It is:

when result comes → process it
        *
        *
        *
        *
        *
        *
        *
        *
        *
        //CIRCUITBREAKER CONCEPTS:
👉
“Circuit breaker watches last 10 calls, and if 50% fail, it stops calling the service for 10 seconds before retrying.”

        * 1. slidingWindowType: COUNT_BASED
👉 Means:

Check last FIXED number of calls

✔ Not time-based
✔ Based on count

🔹 2. slidingWindowSize: 10

👉 Means:

Look at last 10 API calls

Example:

Call results: S F S F F S S F S F
🔹 3. failureRateThreshold: 50

👉 Means:

If 50% calls fail → open circuit

Example:

10 calls → 5 failed → 50% → OPEN ❌

✔ If failures < 50% → stay normal

🔹 4. waitDurationInOpenState: 10s

👉 Means:

When OPEN → don’t call service for 10 seconds

✔ After 10 sec → try again (test)

🔹 Put it all together (flow)
Step 1: Monitor last 10 calls
Step 2: If 5 or more fail → OPEN circuit
Step 3: Stop calling service for 10 sec goes to fallback
Step 4: After 10 sec → try again
🔹 States (very important)
🟢 CLOSED
Everything normal → calls allowed
🔴 OPEN
Too many failures → stop calls
🟡 HALF-OPEN
After 10 sec → try few calls
If success → CLOSED
If fail → OPEN again

🔹 Real-life example
Inventory service failing again & again

👉 Without circuit breaker:

Keep calling → system slow ❌

👉 With circuit breaker:

Stop calls → wait → try later ✔
🔹 Super short memory trick
10 calls → 50% fail → stop for 10 sec
        * */








}
