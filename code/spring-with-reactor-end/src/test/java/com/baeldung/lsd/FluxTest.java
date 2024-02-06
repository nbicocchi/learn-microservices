package com.baeldung.lsd;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxTest {
    @Test
    public void justFlux() {
        // Flux
        Flux<String> simpleFlux = Flux.just("hello", "there").log();

        // Subscriber
        simpleFlux.subscribe(val -> System.out.println(val));

        //Test code
        StepVerifier.create(simpleFlux)
                .expectNext("hello")
                .expectNext("there")
                .verifyComplete();
    }

    @Test
    public void subscribeFlux() {
        Flux<String> messages = Flux.just("Welcome", "to", "Jstobigdata").log();

        messages.subscribe(new Subscriber<String>() {

            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext: " + s);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("====== Execution Completed ======");
            }
        });

        StepVerifier.create(messages)
                .expectNext("Welcome")
                .expectNext("to")
                .expectNext("Jstobigdata")
                .verifyComplete();
    }

    @Test
    public void emptyFlux() {
        Flux emptyFlux = Flux.empty().log();

        emptyFlux.subscribe(
                val -> {
                    System.out.println("=== Never called ===");
                },
                error -> {
                    //Error
                },
                () -> {
                    System.out.println("==== Completed ===");
                });

        StepVerifier.create(emptyFlux)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void neverFlux() {
        Flux neverFlux = Flux.never().log();

        StepVerifier.create(neverFlux)
                .expectTimeout(Duration.ofSeconds(2))
                .verify();
    }

    @Test
    public void handleError() {
        Flux<String> names = Flux.just("Tom", "Jerry")
                .concatWith(Flux.error(new RuntimeException("Exception occurred..!")))
                .concatWith(Flux.just("John"));

        names.subscribe(
                name -> {
                    System.out.println(name);
                }, err -> {
                    err.printStackTrace();
                }
        );

        StepVerifier.create(names)
                .expectNext("Tom")
                .expectNext("Jerry")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void rangeFlux() {
        Flux<Integer> range = Flux.range(10, 10)
                .filter(num -> Math.floorMod(num, 2) == 1)
                .log();

        range.subscribe(System.out::println);

        StepVerifier.create(range)
                //.expectNextCount(5)
                .expectNext(11, 13, 15, 17, 19)
                .verifyComplete();
    }

    @Test
    public void transformMap(){
        List<String> names = Arrays.asList("google", "abc", "fb", "stackoverflow");
        Flux<String> flux = Flux.fromIterable(names)
                .filter(name -> name.length() > 5)
                .map(name -> name.toUpperCase())
                .repeat(1) // Just repeat once
                .log();

        StepVerifier.create(flux)
                .expectNext("GOOGLE", "STACKOVERFLOW", "GOOGLE", "STACKOVERFLOW")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap(){
        List<String> names = Arrays.asList("google", "abc", "fb", "stackoverflow");
        Flux<String> flux = Flux.fromIterable(names)
                .filter(name -> name.length() > 5)
                .flatMap(name -> {
                    return Mono.just(name.toUpperCase());
                })
                .repeat(1) //Just repeat once
                .log();

        StepVerifier.create(flux)
                .expectNext("GOOGLE", "STACKOVERFLOW", "GOOGLE", "STACKOVERFLOW")
                .verifyComplete();
    }

    @Test
    public void combineUsingMerge(){
        Flux<String> names1 = Flux.just("Blenders", "Old", "Johnnie");
        Flux<String> names2 = Flux.just("Pride", "Monk", "Walker");
        Flux<String> names = Flux.merge(names1, names2).log();

        StepVerifier.create(names)
                .expectSubscription()
                .expectNext("Blenders", "Old", "Johnnie", "Pride", "Monk", "Walker")
                .verifyComplete();
    }

    @Test
    public void mergeWithDelay(){
        Flux<String> names1 = Flux.just("Blenders", "Old", "Johnnie")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> names2 = Flux.just("Pride", "Monk", "Walker")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> names = Flux.merge(names1, names2).log();

        StepVerifier.create(names)
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public  void concatWithDelay(){
        Flux<String> names1 = Flux.just("Blenders", "Old", "Johnnie")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> names2 = Flux.just("Pride", "Monk", "Walker")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> names = Flux.concat(names1, names2)
                .log();
        StepVerifier.create(names)
                .expectSubscription()
                .expectNext("Blenders", "Old", "Johnnie", "Pride", "Monk", "Walker")
                .verifyComplete();
    }

    @Test
    public void combineWithZip(){
        Flux<String> names1 = Flux.just("Blenders", "Old", "Johnnie")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> names2 = Flux.just("Pride", "Monk", "Walker")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> names = Flux.zip(names1, names2, (n1, n2) -> {
            return n1.concat(" ").concat(n2);
        }).log();

        StepVerifier.create(names)
                .expectNext("Blenders Pride", "Old Monk", "Johnnie Walker")
                .verifyComplete();
    }

    @Test
    public void subscriptionRequest() {
        Flux<Integer> range = Flux.range(1, 100);

        //gets only 10 elements
        range.subscribe(
                value -> System.out.println(value),
                err -> err.printStackTrace(),
                () -> System.out.println("==== Completed ===="),
                subscription -> subscription.request(10)
        );
    }

    @Test
    public void cancelCallback() {
        Flux<Integer> range = Flux.range(1, 100).log() ;
        range.doOnCancel(() -> {
            System.out.println("===== Cancel method invoked =======");
        }).doOnComplete(() -> {
            System.out.println("==== Completed ====");
        }).subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value){
                try{
                    Thread.sleep(500);
                    request(1); //request next element
                    System.out.println(value);
                    if (value == 5) {
                        cancel();
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });

        StepVerifier.create(range)
                .expectNext(1, 2, 3, 4, 5)
                .thenCancel()
                .verify();
    }

    @Test
    public void backpressureVerifier() {
        Flux data = Flux.just(101, 201, 301).log();
        StepVerifier.create(data)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(101)
                .thenRequest(2)
                .expectNext(201, 301)
                .verifyComplete();
    }

}