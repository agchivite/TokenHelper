package dev.sbytmacke.tokenhelper.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeUtils {
    public static List<String> getAllSliceHours() {
        List<String> sliceHours = new ArrayList<>();
        LocalTime startTime = LocalTime.of(0, 1);

        while (true) {
            LocalTime endTime = startTime.plusMinutes(59);
            String timeRange = startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "-" +
                    endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            sliceHours.add(timeRange);

            if (startTime.equals(LocalTime.of(23, 1))) {
                break;
            }

            startTime = endTime.plusMinutes(1);

            if (startTime.equals(LocalTime.of(0, 0))) {
                break;
            }
        }

        return sliceHours;
    }
}
