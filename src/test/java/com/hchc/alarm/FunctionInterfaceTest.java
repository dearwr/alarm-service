package com.hchc.alarm;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;

/**
 * @author wangrong
 * @date 2020-09-25
 */
public class FunctionInterfaceTest {

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {
        say("hello", s -> s + " world");
    }

    @FunctionalInterface
    interface GreetingService<T>
    {
        String addString(T t);

        default void defaultMethod() {
            System.out.println("hello");
        }
    }

    public static void say(String preString, GreetingService<String> service) {
        service.defaultMethod();
        System.out.println("hello world".equals(service.addString(preString)));
    }

    public static void eval(List<Integer> list, Predicate<Integer> predicate) {
        for(Integer n: list) {
            if(predicate.test(n)) {
                System.out.println(n + " ");
            }
        }
    }

    public void test2() {
        CountDownLatch latch = new CountDownLatch(4);
        latch.countDown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
