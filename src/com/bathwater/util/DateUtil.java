/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author rajeshk
 */
public class DateUtil {

    public static int getWeekOfTheYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static Map<String, String> getDatesForWeek(int week) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        Map<String, String> dateMap = new LinkedHashMap<>(7);
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.WEEK_OF_YEAR, week);

        for (int i = 1; i <= 7; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, i);
            dateMap.put(days[i - 1], formatter.format(calendar.getTime()));
        }

        return dateMap;
    }

}
