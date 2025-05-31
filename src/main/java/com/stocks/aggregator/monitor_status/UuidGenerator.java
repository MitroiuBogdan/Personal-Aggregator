package com.stocks.aggregator.monitor_status;

import java.util.UUID;

public class UuidGenerator {
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
