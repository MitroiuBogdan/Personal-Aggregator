package com.stocks.aggregator.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.stocks.aggregator.model.DayTradeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class InfluxDBService {

    private static final Logger logger = Logger.getLogger(InfluxDBService.class.getName());
    private final InfluxDBClient influxDBClient;

    @Autowired
    public InfluxDBService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    private Instant toInstant(LocalDate date) {
        return date.atTime(LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC);
    }

    private Point toPoint(DayTradeStatus dayTradeStatus) {
        Instant instant = toInstant(dayTradeStatus.getDate());
        Point point = Point.measurement("day_trade_status")
                .addTag("source", "aggregator")
                .time(instant, WritePrecision.S);

        List.of(dayTradeStatus.getClass().getDeclaredFields()).stream()
                .filter(field -> Double.class.isAssignableFrom(field.getType()) || Long.class.isAssignableFrom(field.getType()))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(dayTradeStatus);
                        if (value != null) {
                            if (value instanceof Double d) point.addField(field.getName(), d);
                            if (value instanceof Long l) point.addField(field.getName(), l);
                        }
                    } catch (IllegalAccessException e) {
                        logger.warning(() -> "Failed to map field: " + field.getName() + " - " + e.getMessage());
                    }
                });

        return point;
    }

    public void writeProfitData(DayTradeStatus dayTradeStatus) {
        logger.info(() -> "Writing single profit data: " + dayTradeStatus);
        influxDBClient.getWriteApi().writePoint(toPoint(dayTradeStatus));
    }

    public void writeBatchData(List<DayTradeStatus> dayTradeStatuses) {
        logger.info(() -> "Writing batch profit data: " + dayTradeStatuses.size() + " entries");
        List<Point> points = dayTradeStatuses.stream()
                .map(this::toPoint)
                .peek(point -> logger.info(() -> "Point: " + point.toLineProtocol()))
                .collect(Collectors.toList());

        influxDBClient.getWriteApi().writePoints(points);
    }
}
