package com.spring.cloud.examples.gateway;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class ReactiveSessionDayOne {
    public static void main(String[] args) throws InterruptedException {
        ReactiveSessionDayOne reactiveSessionTest = new ReactiveSessionDayOne();
        /*reactiveSessionTest.backpressure();
        reactiveSessionTest.separator("buffer");
        reactiveSessionTest.buffer();
        reactiveSessionTest.separator("compose");
        reactiveSessionTest.compose();
        reactiveSessionTest.separator("concat");
        reactiveSessionTest.concat();
        reactiveSessionTest.separator("flatmap");
        reactiveSessionTest.flatmap();
        reactiveSessionTest.separator("grouby");
        reactiveSessionTest.grouby();
        reactiveSessionTest.separator("limitrate");
        reactiveSessionTest.limitrate();
        reactiveSessionTest.separator("limitrequest");
        reactiveSessionTest.limitrequest();
        reactiveSessionTest.separator("elapsed");
        reactiveSessionTest.elaspsed();*/

        reactiveSessionTest.separator("custom subscriber");
        reactiveSessionTest.customSubscriber();
        reactiveSessionTest.separator("parallel");
        reactiveSessionTest.parallel();
        reactiveSessionTest.separator("parallel-flux");
        reactiveSessionTest.parallelFlux();
        reactiveSessionTest.separator("connectableFluxOperator");
        reactiveSessionTest.connectableFluxOperator();
        reactiveSessionTest.separator("distinctUntilChange");
        reactiveSessionTest.distinctUntilChange();
        reactiveSessionTest.separator("reduce");
        reactiveSessionTest.reduce();
        reactiveSessionTest.separator("scan");
        reactiveSessionTest.scan();
        reactiveSessionTest.separator("transform");
        reactiveSessionTest.transform();
        reactiveSessionTest.separator("subscribeOn");
        reactiveSessionTest.subscribeOn();
        reactiveSessionTest.separator("publishOn");
        reactiveSessionTest.publishOn();
        reactiveSessionTest.separator("end");

        Thread.sleep(3_000);
    }

    private void publishOn() {
        Consumer<Integer> consumer = s -> System.out.println(s + " : " + Thread.currentThread().getName());

        Flux.range(1, 5)
                .map(i -> i + 2)
                .doOnNext(consumer)
                .publishOn(Schedulers.newElastic("First_PublishOn()_thread"))
                .doOnNext(consumer)
                .map(s -> s + 2)
                .subscribe(System.out::println);
    }

    private void subscribeOn() {
        Consumer<Integer> consumer = s -> System.out.println(s + " : " + Thread.currentThread().getName());

        Flux.range(1, 5)
                .subscribeOn(Schedulers.newElastic("subscribeOn: "))
                .doOnNext(consumer)
                .map(i -> i + 1)
                .publishOn(Schedulers.newElastic("publishOn: "))
                .doOnNext(consumer)
                .map(s -> s + 5)
                .subscribe(System.out::println);

    }

    private void transform() {
        Function<Flux<String>, Flux<String>> filterAndMap =
                f -> f.filter(color -> !color.equals("orange"))
                        .map(String::toUpperCase);

        Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                .transform(filterAndMap)
                .subscribe(d -> System.out.println("MapAndFilter for: " + d));
    }

    private void scan() {
        Flux.range(1, 5)
                .scan(0, Integer::sum)
                .subscribe(result -> System.out.println("Result: " + result));
    }

    private void reduce() {
        Flux.range(1, 5)
                .reduce(0, Integer::sum)
                .subscribe(result -> System.out.println("Result: " + result));
    }

    private void distinctUntilChange() {
        Flux.just(1, 1, 2, 2, 3, 4, 4, 4, 5, 6, 2, 2, 3, 3, 3, 1, 1, 1)
                .distinctUntilChanged()
                .subscribe(System.out::println);
    }

    private void connectableFluxOperator() throws InterruptedException {
        System.out.println("connect");
        // connect() - starts subscription manually when N-number of subscribers has subscribed
        ConnectableFlux<Integer> source = Flux.range(1, 3)
                .doOnSubscribe(s -> System.out.println("Subscribed"))
                .publish();
        // ConnectableFlux<Integer> co = source.publish();
        source.subscribe(System.out::println);  // 1st subscriber
        source.subscribe(System.out::println);  // 2nd subscriber
        System.out.println("Ready");
        Thread.sleep(500);
        System.out.println("Connection");
        source.connect();    // start
        System.out.println("\nautoConnect");
        // autoConnect(n) - starts subscription automatically after N-number of subscribers
        Flux<Integer> source2 = Flux.range(1, 3)
                .doOnSubscribe(s -> System.out.println("Subscribed"));

        Flux<Integer> autoConnect = source2.publish().autoConnect(2);  // as soon as 2 subscribers have signed up - let's start!
        autoConnect.subscribe(System.out::println);
        System.out.println("first subscriber");    // 1st subscriber
        Thread.sleep(500);
        System.out.println("second subscriber");   // 2nd subscriber
        autoConnect.subscribe(System.out::println);
    }

    private void parallelFlux() {
        System.out.println("\nExample 1:");
        Flux.range(1, 10)
                .parallel(2)   // we explicitly specified 2 processes, instead of letting the program itself determine how many they need, depending on the processor cores
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));


        System.out.println("\nExample 2:");
        Flux.range(1, 10)
                .parallel(2)
                .runOn(Schedulers.parallel())   // here is the execution in two threads
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
    }

    private void parallel() {
        Flux.range(0, 100)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(s -> s + 1)
                .filter(f -> f <= 10)
                .subscribe(System.out::println);
    }

    private void customSubscriber() {
        Subscriber<String> subscriber = new Subscriber<String>() {
            volatile Subscription subscription;
            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                System.out.println("initial request for 1 element");
                subscription.request(1);
            }
            @Override
            public void onNext(String s) {
                System.out.println("onNext: {}" + s);
                System.out.println("requesting 1 more element");
                subscription.request(1);
            }
            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
            @Override
            public void onError(Throwable t) {
                System.out.println("onError: {}" + t.getMessage());
            }
        };
        Flux.just("One", "Two", "Java")
                .subscribe(subscriber);
    }


    private void separator(String str) {
        System.out.println("*************  "+ str + " *******************************");
    }

    private void backpressure() {
        Flux.range(1, 100)
                .subscribe(
                        System.out::println,
                        err -> {
                        },
                        () -> System.out.println("Done"),
                        subscription -> {
                            subscription.request(4);
                            subscription.cancel();
                        }
                );
    }

    private void buffer() {
        Flux.range(1, 13) .buffer(4)
                .subscribe(e -> System.out.println("onNext: " + e));
    }

    private void compose() {
        Function<Flux<String>, Flux<String>> filterAndMap =
                f -> f.filter(color -> !color.equals("orange"))
                        .map(String::toUpperCase);

        Flux<String> publisher = Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                .compose(filterAndMap);
        publisher.subscribe(d -> System.out.println("Subscriber 1: MapAndFilter for: " + d));
        publisher.subscribe(d -> System.out.println("Subscriber 2: MapAndFilter for: " + d));
    }

    private void concat() {
        Flux.concat(
                Flux.range(1, 3),
                Flux.range(4, 2),
                Flux.range(6, 5)
        ).subscribe(e ->    System.out.println("onNext: " + e));

        // Example 2
        System.out.println("\nExample 2");
        Flux<Integer> oddFlux = Flux.just(1, 3);
        Flux<Integer> evenFlux = Flux.just(2, 4);

        Flux.concat(evenFlux, oddFlux)
                .subscribe(value -> System.out.println("Outer: " + value));
    }

    private void flatmap() {
        // Example 1
        System.out.println("Example 1:");
        Flux.just("1,2,3", "4,5,6")
                .flatMap(i -> Flux.fromIterable(Arrays.asList(i.split(","))))
                .collect(Collectors.toList())
                .subscribe(System.out::println);



        // Example 2:
        System.out.println("\nExample 2:");
        Flux.range(1, 10)
                .flatMap(v -> {
                    if (v < 5) {
                        return Flux.just(v * v);
                    }
                    return Flux.error(new IOException("Error: "));
                })
                .subscribe(System.out::println, Throwable::printStackTrace);

        // Example 3:
        System.out.println("\nExample 3:");
        List<String> list = Arrays.asList("a", "b", "c", "d", "e", "f");

        Flux.fromIterable(list)
                .flatMap( s -> Flux.just(s + "x"))
                .collect(Collectors.toList())
                .subscribe(System.out::println);

        // Example 4:
        System.out.println("\nExample 4:");
        Flux.just("Hello", "world")
                .flatMap(s -> Flux.fromArray(s.split("")))
                .subscribe(System.out::println);
    }


    public void grouby() {
        // Example 1:
        System.out.println("Example 1:");
        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .groupBy(i -> i % 2 == 0 ? "even:" : "odd:")
                .concatMap(Flux::collectList)
                .subscribe(System.out::println);

        // Example 2:
        System.out.println("\nExample 2:");
        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .groupBy(i -> i % 2 == 0 ? "even:" : "odd:")
                .concatMap(g -> g.defaultIfEmpty(1)           // if empty groups, show them
                        .map(String::valueOf)                 // map to string
                        .startWith(g.key()))                  // start with the group's key
                .subscribe(System.out::println);


        // Example 3:
        System.out.println("\nExample 3:");
        Flux.just("Hello", "world")
                .map(String::toUpperCase)
                .flatMap(s -> Flux.fromArray(s.split("")))
                .groupBy(String::toString)
                .concatMap(Flux::collectList)
                .subscribe(System.out::println);
    }

    public void limitrate() {
        Flux.range(1, 10)
                .limitRate(5)
                .subscribe(System.out::println);
    }

    public void limitrequest() {
        System.out.println("Example 1:");
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .limitRequest(5)
                .take(10)
                .subscribe(System.out::println);

        System.out.println("\nExample 2:");
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .limitRequest(5)
                .take(3)
                .subscribe(System.out::println);

    }
    private void elaspsed() {
        Flux.range(0, 5)
                .elapsed()
                .subscribe(e -> System.out.println("Elapsed ms: " + e.getT1() + e.getT2()));
    }
}
