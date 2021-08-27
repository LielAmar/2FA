package com.lielamar.auth.shared.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static final long MILLISECONDS_IN_SECOND = 1000;
    private static final long MILLISECONDS_IN_MINUTE = 60000;
    private static final long MILLISECONDS_IN_HOUR = 3600000;
    private static final long MILLISECONDS_IN_DAY = 86400000;
    private static final long MILLISECONDS_IN_WEEK = 604800000;
    private static final long MILLISECONDS_IN_MONTH = 2592000000L;
    private static final long MILLISECONDS_IN_YEAR = 31536000000L;

    /**
     * Returns the current date
     *
     * @param pattern   Pattern to use
     * @return          Current date (String Format)
     */
    public static String getDate(String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(LocalDateTime.now());
    }

    /**
     * Parses time given in string into milliseconds as long
     * Example: 1y2m -> 100000
     *
     * @param time        Time to parse
     * @return            Parsed time as long
     */
    public static long parseTime(String time) {
        long milliseconds = 0;

        char[] timeArray = time.toCharArray();

        for(int i = 0; i < timeArray.length; i++) {
            if(timeArray[i] == 's')
                milliseconds += parseSpecificTime(timeArray, i, MILLISECONDS_IN_SECOND);
            else if(timeArray[i] == 'm')
                milliseconds += parseSpecificTime(timeArray, i, MILLISECONDS_IN_MINUTE);
            else if(timeArray[i] == 'h')
                milliseconds += parseSpecificTime(timeArray, i, MILLISECONDS_IN_HOUR);
            else if(timeArray[i] == 'd')
                milliseconds += parseSpecificTime(timeArray, i, MILLISECONDS_IN_DAY);
            else if(timeArray[i] == 'w')
                milliseconds += parseSpecificTime(timeArray, i, MILLISECONDS_IN_WEEK);
            else if(timeArray[i] == 'M')
                milliseconds += parseSpecificTime(timeArray, i, MILLISECONDS_IN_MONTH);
            else if(timeArray[i] == 'y')
                milliseconds += parseSpecificTime(timeArray, i, MILLISECONDS_IN_YEAR);
        }

        return milliseconds;
    }

    /**
     * Util function to parse a specific time in string
     * Example: 1y -> 90000
     *
     * @param timeArray              Array of characters to parse
     * @param index                  Current index in the array
     * @param millisecondsModifier   Modifier to milliseconds - by how much does this function need to multiply the result to turn it to milliseconds
     * @return                       Parsed specific time as long
     */
    private static long parseSpecificTime(char[] timeArray, int index, long millisecondsModifier) {
        long milliseconds = 0;

        int j = index - 1;
        int counter = 1;

        while(j >= 0 && Character.isDigit(timeArray[j])) {
            milliseconds += counter * Integer.parseInt(timeArray[j] + "") * millisecondsModifier;
            counter = counter * 10;
            j--;
        }

        return milliseconds;
    }

    /**
     * Parses time given in milliseconds into text as string
     * Example: 100000 -> 1 year, 2 months etc.
     *
     * @param time        Time to parse
     * @return            Parsed time as string
     */
    public static String parseTime(long time) {
        if(time < 0)
            return "Forever";

        long years = time/MILLISECONDS_IN_YEAR;
        time -= MILLISECONDS_IN_YEAR * years;

        long months = time/MILLISECONDS_IN_MONTH;
        time -= MILLISECONDS_IN_MONTH * months;

        long weeks = time/MILLISECONDS_IN_WEEK;
        time -= MILLISECONDS_IN_WEEK * weeks;

        long days = time/MILLISECONDS_IN_DAY;
        time -= MILLISECONDS_IN_DAY * days;

        long hours = time/MILLISECONDS_IN_HOUR;
        time -= MILLISECONDS_IN_HOUR * hours;

        long minutes = time/MILLISECONDS_IN_MINUTE;
        time -= MILLISECONDS_IN_MINUTE * minutes;

        long seconds = time/MILLISECONDS_IN_SECOND;

        StringBuilder finalTime = new StringBuilder();
        if(years > 0) finalTime.append(years).append(" years, ");
        if(months > 0) finalTime.append(months).append(" months, ");
        if(weeks > 0) finalTime.append(weeks).append(" weeks, ");
        if(days > 0) finalTime.append(days).append(" days, ");
        if(hours > 0) finalTime.append(hours).append(" hours, ");
        if(minutes > 0) finalTime.append(minutes).append(" minutes, ");
        if(seconds > 0) finalTime.append(seconds).append(" seconds, ");

        return finalTime.substring(0, finalTime.length() - 2);
    }

    /**
     * Formats X seconds to the following format: XX:XX
     *
     * @param seconds        Seconds to format
     * @return               Time left in the XX:XX format
     */
    public static String formatSeconds(int seconds) {
        int minutes = seconds/60;
        seconds = seconds%60;

        String sMinutes = minutes + "";
        String sSeconds = seconds + "";

        if(minutes < 10) sMinutes = "0" + minutes;
        if(seconds < 10) sSeconds = "0" + seconds;

        return sMinutes + ":" + sSeconds;
    }
}