package com.hjb.nice.server.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OtpStore {

    private static final long TTL_MS = 10 * 60 * 1000L;
    private static final SecureRandom RANDOM = new SecureRandom();

    private record Entry(String code, Instant expiry) {}

    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();

    public String generate(String email) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        store.put(email.toLowerCase(), new Entry(code, Instant.now().plusMillis(TTL_MS)));
        return code;
    }

    public boolean verify(String email, String code) {
        Entry entry = store.get(email.toLowerCase());
        if (entry == null || Instant.now().isAfter(entry.expiry())) {
            store.remove(email.toLowerCase());
            return false;
        }
        if (!entry.code().equals(code)) return false;
        store.remove(email.toLowerCase());
        return true;
    }

    @Scheduled(fixedDelay = 300_000)
    public void evictExpired() {
        Instant now = Instant.now();
        store.entrySet().removeIf(e -> now.isAfter(e.getValue().expiry()));
    }
}
