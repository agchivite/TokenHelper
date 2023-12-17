package dev.sbytmacke.tokenhelper.utils;

import java.util.Comparator;

public class DayHourComparator implements Comparator<String> {
    @Override
    public int compare(String dayHour1, String dayHour2) {
        String[] parts1 = dayHour1.split(" de ");
        String[] parts2 = dayHour2.split(" de ");

        int dayOfWeek1 = getDayOrder(parts1[0]);
        int dayOfWeek2 = getDayOrder(parts2[0]);

        int dayComparison = Integer.compare(dayOfWeek1, dayOfWeek2);

        if (dayComparison == 0) {
            // Si los días son iguales, comparar por horas
            String[] timeRange1 = parts1[1].split("-");
            String[] timeRange2 = parts2[1].split("-");

            // Comparar por la primera hora del rango
            return timeRange1[0].compareTo(timeRange2[0]);
        }

        return dayComparison;
    }

    private int getDayOrder(String dayOfWeekString) {
        // Extraer el día de la semana de la cadena completa
        String dayOfWeek = dayOfWeekString.split("-")[1].trim();

        // Remover cualquier texto adicional después del día de la semana
        dayOfWeek = dayOfWeek.split(" ")[0].trim();

        switch (dayOfWeek) {
            case "LUNES":
                return 1;
            case "MARTES":
                return 2;
            case "MIÉRCOLES":
                return 3;
            case "JUEVES":
                return 4;
            case "VIERNES":
                return 5;
            case "SÁBADO":
                return 6;
            case "DOMINGO":
                return 7;
            default:
                throw new IllegalArgumentException("Día de la semana no válido: " + dayOfWeek);
        }
    }
}
