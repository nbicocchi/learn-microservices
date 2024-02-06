package com.baeldung.lsd;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Supplier;

public class MonoTest {
    @Test
    public void simpleMono(){
        Mono<String> message = Mono.just("Hello")
                .map(msg -> msg.concat(" World"))
                .log();
        message.subscribe(System.out::println);

        StepVerifier.create(message)
                .expectNext("Hello World")
                .verifyComplete();
    }

    @Test
    public void subscribeMono() {
        Mono<String> message = Mono.just("Welcome to Jstobigdata")
                .map(msg -> msg.concat(".com")).log();

        message.subscribe(new Subscriber<String>() {
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

        StepVerifier.create(message)
                .expectNext("Welcome to Jstobigdata.com")
                .verifyComplete();
    }

    @Test
    public void emptyMono() {
        Mono empty =  Mono.empty().log();

        empty.subscribe(val -> {
            System.out.println("==== Value ====" + val);
        }, err -> {
        }, ()->{
            System.out.println("====== On Complete Invoked ======");
        });

        StepVerifier.create(empty)
                .expectNextCount(0) //optional
                .verifyComplete();
    }

    @Test
    public void noSignalMono() {
        Mono<String> noSignal = Mono.never();

        //Can not use - Mono.never().log()
        noSignal.subscribe(val -> {
            System.out.println("==== Value ====" + val);
        });

        StepVerifier.create(noSignal)
                .expectTimeout(Duration.ofSeconds(5))
                .verify();
    }

    @Test
    public void subscribeMono3() {
        Mono<String> message = Mono.error(new RuntimeException("Check error mono"));

        message.subscribe(
                value -> {
                    System.out.println(value);
                },
                err -> {
                    err.printStackTrace();
                },
                () -> {
                    System.out.println("===== Execution completed =====");
                });

        StepVerifier.create(message)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fromSupplier() {
        Supplier<String> stringSupplier = () -> "Sample Message";
        Mono<String> sMono = Mono.fromSupplier(stringSupplier).log();

        sMono.subscribe(System.out::println);

        StepVerifier.create(sMono)
                .expectNext("Sample Message")
                .verifyComplete();
    }

    @Test
    public void filterMono(){
        Supplier<String> stringSupplier = ()-> "Hello World";
        Mono<String> filteredMono = Mono.fromSupplier(stringSupplier)
                .filter(str ->str.length()>5)
                .log();

        filteredMono.subscribe(System.out::println);

        StepVerifier.create(filteredMono)
                .expectComplete();
    }
}