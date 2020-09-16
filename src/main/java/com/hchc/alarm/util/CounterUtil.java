package com.hchc.alarm.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jwing on 08/11/2017.
 */
public class CounterUtil {
    private static final ConcurrentHashMap<String, AtomicInteger> CLUSTER = new ConcurrentHashMap<>();

    private static AtomicInteger fetchCounter(String key){
        AtomicInteger counter = CLUSTER.get(key);
        if (counter != null){
            return counter;
        }
        counter = new AtomicInteger();
        AtomicInteger finalCounter = CLUSTER.putIfAbsent(key, counter);
        if (finalCounter == null){
            finalCounter = counter;
        }
        return finalCounter;
    }

    public static int getCounter(String key, int max){
        AtomicInteger counter = fetchCounter(key);
        return getCounter(counter, max);
    }

    public static int getCounter(AtomicInteger c, int max){
        if (c.get() < max){
            int curr = c.incrementAndGet();
            if (curr < max){
                return curr;
            }
            if (curr == max){//curr == max
                c.set(1);
                return 1;
            }
        }
        if (c.get() == max){
            throw new RuntimeException("unreach code on get counter algorithm");
        }
        while (c.get() > max){
            continue;
        }
        return getCounter(c, max);
    }
}
