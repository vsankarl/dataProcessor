package com.demo.matcher;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Util {

    public static String[] tokenize(String input) {
        String[] output = input.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        return output;
    }

    public static int extractYearFromDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            return dateTime.getYear();
        } catch (DateTimeException e) {
            // Commenting to avoid log noise
            //LOGGER.error("Incorrect datetime format {}", date);
        }
        return 0;
    }

    public static int getIntOrDefault(String num, int result) {
        try {
            int value = Integer.parseInt(num);
            return value;
        } catch (NumberFormatException e) {
            // Commenting to avoid log noise
            //LOGGER.error("Incorrect number format {}", num);
        }
        return result;
    }
}
