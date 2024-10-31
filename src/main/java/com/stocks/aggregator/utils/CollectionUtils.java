package com.stocks.aggregator.utils;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {

    public static List<List<String[]>> splitIntoBatches(List<String[]> records, int batchSize) {
        List<List<String[]>> batches = new ArrayList<>();

        for (int i = 0; i < records.size(); i += batchSize) {
            int end = Math.min(i + batchSize, records.size());
            batches.add(records.subList(i, end));
        }
        return batches;
    }
}
