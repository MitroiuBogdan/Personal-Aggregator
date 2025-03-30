package com.stocks.aggregator.utils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class MathUtils {

    public static  <T> void updateCumulativeAverage(List<T> records,
                                             Function<T, Double> valueGetter,
                                             BiConsumer<T, Double> valueSetter) {
        double[] cumulativeValue = {0.0};

        IntStream.range(0, records.size()).forEach(i -> {
            T record = records.get(i);

            // Update cumulative value
            Double value = valueGetter.apply(record);
            cumulativeValue[0] += value != null ? value : 0.0;

            // Calculate average up to this point
            double average = cumulativeValue[0] / (i + 1);

            // Round to 2 decimal places and set the value
            valueSetter.accept(record, roundToNDecimalPlaces(average, 2));
        });
    }

    private  static double roundToNDecimalPlaces(double value, int places) {
        return Math.round(value * Math.pow(10, places)) / Math.pow(10, places);
    }


}
