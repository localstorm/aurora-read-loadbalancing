package aws.aurora.common;

import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RWCounters {
    private final ConcurrentHashMap<String, AtomicLong> serverCounters = new ConcurrentHashMap<>();

    public void printStats() {
        for (Map.Entry<String, AtomicLong> counterEntry: serverCounters.entrySet()) {
            System.out.println(pad(counterEntry.getKey(), 15) + "\t: " + counterEntry.getValue().get());
        }
        System.out.println("-------------");
    }

    private String pad(String key, int len) {
        if (key.length() < len) {
            return  key + dupe(" ", len - key.length());
        }
        return key;
    }

    private String dupe(String fill, int count) {
        StringBuilder sb = new StringBuilder(fill.length() * count);
        for (int i=0; i<count ; i++) {
            sb.append(fill);
        }
        return sb.toString();
    }

    public void increment(String server) {
        AtomicLong old = serverCounters.putIfAbsent(server, new AtomicLong(1));
        if (old != null) {
            old.incrementAndGet();
        }
    }
}
